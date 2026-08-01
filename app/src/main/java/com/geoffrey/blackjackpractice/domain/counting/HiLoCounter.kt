package com.geoffrey.blackjackpractice.domain.counting

import com.geoffrey.blackjackpractice.domain.model.Card
import kotlin.math.truncate

/**
 * Tracks the Hi-Lo running count for a shoe and derives the true count.
 *
 * True count = running count / decks remaining. Illustrious 18 thresholds are conventionally
 * compared against the true count truncated toward zero (not rounded) — see [truncatedTrueCount].
 */
class HiLoCounter {

    var runningCount: Int = 0
        private set

    fun observe(card: Card) {
        runningCount += card.hiLoValue
    }

    fun reset() {
        runningCount = 0
    }

    fun decksRemaining(cardsRemaining: Int): Double =
        (cardsRemaining.toDouble() / 52.0).coerceAtLeast(1.0 / 52.0)

    fun trueCount(cardsRemaining: Int): Double =
        runningCount / decksRemaining(cardsRemaining)

    /** Truncated toward zero, per standard card-counting convention for index-play comparisons. */
    fun truncatedTrueCount(cardsRemaining: Int): Int =
        truncate(trueCount(cardsRemaining)).toInt()
}
