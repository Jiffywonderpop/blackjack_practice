package com.geoffrey.blackjackpractice.domain.model

enum class Suit {
    CLUBS, DIAMONDS, HEARTS, SPADES
}

enum class Rank(val label: String, val blackjackValue: Int) {
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("J", 10),
    QUEEN("Q", 10),
    KING("K", 10),
    ACE("A", 11);

    /** Hi-Lo tag: 2-6 -> +1, 7-9 -> 0, 10/face/A -> -1. */
    val hiLoValue: Int
        get() = when (this) {
            TWO, THREE, FOUR, FIVE, SIX -> 1
            SEVEN, EIGHT, NINE -> 0
            TEN, JACK, QUEEN, KING, ACE -> -1
        }

    /** Dealer-upcard / basic-strategy column index: 2..9 map to themselves, 10/J/Q/K -> 10, A -> 11. */
    val strategyColumn: Int
        get() = if (this == ACE) 11 else blackjackValue
}

data class Card(val rank: Rank, val suit: Suit) {
    val hiLoValue: Int get() = rank.hiLoValue
}
