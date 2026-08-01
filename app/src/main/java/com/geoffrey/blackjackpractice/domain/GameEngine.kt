package com.geoffrey.blackjackpractice.domain

import com.geoffrey.blackjackpractice.domain.counting.HiLoCounter
import com.geoffrey.blackjackpractice.domain.model.Card
import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.HandOutcome
import com.geoffrey.blackjackpractice.domain.model.Rank
import com.geoffrey.blackjackpractice.domain.model.Shoe
import com.geoffrey.blackjackpractice.domain.model.TableRules
import com.geoffrey.blackjackpractice.domain.payout.PayoutCalculator

data class RoundState(
    val playerHands: List<Hand>,
    val activeHandIndex: Int,
    val dealerHand: Hand,
    val dealerHoleRevealed: Boolean,
    val roundOver: Boolean,
    val handOutcomes: List<HandOutcome>? = null,
    val handPayouts: List<Double>? = null
) {
    val activeHand: Hand? get() = activeHandIndex.takeIf { it in playerHands.indices }?.let { playerHands[it] }
}

/**
 * Orchestrates a single round of blackjack against a shared [Shoe]/[HiLoCounter] pair that
 * persists across rounds until the cut card forces a reshuffle. Card-counting realism note:
 * the dealer's hole card is not observed by [counter] until it is actually revealed (peek or
 * dealer's turn), matching what a player at a real table would be able to see.
 */
class GameEngine(
    private val rules: TableRules,
    private val shoe: Shoe,
    private val counter: HiLoCounter
) {

    private fun drawVisible(): Card {
        if (shoe.cardsRemaining <= 0) {
            shoe.reshuffle()
            counter.reset()
        }
        val card = shoe.dealCard()
        counter.observe(card)
        return card
    }

    private fun drawHidden(): Card {
        if (shoe.cardsRemaining <= 0) {
            shoe.reshuffle()
            counter.reset()
        }
        return shoe.dealCard()
    }

    fun startRound(bet: Double): RoundState {
        if (shoe.needsShuffle) {
            shoe.reshuffle()
            counter.reset()
        }

        val playerCard1 = drawVisible()
        val dealerUpCard = drawVisible()
        val playerCard2 = drawVisible()
        val dealerHoleCard = drawHidden()

        val playerHand = Hand(cards = listOf(playerCard1, playerCard2), bet = bet)
        val dealerHand = Hand(cards = listOf(dealerUpCard, dealerHoleCard))

        val dealerPeekable = dealerUpCard.rank == Rank.ACE || dealerUpCard.rank.blackjackValue == 10
        if (dealerPeekable && dealerHand.isBlackjack) {
            counter.observe(dealerHoleCard)
            return settleRound(RoundState(listOf(playerHand), -1, dealerHand, dealerHoleRevealed = true, roundOver = false))
        }

        val initialState = RoundState(listOf(playerHand), 0, dealerHand, dealerHoleRevealed = false, roundOver = false)
        return if (playerHand.isBlackjack) advanceOrFinish(initialState) else initialState
    }

    fun legalActions(state: RoundState, handIndex: Int): Set<HandAction> {
        val hand = state.playerHands[handIndex]
        if (hand.isBust || hand.isSurrendered || hand.bestTotal == 21) return emptySet()
        if (hand.isFromSplitAces) {
            // Split aces get exactly one card and cannot hit/double; re-splitting a new pair of aces
            // is only possible when resplitAcesAllowed is on.
            val canResplit = hand.isPair && rules.resplitAcesAllowed && state.playerHands.size < rules.maxSplitHands
            return if (canResplit) setOf(HandAction.SPLIT) else emptySet()
        }

        val actions = mutableSetOf(HandAction.HIT, HandAction.STAND)
        if (hand.cards.size == 2) {
            if (!hand.isSplitHand || rules.doubleAfterSplit) actions.add(HandAction.DOUBLE)
            val canSplitMore = state.playerHands.size < rules.maxSplitHands
            val canSplitAces = hand.cards[0].rank != Rank.ACE || rules.resplitAcesAllowed
            if (hand.isPair && canSplitMore && canSplitAces) actions.add(HandAction.SPLIT)
            if (!hand.isSplitHand && state.playerHands.size == 1) actions.add(HandAction.SURRENDER)
        }
        return actions
    }

    fun applyAction(state: RoundState, action: HandAction): RoundState {
        val idx = state.activeHandIndex
        val hand = state.playerHands[idx]
        require(action in legalActions(state, idx)) { "$action is not legal for this hand" }

        return when (action) {
            HandAction.HIT -> {
                val newHand = hand.withCard(drawVisible())
                val hands = state.playerHands.toMutableList().apply { this[idx] = newHand }
                val updated = state.copy(playerHands = hands)
                if (newHand.isBust || newHand.bestTotal == 21) advanceOrFinish(updated) else updated
            }
            HandAction.STAND -> advanceOrFinish(state)
            HandAction.DOUBLE -> {
                val doubledHand = hand.copy(bet = hand.bet * 2, isDoubled = true).withCard(drawVisible())
                val hands = state.playerHands.toMutableList().apply { this[idx] = doubledHand }
                advanceOrFinish(state.copy(playerHands = hands))
            }
            HandAction.SPLIT -> {
                val isAceSplit = hand.cards[0].rank == Rank.ACE
                val hand1 = Hand(cards = listOf(hand.cards[0], drawVisible()), bet = hand.bet, isSplitHand = true, isFromSplitAces = isAceSplit)
                val hand2 = Hand(cards = listOf(hand.cards[1], drawVisible()), bet = hand.bet, isSplitHand = true, isFromSplitAces = isAceSplit)
                val hands = state.playerHands.toMutableList().apply {
                    this[idx] = hand1
                    add(idx + 1, hand2)
                }
                val updated = state.copy(playerHands = hands)
                if (legalActions(updated, idx).isEmpty()) advanceOrFinish(updated) else updated
            }
            HandAction.SURRENDER -> {
                val hands = state.playerHands.toMutableList().apply { this[idx] = hand.copy(isSurrendered = true) }
                advanceOrFinish(state.copy(playerHands = hands))
            }
        }
    }

    private fun advanceOrFinish(state: RoundState): RoundState {
        val nextIndex = state.activeHandIndex + 1
        return if (nextIndex >= state.playerHands.size) {
            playDealerAndSettle(state.copy(activeHandIndex = -1))
        } else {
            state.copy(activeHandIndex = nextIndex)
        }
    }

    private fun playDealerAndSettle(state: RoundState): RoundState {
        var dealer = state.dealerHand
        if (!state.dealerHoleRevealed) {
            counter.observe(dealer.cards[1])
        }

        val anyHandNeedsDealerPlay = state.playerHands.any { !it.isBust && !it.isSurrendered && !it.isBlackjack }
        if (anyHandNeedsDealerPlay) {
            while (dealer.bestTotal < 17 || (dealer.bestTotal == 17 && dealer.isSoft && rules.dealerHitsSoft17)) {
                dealer = dealer.withCard(drawVisible())
            }
        }

        return settleRound(state.copy(dealerHand = dealer, dealerHoleRevealed = true))
    }

    private fun settleRound(state: RoundState): RoundState {
        val results = state.playerHands.map { PayoutCalculator.settle(it, state.dealerHand, rules) }
        return state.copy(
            roundOver = true,
            handOutcomes = results.map { it.first },
            handPayouts = results.map { it.second }
        )
    }
}
