package com.geoffrey.blackjackpractice.domain.strategy

import com.geoffrey.blackjackpractice.domain.model.Card
import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.HandAction.DOUBLE
import com.geoffrey.blackjackpractice.domain.model.HandAction.HIT
import com.geoffrey.blackjackpractice.domain.model.HandAction.SPLIT
import com.geoffrey.blackjackpractice.domain.model.HandAction.STAND
import com.geoffrey.blackjackpractice.domain.model.HandAction.SURRENDER
import com.geoffrey.blackjackpractice.domain.model.Rank
import com.geoffrey.blackjackpractice.domain.model.TableRules

/**
 * Computes the basic-strategy-optimal action for a hand, before any Illustrious 18 deviation
 * is applied. See [BasicStrategyTables] for the underlying chart and rule-overlay notes.
 */
object BasicStrategyEngine {

    fun getOptimalAction(hand: Hand, dealerUpcard: Card, rules: TableRules): HandAction {
        val col = BasicStrategyTables.dealerColumnIndex(dealerUpcard.rank)

        if (hand.isPair) {
            val pairRank = hand.cards[0].rank
            val table = if (rules.doubleAfterSplit) {
                BasicStrategyTables.pairSplitsWithDas
            } else {
                BasicStrategyTables.pairSplitsNoDas
            }
            table[pairRank]?.get(col)?.let { return it }
            // Falls through to hard/soft total lookup below (e.g. 4,4 / 5,5 / 10,10).
        }

        if (hand.isSoft) {
            val base = softRow(hand.bestTotal)?.get(col) ?: STAND
            return applyH17SoftOverride(hand.bestTotal, col, base, rules)
        }

        val base = hardRow(hand.hardTotal)?.get(col) ?: if (hand.hardTotal >= 17) STAND else HIT
        return applySurrenderAndH17HardOverride(hand.hardTotal, col, base, rules)
    }

    /**
     * Reduces the theoretical chart recommendation to a legal action for the current hand
     * state (e.g. Double/Surrender are only legal on an untouched two-card hand).
     */
    fun legalizedOptimalAction(
        hand: Hand,
        dealerUpcard: Card,
        rules: TableRules,
        canDouble: Boolean,
        canSurrender: Boolean
    ): HandAction {
        val ideal = getOptimalAction(hand, dealerUpcard, rules)
        return when {
            ideal == DOUBLE && !canDouble -> HIT
            ideal == SURRENDER && !canSurrender -> HIT
            ideal == SPLIT && hand.cards.size != 2 -> HIT
            else -> ideal
        }
    }

    private fun hardRow(total: Int): Array<HandAction>? = when (total) {
        9 -> BasicStrategyTables.hard9
        10 -> BasicStrategyTables.hard10
        11 -> BasicStrategyTables.hard11
        12 -> BasicStrategyTables.hard12
        13 -> BasicStrategyTables.hard13
        14 -> BasicStrategyTables.hard14
        15 -> BasicStrategyTables.hard15
        16 -> BasicStrategyTables.hard16
        else -> null
    }

    private fun softRow(total: Int): Array<HandAction>? = when (total) {
        13 -> BasicStrategyTables.soft13
        14 -> BasicStrategyTables.soft14
        15 -> BasicStrategyTables.soft15
        16 -> BasicStrategyTables.soft16
        17 -> BasicStrategyTables.soft17
        18 -> BasicStrategyTables.soft18
        19 -> BasicStrategyTables.soft19
        20 -> BasicStrategyTables.soft20
        else -> null
    }

    private val aceColumn = BasicStrategyTables.dealerColumnIndex(Rank.ACE)
    private val tenColumn = BasicStrategyTables.dealerColumnIndex(Rank.TEN)
    private val nineColumn = BasicStrategyTables.dealerColumnIndex(Rank.NINE)

    private fun applyH17SoftOverride(total: Int, col: Int, base: HandAction, rules: TableRules): HandAction {
        // Soft 18 vs Ace: Stand under S17, Hit under H17.
        if (total == 18 && col == aceColumn && rules.dealerHitsSoft17) return HIT
        return base
    }

    private fun applySurrenderAndH17HardOverride(total: Int, col: Int, base: HandAction, rules: TableRules): HandAction {
        var action = base

        // Hard 11 vs Ace: Hit under S17, Double under H17.
        if (total == 11 && col == aceColumn && rules.dealerHitsSoft17) {
            action = DOUBLE
        }

        if (rules.surrenderAllowed) {
            // Hard 16 vs 9, 10, Ace: surrender over hit.
            if (total == 16 && (col == nineColumn || col == tenColumn || col == aceColumn)) {
                action = SURRENDER
            }
            // Hard 15 vs 10: surrender over hit. Vs Ace: only surrenders under H17.
            if (total == 15 && col == tenColumn) {
                action = SURRENDER
            }
            if (total == 15 && col == aceColumn && rules.dealerHitsSoft17) {
                action = SURRENDER
            }
        }

        return action
    }
}
