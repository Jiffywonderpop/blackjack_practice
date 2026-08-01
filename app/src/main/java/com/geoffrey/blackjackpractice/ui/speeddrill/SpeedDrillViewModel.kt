package com.geoffrey.blackjackpractice.ui.speeddrill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geoffrey.blackjackpractice.data.local.entity.CountGuessEntity
import com.geoffrey.blackjackpractice.data.local.entity.SessionEntity
import com.geoffrey.blackjackpractice.data.repository.SessionRepository
import com.geoffrey.blackjackpractice.data.settings.RulesSettingsRepository
import com.geoffrey.blackjackpractice.domain.counting.HiLoCounter
import com.geoffrey.blackjackpractice.domain.model.PracticeMode
import com.geoffrey.blackjackpractice.domain.model.Shoe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SpeedDrillViewModel(
    private val settingsRepository: RulesSettingsRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpeedDrillUiState())
    val uiState: StateFlow<SpeedDrillUiState> = _uiState.asStateFlow()

    private lateinit var shoe: Shoe
    private val counter = HiLoCounter()
    private var startTimeMs = 0L

    init {
        viewModelScope.launch {
            val settings = settingsRepository.settingsFlow.first()
            shoe = Shoe(settings.tableRules)
            val sessionId = sessionRepository.startSession(
                SessionEntity(
                    mode = PracticeMode.SPEED_DRILL,
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
            _uiState.value = _uiState.value.copy(sessionId = sessionId, isLoading = false)
        }
    }

    fun configure(totalCards: Int, flashIntervalMs: Long) {
        if (_uiState.value.isRunning) return
        _uiState.value = _uiState.value.copy(totalCards = totalCards, flashIntervalMs = flashIntervalMs)
    }

    fun startDrill() {
        val state = _uiState.value
        if (state.isRunning || state.isLoading) return

        counter.reset()
        startTimeMs = System.currentTimeMillis()
        _uiState.value = state.copy(
            isRunning = true,
            isFinished = false,
            cardsShown = 0,
            currentCard = null,
            lastGuessResult = null
        )

        viewModelScope.launch {
            repeat(state.totalCards) { i ->
                if (shoe.cardsRemaining <= 0) shoe.reshuffle()
                val card = shoe.dealCard()
                counter.observe(card)
                _uiState.value = _uiState.value.copy(currentCard = card, cardsShown = i + 1)
                delay(_uiState.value.flashIntervalMs)
            }
            val elapsed = System.currentTimeMillis() - startTimeMs
            _uiState.value = _uiState.value.copy(
                isRunning = false,
                isFinished = true,
                currentCard = null,
                lastActualRunningCount = counter.runningCount,
                elapsedMs = elapsed
            )
        }
    }

    fun submitGuess(guess: Int) {
        val state = _uiState.value
        val actual = counter.runningCount
        state.sessionId?.let { sid ->
            viewModelScope.launch {
                sessionRepository.logCountGuess(
                    CountGuessEntity(
                        sessionId = sid,
                        mode = PracticeMode.SPEED_DRILL,
                        timestamp = System.currentTimeMillis(),
                        sequenceNumber = state.cardsShown,
                        actualRunningCount = actual,
                        actualTrueCount = actual.toDouble(),
                        guessedRunningCount = guess,
                        elapsedMs = state.elapsedMs,
                        cardsShownCount = state.cardsShown
                    )
                )
            }
        }
        val resultText = if (guess == actual) {
            "Correct! Running count was $actual."
        } else {
            "Off by ${kotlin.math.abs(guess - actual)}. Actual running count was $actual."
        }
        _uiState.value = state.copy(isFinished = false, lastGuessResult = resultText)
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
