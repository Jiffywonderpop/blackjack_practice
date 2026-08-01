package com.geoffrey.blackjackpractice.domain.strategy

import com.geoffrey.blackjackpractice.domain.model.Card
import com.geoffrey.blackjackpractice.domain.model.DeviationClassification
import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.Rank
import com.geoffrey.blackjackpractice.domain.model.Suit
import com.geoffrey.blackjackpractice.domain.model.TableRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviationEngineTest {

    private fun card(rank: Rank) = Card(rank, Suit.SPADES)
    private fun hand(vararg ranks: Rank) = Hand(cards = ranks.map { card(it) })
    private val rules = TableRules(surrenderAllowed = false)

    @Test
    fun `16 vs 10 at true count 0 correctly deviates to stand`() {
        val result = DeviationEngine.evaluate(
            hand = hand(Rank.TEN, Rank.SIX),
            dealerUpcard = card(Rank.TEN),
            truncatedTrueCount = 0,
            rules = rules,
            playerAction = HandAction.STAND
        )
        assertEquals(HandAction.STAND, result.optimalFinalAction)
        assertEquals(DeviationClassification.CORRECT_DEVIATION, result.classification)
    }

    @Test
    fun `16 vs 10 below the index still hits`() {
        val result = DeviationEngine.evaluate(
            hand = hand(Rank.TEN, Rank.SIX),
            dealerUpcard = card(Rank.TEN),
            truncatedTrueCount = -1,
            rules = rules,
            playerAction = HandAction.HIT
        )
        assertEquals(HandAction.HIT, result.optimalFinalAction)
        assertEquals(DeviationClassification.CORRECT_PLAY, result.classification)
    }

    @Test
    fun `12 vs 4 below zero deviates to hit`() {
        val result = DeviationEngine.evaluate(
            hand = hand(Rank.TEN, Rank.TWO),
            dealerUpcard = card(Rank.FOUR),
            truncatedTrueCount = -1,
            rules = rules,
            playerAction = HandAction.HIT
        )
        assertEquals(HandAction.HIT, result.optimalFinalAction)
        assertEquals(DeviationClassification.CORRECT_DEVIATION, result.classification)
    }

    @Test
    fun `12 vs 4 at zero or above stands per basic strategy`() {
        val result = DeviationEngine.evaluate(
            hand = hand(Rank.TEN, Rank.TWO),
            dealerUpcard = card(Rank.FOUR),
            truncatedTrueCount = 0,
            rules = rules,
            playerAction = HandAction.STAND
        )
        assertEquals(HandAction.STAND, result.optimalFinalAction)
        assertEquals(DeviationClassification.CORRECT_PLAY, result.classification)
    }

    @Test
    fun `missed deviation is classified correctly`() {
        val result = DeviationEngine.evaluate(
            hand = hand(Rank.TEN, Rank.SIX),
            dealerUpcard = card(Rank.TEN),
            truncatedTrueCount = 2,
            rules = rules,
            playerAction = HandAction.HIT
        )
        assertEquals(DeviationClassification.MISSED_DEVIATION, result.classification)
    }

    @Test
    fun `insurance threshold is truncated toward zero`() {
        assertFalse(Illustrious18Table.shouldTakeInsurance(2))
        assertTrue(Illustrious18Table.shouldTakeInsurance(3))
    }
}
