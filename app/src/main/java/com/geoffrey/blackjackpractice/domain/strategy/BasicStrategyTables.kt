package com.geoffrey.blackjackpractice.domain.strategy

import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.HandAction.DOUBLE
import com.geoffrey.blackjackpractice.domain.model.HandAction.HIT
import com.geoffrey.blackjackpractice.domain.model.HandAction.SPLIT
import com.geoffrey.blackjackpractice.domain.model.HandAction.STAND
import com.geoffrey.blackjackpractice.domain.model.Rank

/**
 * Static basic-strategy tables for a 4-8 deck, dealer-stands-on-soft-17, double-after-split
 * game (the standard reference chart most sources publish). [BasicStrategyEngine] applies
 * rule-specific overlays (H17, no-DAS, surrender) on top of these baseline rows.
 *
 * All rows are indexed by dealer upcard column 2,3,4,5,6,7,8,9,10,A -> index 0..9.
 */
object BasicStrategyTables {

    fun dealerColumnIndex(upcard: Rank): Int {
        val col = upcard.strategyColumn
        return if (col == 11) 9 else col - 2
    }

    // Hard totals 9-16 (<=8 always Hit, >=17 always Stand, handled in BasicStrategyEngine).
    val hard9 = arrayOf(HIT, DOUBLE, DOUBLE, DOUBLE, DOUBLE, HIT, HIT, HIT, HIT, HIT)
    val hard10 = arrayOf(DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, HIT, HIT)
    val hard11 = arrayOf(DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, HIT)
    val hard12 = arrayOf(HIT, HIT, STAND, STAND, STAND, HIT, HIT, HIT, HIT, HIT)
    val hard13 = arrayOf(STAND, STAND, STAND, STAND, STAND, HIT, HIT, HIT, HIT, HIT)
    val hard14 = arrayOf(STAND, STAND, STAND, STAND, STAND, HIT, HIT, HIT, HIT, HIT)
    val hard15 = arrayOf(STAND, STAND, STAND, STAND, STAND, HIT, HIT, HIT, HIT, HIT)
    val hard16 = arrayOf(STAND, STAND, STAND, STAND, STAND, HIT, HIT, HIT, HIT, HIT)

    // Soft totals 13-20 (A,2 .. A,9). Soft 21 is blackjack and never reaches strategy lookup.
    val soft13 = arrayOf(HIT, HIT, HIT, DOUBLE, DOUBLE, HIT, HIT, HIT, HIT, HIT)
    val soft14 = arrayOf(HIT, HIT, HIT, DOUBLE, DOUBLE, HIT, HIT, HIT, HIT, HIT)
    val soft15 = arrayOf(HIT, HIT, DOUBLE, DOUBLE, DOUBLE, HIT, HIT, HIT, HIT, HIT)
    val soft16 = arrayOf(HIT, HIT, DOUBLE, DOUBLE, DOUBLE, HIT, HIT, HIT, HIT, HIT)
    val soft17 = arrayOf(HIT, DOUBLE, DOUBLE, DOUBLE, DOUBLE, HIT, HIT, HIT, HIT, HIT)
    val soft18 = arrayOf(STAND, DOUBLE, DOUBLE, DOUBLE, DOUBLE, STAND, STAND, HIT, HIT, STAND)
    val soft19 = arrayOf(STAND, STAND, STAND, STAND, DOUBLE, STAND, STAND, STAND, STAND, STAND)
    val soft20 = arrayOf(STAND, STAND, STAND, STAND, STAND, STAND, STAND, STAND, STAND, STAND)

    /** Pair rows: SPLIT where basic strategy splits; null means "fall through to the hard/soft table". */
    val pairSplitsWithDas: Map<Rank, Array<HandAction?>> = mapOf(
        Rank.TWO to arrayOf(SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, null, null, null, null),
        Rank.THREE to arrayOf(SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, null, null, null, null),
        Rank.FOUR to arrayOf(null, null, null, SPLIT, SPLIT, null, null, null, null, null),
        Rank.FIVE to arrayOf(null, null, null, null, null, null, null, null, null, null),
        Rank.SIX to arrayOf(SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, null, null, null, null, null),
        Rank.SEVEN to arrayOf(SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, null, null, null, null),
        Rank.EIGHT to arrayOf(SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT),
        Rank.NINE to arrayOf(SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, null, SPLIT, SPLIT, null, null),
        Rank.TEN to arrayOf(null, null, null, null, null, null, null, null, null, null),
        Rank.ACE to arrayOf(SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT)
    )

    /** Same shape, for tables where double-after-split is NOT allowed (2,2 / 3,3 / 4,4 / 6,6 shrink). */
    val pairSplitsNoDas: Map<Rank, Array<HandAction?>> = mapOf(
        Rank.TWO to arrayOf(null, null, SPLIT, SPLIT, SPLIT, SPLIT, null, null, null, null),
        Rank.THREE to arrayOf(null, null, SPLIT, SPLIT, SPLIT, SPLIT, null, null, null, null),
        Rank.FOUR to arrayOf(null, null, null, null, null, null, null, null, null, null),
        Rank.FIVE to arrayOf(null, null, null, null, null, null, null, null, null, null),
        Rank.SIX to arrayOf(null, SPLIT, SPLIT, SPLIT, SPLIT, null, null, null, null, null),
        Rank.SEVEN to arrayOf(SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, null, null, null, null),
        Rank.EIGHT to arrayOf(SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT),
        Rank.NINE to arrayOf(SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, null, SPLIT, SPLIT, null, null),
        Rank.TEN to arrayOf(null, null, null, null, null, null, null, null, null, null),
        Rank.ACE to arrayOf(SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT, SPLIT)
    )
}
