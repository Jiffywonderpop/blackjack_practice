package com.geoffrey.blackjackpractice.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.geoffrey.blackjackpractice.ui.history.SessionHistoryViewModel
import com.geoffrey.blackjackpractice.ui.play.PlayViewModel
import com.geoffrey.blackjackpractice.ui.quiz.StrategyQuizViewModel
import com.geoffrey.blackjackpractice.ui.review.MistakeReviewViewModel
import com.geoffrey.blackjackpractice.ui.settings.SettingsViewModel
import com.geoffrey.blackjackpractice.ui.speeddrill.SpeedDrillViewModel
import com.geoffrey.blackjackpractice.ui.stats.StatsViewModel

class ViewModelFactory(
    private val container: AppContainer,
    /** Builds StrategyQuizViewModel in mistake-review drill mode. */
    private val reviewDrill: Boolean = false
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            PlayViewModel::class.java -> PlayViewModel(container.settingsRepository, container.sessionRepository)
            SpeedDrillViewModel::class.java -> SpeedDrillViewModel(container.settingsRepository, container.sessionRepository)
            StrategyQuizViewModel::class.java -> StrategyQuizViewModel(
                container.settingsRepository,
                container.sessionRepository,
                container.statsRepository,
                reviewDrill = reviewDrill
            )
            StatsViewModel::class.java -> StatsViewModel(container.statsRepository)
            MistakeReviewViewModel::class.java -> MistakeReviewViewModel(container.statsRepository)
            SessionHistoryViewModel::class.java -> SessionHistoryViewModel(container.sessionRepository)
            SettingsViewModel::class.java -> SettingsViewModel(container.settingsRepository)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T
    }
}
