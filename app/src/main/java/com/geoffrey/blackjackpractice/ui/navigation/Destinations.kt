package com.geoffrey.blackjackpractice.ui.navigation

object Destinations {
    const val HOME = "home"
    const val PLAY = "play"
    const val SPEED_DRILL = "speed_drill"
    const val STRATEGY_QUIZ = "strategy_quiz"
    const val STATS_DASHBOARD = "stats_dashboard"
    const val SESSION_HISTORY = "session_history"
    const val SESSION_DETAIL = "session_detail/{sessionId}"
    const val SETTINGS = "settings"
    const val MISTAKE_REVIEW = "mistake_review"
    const val MISTAKE_DRILL = "mistake_drill"

    fun sessionDetail(sessionId: Long) = "session_detail/$sessionId"
}
