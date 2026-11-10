package com.geoffrey.blackjackpractice.ui.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geoffrey.blackjackpractice.data.local.entity.CountGuessEntity
import com.geoffrey.blackjackpractice.data.local.entity.HandLogEntity
import com.geoffrey.blackjackpractice.data.local.entity.SessionEntity
import com.geoffrey.blackjackpractice.data.repository.SessionRepository
import com.geoffrey.blackjackpractice.data.settings.AppSettings
import com.geoffrey.blackjackpractice.data.settings.RulesSettingsRepository
import com.geoffrey.blackjackpractice.domain.GameEngine
import com.geoffrey.blackjackpractice.domain.RoundState
import com.geoffrey.blackjackpractice.domain.counting.HiLoCounter
import com.geoffrey.blackjackpractice.domain.model.BetSpreadMode
import com.geoffrey.blackjackpractice.domain.model.CardCodec
import com.geoffrey.blackjackpractice.domain.model.DeviationClassification
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.PracticeMode
import com.geoffrey.blackjackpractice.domain.model.Shoe
import com.geoffrey.blackjackpractice.domain.strategy.DeviationEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayViewModel(
    private val settingsRepository: RulesSettingsRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayUiState())
    val uiState: StateFlow<PlayUiState> = _uiState.asStateFlow()

    private lateinit var shoe: Shoe
    private lateinit var counter: HiLoCounter
    private lateinit var engine: GameEngine
    private var settings: AppSettings = AppSettings()
    private var handsSinceLastGuess = 0

    /** Row id of the most recent logged decision for each active hand index in the current round. */
    private val lastLogIdForHand = mutableMapOf<Int, Long>()

    init {
        viewModelScope.launch {
            settings = settingsRepository.settingsFlow.first()
            shoe = Shoe(settings.tableRules)
            counter = HiLoCounter()
            engine = GameEngine(settings.tableRules, shoe, counter)

            val sessionId = sessionRepository.startSession(
                SessionEntity(
                    mode = PracticeMode.GAME,
                    startTimestamp = System.currentTimeMillis(),
                    numDecks = settings.tableRules.numDecks,
                    penetrationPercent = settings.tableRules.penetrationPercent,
                    dealerHitsSoft17 = settings.tableRules.dealerHitsSoft17,
                    doubleAfterSplit = settings.tableRules.doubleAfterSplit,
                    surrenderAllowed = settings.tableRules.surrenderAllowed,
                    blackjackPayout = settings.tableRules.blackjackPayout,
                    startingBankroll = settings.startingBankroll
                )
            )

            _uiState.value = PlayUiState(
                sessionId = sessionId,
                rules = settings.tableRules,
                bankroll = settings.startingBankroll,
                betAmount = settings.flatBetAmount,
                showLiveCount = settings.showLiveCount,
                isLoading = false
            )
        }
    }

    fun dealRound() {
        val state = _uiState.value
        if (state.isLoading || state.roundState?.roundOver == false) return

        lastLogIdForHand.clear()
        val bet = currentBet()
        val round = engine.startRound(bet)
        val runningCountSnapshot = counter.runningCount
        val trueCountSnapshot = counter.trueCount(shoe.cardsRemaining)

        _uiState.value = state.copy(
            roundState = round,
            runningCount = runningCountSnapshot,
            trueCount = trueCountSnapshot
        )

        if (round.roundOver) {
            viewModelScope.launch { finishRoundBookkeeping(round, runningCountSnapshot, trueCountSnapshot) }
        }
    }

    private fun currentBet(): Double = if (settings.betSpreadMode == BetSpreadMode.FLAT) {
        settings.flatBetAmount
    } else {
        val tc = counter.truncatedTrueCount(shoe.cardsRemaining)
        settings.flatBetAmount * maxOf(1, tc + 1)
    }

    fun act(action: HandAction) {
        val state = _uiState.value
        val round = state.roundState ?: return
        val idx = round.activeHandIndex
        if (idx !in round.playerHands.indices) return

        val hand = round.playerHands[idx]
        val dealerUpcard = round.dealerHand.cards[0]
        val legal = engine.legalActions(round, idx)
        // Illegal presses (e.g. Split on a non-pair) are ignored rather than graded as a decision.
        if (action !in legal) return
        val tc = counter.truncatedTrueCount(shoe.cardsRemaining)

        val deviation = DeviationEngine.evaluate(
            hand = hand,
            dealerUpcard = dealerUpcard,
            truncatedTrueCount = tc,
            rules = settings.tableRules,
            playerAction = action,
            canDouble = HandAction.DOUBLE in legal,
            canSurrender = HandAction.SURRENDER in legal
        )

        val runningCountSnapshot = counter.runningCount
        val trueCountSnapshot = counter.trueCount(shoe.cardsRemaining)
        val newRound = engine.applyAction(round, action)

        _uiState.value = state.copy(
            roundState = newRound,
            runningCount = counter.runningCount,
            trueCount = counter.trueCount(shoe.cardsRemaining),
            lastDeviationFeedback = deviation.classification.name
        )

        viewModelScope.launch {
            val sid = state.sessionId
            if (sid != null) {
                val logId = sessionRepository.logHand(
                    HandLogEntity(
                        sessionId = sid,
                        handIndexInSession = state.handsPlayed + idx,
                        timestamp = System.currentTimeMillis(),
                        playerCards = CardCodec.encodeAll(hand.cards),
                        dealerUpcard = CardCodec.encode(dealerUpcard),
                        playerHardTotal = hand.hardTotal,
                        isSoft = hand.isSoft,
                        isPair = hand.isPair,
                        runningCountAtDecision = runningCountSnapshot,
                        trueCountAtDecision = trueCountSnapshot,
                        actionTaken = action,
                        optimalBasicAction = deviation.optimalBasicAction,
                        optimalFinalAction = deviation.optimalFinalAction,
                        deviationClassification = deviation.classification,
                        betAmount = hand.bet,
                        outcome = null,
                        payout = null,
                        bankrollAfter = null
                    )
                )
                lastLogIdForHand[idx] = logId
            }
            if (newRound.roundOver) {
                finishRoundBookkeeping(newRound, runningCountSnapshot, trueCountSnapshot)
            }
        }
    }

    /** Must run after any logHand() calls for this round have completed, so lastLogIdForHand is populated. */
    private suspend fun finishRoundBookkeeping(round: RoundState, dealRoundRunningCount: Int, dealRoundTrueCount: Double) {
        val state = _uiState.value
        val outcomes = round.handOutcomes ?: return
        val payouts = round.handPayouts ?: return
        val sid = state.sessionId

        var runningBankroll = state.bankroll
        round.playerHands.forEachIndexed { index, hand ->
            val payout = payouts.getOrElse(index) { 0.0 }
            runningBankroll += payout
            val outcome = outcomes.getOrNull(index) ?: return@forEachIndexed

            if (sid == null) return@forEachIndexed
            val existingLogId = lastLogIdForHand[index]
            if (existingLogId != null) {
                sessionRepository.updateHandOutcome(existingLogId, outcome, payout, runningBankroll)
            } else {
                // No decision was ever logged for this hand (e.g. an untouched natural blackjack).
                val dealerUpcard = round.dealerHand.cards[0]
                sessionRepository.logHand(
                    HandLogEntity(
                        sessionId = sid,
                        handIndexInSession = state.handsPlayed + index,
                        timestamp = System.currentTimeMillis(),
                        playerCards = CardCodec.encodeAll(hand.cards),
                        dealerUpcard = CardCodec.encode(dealerUpcard),
                        playerHardTotal = hand.hardTotal,
                        isSoft = hand.isSoft,
                        isPair = hand.isPair,
                        runningCountAtDecision = dealRoundRunningCount,
                        trueCountAtDecision = dealRoundTrueCount,
                        actionTaken = HandAction.STAND,
                        optimalBasicAction = HandAction.STAND,
                        optimalFinalAction = HandAction.STAND,
                        deviationClassification = DeviationClassification.CORRECT_PLAY,
                        betAmount = hand.bet,
                        outcome = outcome,
                        payout = payout,
                        bankrollAfter = runningBankroll,
                        isDecision = false
                    )
                )
            }
        }

        val totalPayout = payouts.sum()
        val newHandsPlayed = state.handsPlayed + round.playerHands.size
        handsSinceLastGuess += round.playerHands.size
        val shouldPromptGuess = handsSinceLastGuess >= settings.countGuessPromptIntervalHands

        _uiState.value = _uiState.value.copy(
            bankroll = state.bankroll + totalPayout,
            handsPlayed = newHandsPlayed,
            awaitingCountGuess = shouldPromptGuess
        )
        if (shouldPromptGuess) handsSinceLastGuess = 0
        lastLogIdForHand.clear()
    }

    fun submitCountGuess(guessedRunningCount: Int) {
        val state = _uiState.value
        state.sessionId?.let { sid ->
            viewModelScope.launch {
                sessionRepository.logCountGuess(
                    CountGuessEntity(
                        sessionId = sid,
                        mode = PracticeMode.GAME,
                        timestamp = System.currentTimeMillis(),
                        sequenceNumber = state.handsPlayed,
                        actualRunningCount = counter.runningCount,
                        actualTrueCount = counter.trueCount(shoe.cardsRemaining),
                        guessedRunningCount = guessedRunningCount
                    )
                )
            }
        }
        _uiState.value = state.copy(awaitingCountGuess = false)
    }

    fun endSession() {
        val state = _uiState.value
        state.sessionId?.let { sid ->
            viewModelScope.launch {
                sessionRepository.getSession(sid)?.let {
                    sessionRepository.endSession(
                        it.copy(endTimestamp = System.currentTimeMillis(), endingBankroll = state.bankroll)
                    )
                }
            }
        }
    }
}
