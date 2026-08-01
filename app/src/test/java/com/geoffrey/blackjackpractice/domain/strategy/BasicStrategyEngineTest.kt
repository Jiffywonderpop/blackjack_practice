package com.geoffrey.blackjackpractice.domain.strategy

import com.geoffrey.blackjackpractice.domain.model.Card
import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.Rank
import com.geoffrey.blackjackpractice.domain.model.Suit
import com.geoffrey.blackjackpractice.domain.model.TableRules
import org.junit.Assert.assertEquals
import org.junit.Test

class BasicStrategyEngineTest {

    private fun card(rank: Rank) = Card(rank, Suit.SPADES)
    private fun hand(vararg ranks: Rank) = Hand(cards = ranks.map { card(it) })

    @Test
    fun `hard 16 vs 10 is hit when surrender not allowed`() {
        val rules = TableRules(surrenderAllowed = false)
        val action = BasicStrategyEngine.getOptimalAction(hand(Rank.TEN, Rank.SIX), card(Rank.TEN), rules)
        assertEquals(HandAction.HIT, action)
    }

    @Test
    fun `pair of 8s always splits`() {
        val rules = TableRules()
        for (upcard in listOf(Rank.TWO, Rank.SEVEN, Rank.NINE, Rank.TEN, Rank.ACE)) {
            val action = BasicStrategyEngine.getOptimalAction(hand(Rank.EIGHT, Rank.EIGHT), card(upcard), rules)
            assertEquals("8,8 vs ${upcard.label}", HandAction.SPLIT, action)
        }
    }

    @Test
    fun `hard 11 vs ace is hit under S17 but double under H17`() {
        val s17 = TableRules(dealerHitsSoft17 = false)
        val h17 = TableRules(dealerHitsSoft17 = true)
        val handAceUp = hand(Rank.SIX, Rank.FIVE)

        assertEquals(HandAction.HIT, BasicStrategyEngine.getOptimalAction(handAceUp, card(Rank.ACE), s17))
        assertEquals(HandAction.DOUBLE, BasicStrategyEngine.getOptimalAction(handAceUp, card(Rank.ACE), h17))
    }

    @Test
    fun `6,6 vs 2 only splits when DAS is allowed`() {
        val withDas = TableRules(doubleAfterSplit = true)
        val withoutDas = TableRules(doubleAfterSplit = false)
        val sixes = hand(Rank.SIX, Rank.SIX)

        assertEquals(HandAction.SPLIT, BasicStrategyEngine.getOptimalAction(sixes, card(Rank.TWO), withDas))
        assertEquals(HandAction.HIT, BasicStrategyEngine.getOptimalAction(sixes, card(Rank.TWO), withoutDas))
    }

    @Test
    fun `hard 16 vs 10 surrenders when allowed`() {
        val rules = TableRules(surrenderAllowed = true)
        val action = BasicStrategyEngine.getOptimalAction(hand(Rank.TEN, Rank.SIX), card(Rank.TEN), rules)
        assertEquals(HandAction.SURRENDER, action)
    }
}
