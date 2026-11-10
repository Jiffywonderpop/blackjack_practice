package com.geoffrey.blackjackpractice.ui.quiz

import com.geoffrey.blackjackpractice.domain.model.Card
import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.model.Rank
import com.geoffrey.blackjackpractice.domain.model.Suit
import com.geoffrey.blackjackpractice.domain.strategy.Illustrious18Table
import com.geoffrey.blackjackpractice.domain.strategy.ThresholdDirection
import kotlin.random.Random

data class QuizQuestion(
    val hand: Hand,
    val dealerUpcard: Card,
    val truncatedTrueCount: Int,
    val indexPlayLabel: String?,
    val canDouble: Boolean = true,
    val canSurrender: Boolean = true,
    /** Set when the question was replayed from one of the player's past mistakes. */
    val reviewCaseLabel: String? = null
)

/** The 10 dealer-upcard columns, one representative rank per column (J/Q/K collapse to TEN). */
private val columnRanks = listOf(
    Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX,
    Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.ACE
)

/** Representative two-card combos that satisfy each Illustrious 18 play's hand-matcher, keyed by label. */
private val indexPlayHandCards: Map<String, Pair<Rank, Rank>> = mapOf(
    "16 vs 10" to (Rank.TEN to Rank.SIX),
    "15 vs 10" to (Rank.TEN to Rank.FIVE),
    "10,10 vs 5" to (Rank.TEN to Rank.TEN),
    "10,10 vs 6" to (Rank.TEN to Rank.TEN),
    "10 vs 10" to (Rank.SIX to Rank.FOUR),
    "9 vs 2" to (Rank.FOUR to Rank.FIVE),
    "9 vs 7" to (Rank.FOUR to Rank.FIVE),
    "16 vs 9" to (Rank.TEN to Rank.SIX),
    "11 vs A" to (Rank.SIX to Rank.FIVE),
    "10 vs A" to (Rank.SIX to Rank.FOUR),
    "13 vs 2" to (Rank.TEN to Rank.THREE),
    "12 vs 3" to (Rank.TEN to Rank.TWO),
    "12 vs 2" to (Rank.TEN to Rank.TWO),
    "13 vs 3" to (Rank.TEN to Rank.THREE),
    "12 vs 4" to (Rank.TEN to Rank.TWO),
    "12 vs 5" to (Rank.TEN to Rank.TWO),
    "12 vs 6" to (Rank.TEN to Rank.TWO)
)

object QuizQuestionGenerator {

    fun next(random: Random = Random.Default): QuizQuestion {
        return if (random.nextFloat() < 0.5f) {
            indexPlayFocused(random)
        } else {
            generalHand(random)
        }
    }

    private fun generalHand(random: Random): QuizQuestion {
        var hand: Hand
        do {
            hand = Hand(cards = listOf(randomCard(random), randomCard(random)))
        } while (hand.isBlackjack)
        val dealerUpcard = Card(columnRanks[random.nextInt(columnRanks.size)], randomSuit(random))
        val trueCount = random.nextInt(-8, 9)
        return QuizQuestion(hand, dealerUpcard, trueCount, indexPlayLabel = null)
    }

    private fun indexPlayFocused(random: Random): QuizQuestion {
        val play = Illustrious18Table.indexPlays.random(random)
        val ranks = indexPlayHandCards.getValue(play.label)
        val hand = Hand(cards = listOf(Card(ranks.first, Suit.SPADES), Card(ranks.second, Suit.HEARTS)))
        val dealerUpcard = Card(columnRanks[play.dealerColumn], Suit.CLUBS)
        val offset = random.nextInt(-2, 3)
        val trueCount = when (play.direction) {
            ThresholdDirection.AT_OR_ABOVE -> play.threshold + offset
            ThresholdDirection.BELOW -> play.threshold - 1 + offset
        }
        return QuizQuestion(hand, dealerUpcard, trueCount, indexPlayLabel = play.label)
    }

    private fun randomCard(random: Random): Card = Card(Rank.entries[random.nextInt(Rank.entries.size)], randomSuit(random))

    private fun randomSuit(random: Random): Suit = Suit.entries[random.nextInt(Suit.entries.size)]
}
