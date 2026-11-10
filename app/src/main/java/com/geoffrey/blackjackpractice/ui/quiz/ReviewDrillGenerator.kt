package com.geoffrey.blackjackpractice.ui.quiz

import com.geoffrey.blackjackpractice.domain.model.CardCodec
import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.review.MistakeCase
import kotlin.math.truncate
import kotlin.random.Random

/**
 * Builds quiz questions by replaying real decisions from the player's mistake cases, choosing a
 * case with probability proportional to how many times it has been missed.
 */
class ReviewDrillGenerator(cases: List<MistakeCase>) {

    private val cases = cases.filter { it.mistakes > 0 }
    private val totalWeight = this.cases.sumOf { it.mistakes }
    private var lastCaseIndex = -1

    val isEmpty: Boolean get() = cases.isEmpty()

    fun next(random: Random = Random.Default): QuizQuestion? {
        if (cases.isEmpty()) return null
        var index = pickWeighted(random)
        // Avoid showing the same case twice in a row when there's an alternative.
        if (index == lastCaseIndex && cases.size > 1) index = pickWeighted(random)
        if (index == lastCaseIndex && cases.size > 1) index = (index + 1) % cases.size
        lastCaseIndex = index

        val case = cases[index]
        for (record in case.records.shuffled(random)) {
            val cards = CardCodec.decodeAll(record.playerCards) ?: continue
            val upcard = CardCodec.decode(record.dealerUpcard) ?: continue
            val twoCards = cards.size == 2
            return QuizQuestion(
                hand = Hand(cards),
                dealerUpcard = upcard,
                truncatedTrueCount = truncate(record.trueCount).toInt(),
                indexPlayLabel = case.indexPlay?.label,
                canDouble = twoCards,
                canSurrender = twoCards,
                reviewCaseLabel = case.key.situation.label
            )
        }
        return null
    }

    private fun pickWeighted(random: Random): Int {
        var roll = random.nextInt(totalWeight)
        cases.forEachIndexed { i, c ->
            roll -= c.mistakes
            if (roll < 0) return i
        }
        return cases.lastIndex
    }
}
