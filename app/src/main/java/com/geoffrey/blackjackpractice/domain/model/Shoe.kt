package com.geoffrey.blackjackpractice.domain.model

/**
 * A multi-deck shoe with a cut card placed according to [TableRules.penetrationPercent].
 * [needsShuffle] flips true once the cut card is reached, mirroring how a real shoe signals
 * "shuffle after this round" rather than cutting off mid-hand.
 */
class Shoe(private val rules: TableRules, random: kotlin.random.Random = kotlin.random.Random.Default) {

    private var cards: MutableList<Card> = buildShoe(rules.numDecks, random)
    private var nextIndex: Int = 0
    private val cutCardIndex: Int = (cards.size * rules.penetrationPercent).toInt()

    val totalCards: Int get() = cards.size

    val cardsDealt: Int get() = nextIndex

    val cardsRemaining: Int get() = cards.size - nextIndex

    val needsShuffle: Boolean get() = nextIndex >= cutCardIndex

    fun dealCard(): Card {
        check(nextIndex < cards.size) { "Shoe is exhausted; call reshuffle() before dealing further." }
        return cards[nextIndex++]
    }

    fun reshuffle(random: kotlin.random.Random = kotlin.random.Random.Default) {
        cards = buildShoe(rules.numDecks, random)
        nextIndex = 0
    }

    private fun buildShoe(numDecks: Int, random: kotlin.random.Random): MutableList<Card> {
        val fresh = ArrayList<Card>(numDecks * 52)
        repeat(numDecks) {
            for (suit in Suit.entries) {
                for (rank in Rank.entries) {
                    fresh.add(Card(rank, suit))
                }
            }
        }
        fresh.shuffle(random)
        return fresh
    }
}
