package com.geoffrey.blackjackpractice.domain.model

data class Hand(
    val cards: List<Card>,
    val bet: Double = 0.0,
    val isDoubled: Boolean = false,
    val isSplitHand: Boolean = false,
    val isFromSplitAces: Boolean = false,
    val isSurrendered: Boolean = false
) {
    private val aceCount: Int get() = cards.count { it.rank == Rank.ACE }

    /** Best total (<=21 preferred), treating at most one ace as 11. */
    val bestTotal: Int
        get() {
            var total = cards.sumOf { it.rank.blackjackValue }
            var acesAsEleven = aceCount
            while (total > 21 && acesAsEleven > 0) {
                total -= 10
                acesAsEleven--
            }
            return total
        }

    /** True if the best total uses an ace counted as 11 (i.e. hand is "soft"). */
    val isSoft: Boolean
        get() {
            var total = cards.sumOf { it.rank.blackjackValue }
            var acesAsEleven = aceCount
            while (total > 21 && acesAsEleven > 0) {
                total -= 10
                acesAsEleven--
            }
            return acesAsEleven > 0
        }

    /** Total with every ace counted as 1 (used for basic-strategy "hard total" lookups). */
    val hardTotal: Int
        get() = cards.sumOf { if (it.rank == Rank.ACE) 1 else it.rank.blackjackValue }

    val isPair: Boolean
        get() = cards.size == 2 && cards[0].rank.blackjackValue == cards[1].rank.blackjackValue

    val isBlackjack: Boolean
        get() = cards.size == 2 && !isSplitHand && bestTotal == 21

    val isBust: Boolean
        get() = bestTotal > 21

    fun withCard(card: Card): Hand = copy(cards = cards + card)
}
