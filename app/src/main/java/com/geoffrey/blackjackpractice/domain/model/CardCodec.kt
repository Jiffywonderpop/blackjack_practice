package com.geoffrey.blackjackpractice.domain.model

/**
 * Compact string form used when persisting cards in hand logs: rank label followed by the
 * suit's initial, e.g. "10S", "KH", "AD". Hands are comma-joined ("10S,6H").
 */
object CardCodec {

    fun encode(card: Card): String = "${card.rank.label}${card.suit.name.first()}"

    fun encodeAll(cards: List<Card>): String = cards.joinToString(",") { encode(it) }

    fun decode(code: String): Card? {
        val trimmed = code.trim()
        if (trimmed.length < 2) return null
        val rank = Rank.entries.find { it.label == trimmed.dropLast(1) } ?: return null
        val suit = Suit.entries.find { it.name.first() == trimmed.last() } ?: return null
        return Card(rank, suit)
    }

    /** Returns null if any card in the list fails to parse. */
    fun decodeAll(codes: String): List<Card>? {
        if (codes.isBlank()) return null
        val cards = codes.split(",").map { decode(it) ?: return null }
        return cards
    }
}
