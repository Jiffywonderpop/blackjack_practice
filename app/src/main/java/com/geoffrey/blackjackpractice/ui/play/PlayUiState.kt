package com.geoffrey.blackjackpractice.ui.play

import com.geoffrey.blackjackpractice.domain.RoundState
import com.geoffrey.blackjackpractice.domain.model.TableRules

data class PlayUiState(
    val sessionId: Long? = null,
    val rules: TableRules = TableRules(),
    val bankroll: Double = 0.0,
    val betAmount: Double = 10.0,
    val handsPlayed: Int = 0,
    val roundState: RoundState? = null,
    val runningCount: Int = 0,
    val trueCount: Double = 0.0,
    val showLiveCount: Boolean = true,
    val awaitingCountGuess: Boolean = false,
    val lastDeviationFeedback: String? = null,
    val isLoading: Boolean = true
)
