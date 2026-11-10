package com.geoffrey.blackjackpractice.domain.review

import com.geoffrey.blackjackpractice.domain.model.Card
import com.geoffrey.blackjackpractice.domain.model.CardCodec
import com.geoffrey.blackjackpractice.domain.model.DeviationClassification
import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.PracticeMode
import com.geoffrey.blackjackpractice.domain.model.Rank
import com.geoffrey.blackjackpractice.domain.model.Suit
import com.geoffrey.blackjackpractice.domain.model.TableRules
import com.geoffrey.blackjackpractice.domain.strategy.DeviationEngine
import com.geoffrey.blackjackpractice.ui.quiz.ReviewDrillGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.truncate
import kotlin.random.Random

class MistakeAnalyzerTest {

    // No surrender, so 16 vs 10 is a pure hit/stand index decision.
    private val rules = TableRules(surrenderAllowed = false)
    private var clock = 0L

    /** Builds a record the same way the app does: grade [action] with DeviationEngine. */
    private fun decision(
        player: List<Rank>,
        dealer: Rank,
        trueCount: Double,
        action: HandAction,
        mode: PracticeMode = PracticeMode.GAME
    ): DecisionRecord {
        val hand = Hand(player.mapIndexed { i, r -> Card(r, Suit.entries[i % 4]) })
        val upcard = Card(dealer, Suit.CLUBS)
        val result = DeviationEngine.evaluate(hand, upcard, truncate(trueCount).toInt(), rules, action)
        return DecisionRecord(
            mode = mode,
            timestamp = ++clock,
            playerCards = CardCodec.encodeAll(hand.cards),
            dealerUpcard = CardCodec.encode(upcard),
            playerHardTotal = hand.hardTotal,
            isSoft = hand.isSoft,
            isPair = hand.isPair,
            trueCount = trueCount,
            actionTaken = action,
            optimalBasicAction = result.optimalBasicAction,
            optimalFinalAction = result.optimalFinalAction,
            classification = result.classification
        )
    }

    @Test
    fun cardCodecRoundTrips() {
        val cards = listOf(Card(Rank.TEN, Suit.SPADES), Card(Rank.ACE, Suit.HEARTS), Card(Rank.KING, Suit.DIAMONDS))
        assertEquals(cards, CardCodec.decodeAll(CardCodec.encodeAll(cards)))
        assertNull(CardCodec.decode("ZZ"))
        assertNull(CardCodec.decodeAll(""))
    }

    @Test
    fun situationsDistinguishHardSoftAndPairs() {
        val hard = MistakeAnalyzer.situationOf(decision(listOf(Rank.TEN, Rank.SIX), Rank.KING, 0.0, HandAction.HIT))
        assertEquals(Situation(HandCategory.HARD, 16, 10), hard)
        assertEquals("Hard 16 vs 10", hard!!.label)

        val soft = MistakeAnalyzer.situationOf(decision(listOf(Rank.ACE, Rank.SEVEN), Rank.NINE, 0.0, HandAction.STAND))
        assertEquals("Soft 18 vs 9", soft!!.label)

        val pair = MistakeAnalyzer.situationOf(decision(listOf(Rank.EIGHT, Rank.EIGHT), Rank.ACE, 0.0, HandAction.HIT))
        assertEquals("8,8 vs A", pair!!.label)

        // Mixed face cards are still a ten-value pair.
        val tens = MistakeAnalyzer.situationOf(decision(listOf(Rank.KING, Rank.QUEEN), Rank.SIX, 0.0, HandAction.STAND))
        assertEquals("10,10 vs 6", tens!!.label)
    }

    @Test
    fun twentyOneIsNotACase() {
        val natural = decision(listOf(Rank.ACE, Rank.KING), Rank.SIX, 0.0, HandAction.STAND)
        assertNull(MistakeAnalyzer.situationOf(natural))
    }

    @Test
    fun ranksMostFrequentMistakesFirst() {
        val records = buildList {
            // Hard 12 vs 2 at neutral count: hit is correct; player stands 3 of 4 times.
            repeat(3) { add(decision(listOf(Rank.TEN, Rank.TWO), Rank.TWO, 0.0, HandAction.STAND)) }
            add(decision(listOf(Rank.TEN, Rank.TWO), Rank.TWO, 0.0, HandAction.HIT))
            // Soft 18 vs 9: hit is correct; player stands once.
            add(decision(listOf(Rank.ACE, Rank.SEVEN), Rank.NINE, 0.0, HandAction.STAND))
            // Hard 20 vs 6: always right.
            repeat(5) { add(decision(listOf(Rank.TEN, Rank.KING), Rank.SIX, 0.0, HandAction.STAND)) }
        }

        val report = MistakeAnalyzer.analyze(records)
        assertEquals(10, report.totalDecisions)
        assertEquals(6, report.correctDecisions)
        assertEquals(2, report.cases.size) // the always-correct case isn't listed

        val top = report.cases[0]
        assertEquals("Hard 12 vs 2", top.key.situation.label)
        assertEquals(3, top.mistakes)
        assertEquals(4, top.attempts)
        assertEquals(HandAction.HIT, top.correctAction)
        assertEquals(HandAction.STAND to 3, top.wrongActions.first())
        assertEquals("Soft 18 vs 9", report.cases[1].key.situation.label)
    }

    @Test
    fun countCasesAreSplitByWhetherTheIndexApplies() {
        val records = listOf(
            // 16 vs 10, TC >= 0: should stand; player hits (missed deviation) twice.
            decision(listOf(Rank.TEN, Rank.SIX), Rank.TEN, 1.4, HandAction.HIT),
            decision(listOf(Rank.NINE, Rank.SEVEN), Rank.QUEEN, 2.0, HandAction.HIT),
            // 16 vs 10, TC < 0: should hit; player stands (unwarranted deviation) once.
            decision(listOf(Rank.TEN, Rank.SIX), Rank.TEN, -2.3, HandAction.STAND)
        )
        val report = MistakeAnalyzer.analyze(records)
        assertEquals(2, report.missedDeviations)
        assertEquals(1, report.incorrectDeviations)
        assertEquals(2, report.cases.size)

        val deviate = report.cases[0]
        assertTrue(deviate.key.countIndicatedDeviation)
        assertEquals(HandAction.STAND, deviate.correctAction)
        assertEquals(2, deviate.missedDeviations)
        assertNotNull(deviate.indexPlay)
        assertEquals("Count deviation: Stand at TC ≥ 0", deviate.countGuidance)
        assertEquals(1.4..2.0, deviate.mistakeTrueCountRange)

        val basic = report.cases[1]
        assertFalse(basic.key.countIndicatedDeviation)
        assertEquals(HandAction.HIT, basic.correctAction)
        assertEquals(1, basic.incorrectDeviations)
    }

    @Test
    fun drillReplaysMistakeSituations() {
        val records = listOf(
            decision(listOf(Rank.TEN, Rank.SIX), Rank.TEN, 1.4, HandAction.HIT),
            decision(listOf(Rank.FIVE, Rank.FOUR, Rank.SEVEN), Rank.TEN, 2.6, HandAction.HIT)
        )
        val cases = MistakeAnalyzer.analyze(records).cases
        val generator = ReviewDrillGenerator(cases)
        assertFalse(generator.isEmpty)

        repeat(20) {
            val q = generator.next(Random(it))!!
            assertEquals("Hard 16 vs 10", q.reviewCaseLabel)
            assertTrue(q.truncatedTrueCount in 1..2)
            // A three-card 16 can't double or surrender.
            assertEquals(q.hand.cards.size == 2, q.canDouble)
        }
        assertTrue(ReviewDrillGenerator(emptyList()).isEmpty)
    }
}
