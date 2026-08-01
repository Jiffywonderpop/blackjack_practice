package com.geoffrey.blackjackpractice.domain.strategy

import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.Rank

enum class ThresholdDirection {
    /** Deviation applies once the true count reaches or exceeds the threshold. */
    AT_OR_ABOVE,

    /** Deviation applies once the true count drops below the threshold (used by the negative-index plays). */
    BELOW
}

data class IndexPlay(
    val label: String,
    val matches: (Hand) -> Boolean,
    val dealerColumn: Int,
    val threshold: Int,
    val direction: ThresholdDirection,
    val deviationAction: HandAction
) {
    fun appliesAt(truncatedTrueCount: Int): Boolean = when (direction) {
        ThresholdDirection.AT_OR_ABOVE -> truncatedTrueCount >= threshold
        ThresholdDirection.BELOW -> truncatedTrueCount < threshold
    }
}

/**
 * Don Schlesinger's "Illustrious 18" index plays (calibrated for a 6-deck, S17 game — see
 * app Settings for a note on other rule sets). Insurance is handled separately via
 * [shouldTakeInsurance] since it is a side-bet decision, not a hit/stand/double/split action.
 */
object Illustrious18Table {

    private fun col(rank: Rank) = BasicStrategyTables.dealerColumnIndex(rank)

    private fun isHardTotal(hand: Hand, total: Int): Boolean =
        !hand.isPair && !hand.isSoft && hand.hardTotal == total

    private fun isTenValuePair(hand: Hand): Boolean =
        hand.isPair && hand.cards[0].rank.blackjackValue == 10

    val indexPlays: List<IndexPlay> = listOf(
        IndexPlay("16 vs 10", { isHardTotal(it, 16) }, col(Rank.TEN), 0, ThresholdDirection.AT_OR_ABOVE, HandAction.STAND),
        IndexPlay("15 vs 10", { isHardTotal(it, 15) }, col(Rank.TEN), 4, ThresholdDirection.AT_OR_ABOVE, HandAction.STAND),
        IndexPlay("10,10 vs 5", ::isTenValuePair, col(Rank.FIVE), 5, ThresholdDirection.AT_OR_ABOVE, HandAction.SPLIT),
        IndexPlay("10,10 vs 6", ::isTenValuePair, col(Rank.SIX), 4, ThresholdDirection.AT_OR_ABOVE, HandAction.SPLIT),
        IndexPlay("10 vs 10", { isHardTotal(it, 10) }, col(Rank.TEN), 4, ThresholdDirection.AT_OR_ABOVE, HandAction.DOUBLE),
        IndexPlay("9 vs 2", { isHardTotal(it, 9) }, col(Rank.TWO), 1, ThresholdDirection.AT_OR_ABOVE, HandAction.DOUBLE),
        IndexPlay("9 vs 7", { isHardTotal(it, 9) }, col(Rank.SEVEN), 3, ThresholdDirection.AT_OR_ABOVE, HandAction.DOUBLE),
        IndexPlay("16 vs 9", { isHardTotal(it, 16) }, col(Rank.NINE), 5, ThresholdDirection.AT_OR_ABOVE, HandAction.STAND),
        IndexPlay("11 vs A", { isHardTotal(it, 11) }, col(Rank.ACE), 1, ThresholdDirection.AT_OR_ABOVE, HandAction.DOUBLE),
        IndexPlay("10 vs A", { isHardTotal(it, 10) }, col(Rank.ACE), 4, ThresholdDirection.AT_OR_ABOVE, HandAction.DOUBLE),
        IndexPlay("13 vs 2", { isHardTotal(it, 13) }, col(Rank.TWO), -1, ThresholdDirection.BELOW, HandAction.HIT),
        IndexPlay("12 vs 3", { isHardTotal(it, 12) }, col(Rank.THREE), 2, ThresholdDirection.AT_OR_ABOVE, HandAction.STAND),
        IndexPlay("12 vs 2", { isHardTotal(it, 12) }, col(Rank.TWO), 3, ThresholdDirection.AT_OR_ABOVE, HandAction.STAND),
        IndexPlay("13 vs 3", { isHardTotal(it, 13) }, col(Rank.THREE), -2, ThresholdDirection.BELOW, HandAction.HIT),
        IndexPlay("12 vs 4", { isHardTotal(it, 12) }, col(Rank.FOUR), 0, ThresholdDirection.BELOW, HandAction.HIT),
        IndexPlay("12 vs 5", { isHardTotal(it, 12) }, col(Rank.FIVE), -2, ThresholdDirection.BELOW, HandAction.HIT),
        IndexPlay("12 vs 6", { isHardTotal(it, 12) }, col(Rank.SIX), -1, ThresholdDirection.BELOW, HandAction.HIT)
    )

    fun findIndexPlay(hand: Hand, dealerColumn: Int): IndexPlay? =
        indexPlays.find { it.dealerColumn == dealerColumn && it.matches(hand) }

    fun shouldTakeInsurance(truncatedTrueCount: Int): Boolean = truncatedTrueCount >= 3
}
