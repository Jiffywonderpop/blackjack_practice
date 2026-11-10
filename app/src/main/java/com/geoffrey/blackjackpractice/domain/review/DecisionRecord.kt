package com.geoffrey.blackjackpractice.domain.review

import com.geoffrey.blackjackpractice.domain.model.DeviationClassification
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.PracticeMode

/**
 * One logged player decision (from Play or Strategy Quiz), compared against the
 * count-adjusted optimal action that was in effect at the time. Populated directly by a Room
 * projection over hand_logs joined with sessions — column aliases must match these names.
 */
data class DecisionRecord(
    val mode: PracticeMode,
    val timestamp: Long,
    val playerCards: String,
    val dealerUpcard: String,
    val playerHardTotal: Int,
    val isSoft: Boolean,
    val isPair: Boolean,
    val trueCount: Double,
    val actionTaken: HandAction,
    val optimalBasicAction: HandAction,
    val optimalFinalAction: HandAction,
    val classification: DeviationClassification
) {
    val isCorrect: Boolean get() = actionTaken == optimalFinalAction

    /** True when the count moved the optimal play away from basic strategy for this decision. */
    val countIndicatedDeviation: Boolean get() = optimalFinalAction != optimalBasicAction
}
