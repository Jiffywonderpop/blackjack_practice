package com.geoffrey.blackjackpractice.domain.strategy

import com.geoffrey.blackjackpractice.domain.model.Card
import com.geoffrey.blackjackpractice.domain.model.DeviationClassification
import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.TableRules

data class DeviationResult(
    val classification: DeviationClassification,
    val optimalBasicAction: HandAction,
    val optimalFinalAction: HandAction,
    val indexPlay: IndexPlay?
)

/**
 * Classifies a player's chosen action against basic strategy plus any applicable Illustrious 18
 * index play. Shared by silent in-game logging (Play mode) and the Strategy Quiz mode.
 */
object DeviationEngine {

    fun evaluate(
        hand: Hand,
        dealerUpcard: Card,
        truncatedTrueCount: Int,
        rules: TableRules,
        playerAction: HandAction,
        canDouble: Boolean = true,
        canSurrender: Boolean = true
    ): DeviationResult {
        val optimalBase = BasicStrategyEngine.legalizedOptimalAction(hand, dealerUpcard, rules, canDouble, canSurrender)

        val col = BasicStrategyTables.dealerColumnIndex(dealerUpcard.rank)
        val indexPlay = Illustrious18Table.findIndexPlay(hand, col)
        val deviationIndicated = indexPlay != null && indexPlay.appliesAt(truncatedTrueCount)

        val optimalFinal = if (deviationIndicated) {
            legalize(indexPlay!!.deviationAction, hand, canDouble, canSurrender)
        } else {
            optimalBase
        }

        val classification = when {
            playerAction == optimalFinal && deviationIndicated -> DeviationClassification.CORRECT_DEVIATION
            playerAction == optimalFinal -> DeviationClassification.CORRECT_PLAY
            deviationIndicated && playerAction == optimalBase -> DeviationClassification.MISSED_DEVIATION
            !deviationIndicated && indexPlay != null && playerAction == legalize(indexPlay.deviationAction, hand, canDouble, canSurrender) ->
                DeviationClassification.INCORRECT_DEVIATION
            else -> DeviationClassification.INCORRECT_PLAY
        }

        return DeviationResult(classification, optimalBase, optimalFinal, indexPlay)
    }

    private fun legalize(action: HandAction, hand: Hand, canDouble: Boolean, canSurrender: Boolean): HandAction = when {
        action == HandAction.DOUBLE && !canDouble -> HandAction.HIT
        action == HandAction.SURRENDER && !canSurrender -> HandAction.HIT
        action == HandAction.SPLIT && hand.cards.size != 2 -> HandAction.HIT
        else -> action
    }
}
