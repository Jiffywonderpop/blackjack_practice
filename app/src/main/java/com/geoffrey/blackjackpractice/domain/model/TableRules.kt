package com.geoffrey.blackjackpractice.domain.model

enum class BlackjackPayout(val multiplier: Double, val label: String) {
    THREE_TO_TWO(1.5, "3:2"),
    SIX_TO_FIVE(1.2, "6:5")
}

enum class BetSpreadMode {
    FLAT,
    BY_TRUE_COUNT
}

data class TableRules(
    val numDecks: Int = 6,
    val penetrationPercent: Float = 0.75f,
    val dealerHitsSoft17: Boolean = false,
    val doubleAfterSplit: Boolean = true,
    val surrenderAllowed: Boolean = true,
    val resplitAcesAllowed: Boolean = false,
    val blackjackPayout: BlackjackPayout = BlackjackPayout.THREE_TO_TWO,
    val maxSplitHands: Int = 4
) {
    init {
        require(numDecks in 1..8) { "numDecks must be between 1 and 8" }
        require(penetrationPercent in 0.1f..0.95f) { "penetrationPercent must be between 0.1 and 0.95" }
        require(maxSplitHands in 2..4) { "maxSplitHands must be between 2 and 4" }
    }
}
