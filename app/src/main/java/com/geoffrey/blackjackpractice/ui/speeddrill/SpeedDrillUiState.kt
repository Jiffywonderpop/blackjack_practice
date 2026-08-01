package com.geoffrey.blackjackpractice.ui.speeddrill

import com.geoffrey.blackjackpractice.domain.model.Card

data class SpeedDrillUiState(
    val sessionId: Long? = null,
    val isRunning: Boolean = false,
    val currentCard: Card? = null,
    val cardsShown: Int = 0,
    val totalCards: Int = 20,
    val flashIntervalMs: Long = 700,
    val isFinished: Boolean = false,
    val lastActualRunningCount: Int? = null,
    val lastGuessResult: String? = null,
    val elapsedMs: Long = 0,
    val isLoading: Boolean = true
)
