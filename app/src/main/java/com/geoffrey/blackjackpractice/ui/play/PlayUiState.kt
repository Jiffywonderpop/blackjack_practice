package com.geoffrey.blackjackpractice.ui.play

import com.geoffrey.blackjackpractice.domain.RoundState
import com.geoffrey.blackjackpractice.domain.model.BetSpreadMode
import com.geoffrey.blackjackpractice.domain.model.DeviationClassification
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.TableRules

/** Instant per-decision coaching shown as a toast over the table after each action. */
data class DecisionFeedback(
    val id: Int,
    val classification: DeviationClassification,
    val actionTaken: HandAction,
    val correctAction: HandAction
) {
    val isCorrect: Boolean
        get() = classification == DeviationClassification.CORRECT_PLAY ||
            classification == DeviationClassification.CORRECT_DEVIATION
}

data class PlayUiState(
    val sessionId: Long? = null,
    val rules: TableRules = TableRules(),
    val bankroll: Double = 0.0,
    /** Flat bet, or the betting unit when [betSpreadMode] scales the bet by true count. */
    val betAmount: Double = 10.0,
    val betSpreadMode: BetSpreadMode = BetSpreadMode.FLAT,
    val handsPlayed: Int = 0,
    /** Increments on every deal so the table can restart its dealing animations. */
    val roundNumber: Int = 0,
    val roundState: RoundState? = null,
    val legalActions: Set<HandAction> = emptySet(),
    val hintAction: HandAction? = null,
    val runningCount: Int = 0,
    val trueCount: Double = 0.0,
    val showLiveCount: Boolean = true,
    val cardsRemaining: Int = 0,
    val totalCards: Int = 0,
    val awaitingCountGuess: Boolean = false,
    val feedback: DecisionFeedback? = null,
    val isLoading: Boolean = true
)
