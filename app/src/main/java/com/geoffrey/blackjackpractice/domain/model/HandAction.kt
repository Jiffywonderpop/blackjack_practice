package com.geoffrey.blackjackpractice.domain.model

enum class HandAction {
    HIT,
    STAND,
    DOUBLE,
    SPLIT,
    SURRENDER;
}

enum class DeviationClassification {
    /** Matched basic strategy, no index play was in effect. */
    CORRECT_PLAY,

    /** An Illustrious 18 index play was indicated and the player correctly took it. */
    CORRECT_DEVIATION,

    /** An index play was indicated but the player played plain basic strategy instead. */
    MISSED_DEVIATION,

    /** The player deviated from basic strategy even though the count did not indicate it. */
    INCORRECT_DEVIATION,

    /** A plain basic-strategy mistake, unrelated to any index play. */
    INCORRECT_PLAY
}

enum class HandOutcome {
    WIN,
    LOSS,
    PUSH,
    BLACKJACK,
    SURRENDER
}

enum class PracticeMode {
    GAME,
    SPEED_DRILL,
    QUIZ
}
