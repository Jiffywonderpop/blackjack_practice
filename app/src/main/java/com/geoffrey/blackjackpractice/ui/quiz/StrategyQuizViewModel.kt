package com.geoffrey.blackjackpractice.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geoffrey.blackjackpractice.data.local.entity.HandLogEntity
import com.geoffrey.blackjackpractice.data.local.entity.SessionEntity
import com.geoffrey.blackjackpractice.data.repository.SessionRepository
import com.geoffrey.blackjackpractice.data.settings.AppSettings
import com.geoffrey.blackjackpractice.data.settings.RulesSettingsRepository
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.PracticeMode
import com.geoffrey.blackjackpractice.domain.strategy.DeviationEngine
import com.geoffrey.blackjackpractice.domain.strategy.DeviationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class StrategyQuizUiState(
    val sessionId: Long? = null,
    val question: QuizQuestion? = null,
    val questionsAnswered: Int = 0,
    val correctCount: Int = 0,
    val lastResult: DeviationResult? = null,
    val lastActionTaken: HandAction? = null,
    val isLoading: Boolean = true
)

class StrategyQuizViewModel(
    private val settingsRepository: RulesSettingsRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StrategyQuizUiState())
    val uiState: StateFlow<StrategyQuizUiState> = _uiState.asStateFlow()

    private var settings: AppSettings = AppSettings()

    init {
        viewModelScope.launch {
            settings = settingsRepository.settingsFlow.first()
            val sessionId = sessionRepository.startSession(
                SessionEntity(
                    mode = PracticeMode.QUIZ,
                    startTimestamp = System.currentTimeMillis(),
                    numDecks = settings.tableRules.numDecks,
                    penetrationPercent = settings.tableRules.penetrationPercent,
                    dealerHitsSoft17 = settings.tableRules.dealerHitsSoft17,
                    doubleAfterSplit = settings.tableRules.doubleAfterSplit,
                    surrenderAllowed = settings.tableRules.surrenderAllowed,
                    blackjackPayout = settings.tableRules.blackjackPayout,
                    startingBankroll = 0.0
                )
            )
            _uiState.value = StrategyQuizUiState(sessionId = sessionId, isLoading = false, question = QuizQuestionGenerator.next())
        }
    }

    fun answer(action: HandAction) {
        val state = _uiState.value
        val question = state.question ?: return

        val result = DeviationEngine.evaluate(
            hand = question.hand,
            dealerUpcard = question.dealerUpcard,
            truncatedTrueCount = question.truncatedTrueCount,
            rules = settings.tableRules,
            playerAction = action,
            canDouble = true,
            canSurrender = true
        )

        state.sessionId?.let { sid ->
            viewModelScope.launch {
                sessionRepository.logHand(
                    HandLogEntity(
                        sessionId = sid,
                        handIndexInSession = state.questionsAnswered,
                        timestamp = System.currentTimeMillis(),
                        playerCards = question.hand.cards.joinToString(",") { "${it.rank.label}${it.suit.name.first()}" },
                        dealerUpcard = "${question.dealerUpcard.rank.label}${question.dealerUpcard.suit.name.first()}",
                        playerHardTotal = question.hand.hardTotal,
                        isSoft = question.hand.isSoft,
                        isPair = question.hand.isPair,
                        runningCountAtDecision = question.truncatedTrueCount,
                        trueCountAtDecision = question.truncatedTrueCount.toDouble(),
                        actionTaken = action,
                        optimalBasicAction = result.optimalBasicAction,
                        optimalFinalAction = result.optimalFinalAction,
                        deviationClassification = result.classification,
                        betAmount = 0.0,
                        outcome = null,
                        payout = null,
                        bankrollAfter = null
                    )
                )
            }
        }

        val isCorrect = action == result.optimalFinalAction
        _uiState.value = state.copy(
            lastResult = result,
            lastActionTaken = action,
            questionsAnswered = state.questionsAnswered + 1,
            correctCount = state.correctCount + if (isCorrect) 1 else 0
        )
    }

    fun nextQuestion() {
        _uiState.value = _uiState.value.copy(question = QuizQuestionGenerator.next(), lastResult = null, lastActionTaken = null)
    }

    fun endSession() {
        val sessionId = _uiState.value.sessionId ?: return
        viewModelScope.launch {
            sessionRepository.getSession(sessionId)?.let {
                sessionRepository.endSession(it.copy(endTimestamp = System.currentTimeMillis()))
            }
        }
    }
}
