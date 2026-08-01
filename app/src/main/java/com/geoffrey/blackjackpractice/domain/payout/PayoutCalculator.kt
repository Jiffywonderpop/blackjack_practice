package com.geoffrey.blackjackpractice.domain.payout

import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.model.HandOutcome
import com.geoffrey.blackjackpractice.domain.model.TableRules

/** Resolves a single player hand against the dealer's final hand into an outcome and net profit/loss. */
object PayoutCalculator {

    fun settle(playerHand: Hand, dealerHand: Hand, rules: TableRules): Pair<HandOutcome, Double> {
        if (playerHand.isSurrendered) return HandOutcome.SURRENDER to -playerHand.bet / 2.0
        if (playerHand.isBust) return HandOutcome.LOSS to -playerHand.bet

        val playerBlackjack = playerHand.isBlackjack
        val dealerBlackjack = dealerHand.isBlackjack

        return when {
            playerBlackjack && dealerBlackjack -> HandOutcome.PUSH to 0.0
            playerBlackjack -> HandOutcome.BLACKJACK to playerHand.bet * rules.blackjackPayout.multiplier
            dealerBlackjack -> HandOutcome.LOSS to -playerHand.bet
            dealerHand.isBust -> HandOutcome.WIN to playerHand.bet
            playerHand.bestTotal > dealerHand.bestTotal -> HandOutcome.WIN to playerHand.bet
            playerHand.bestTotal < dealerHand.bestTotal -> HandOutcome.LOSS to -playerHand.bet
            else -> HandOutcome.PUSH to 0.0
        }
    }
}
