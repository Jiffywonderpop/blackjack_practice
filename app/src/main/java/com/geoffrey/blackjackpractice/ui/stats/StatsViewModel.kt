package com.geoffrey.blackjackpractice.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geoffrey.blackjackpractice.data.local.dao.AdherenceSummary
import com.geoffrey.blackjackpractice.data.local.dao.BankrollPoint
import com.geoffrey.blackjackpractice.data.local.dao.CountAccuracySummary
import com.geoffrey.blackjackpractice.data.local.dao.OutcomeSummary
import com.geoffrey.blackjackpractice.data.repository.StatsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class StatsUiState(
    val outcomes: List<OutcomeSummary> = emptyList(),
    val adherence: List<AdherenceSummary> = emptyList(),
    val bankrollPoints: List<BankrollPoint> = emptyList(),
    val countAccuracy: List<CountAccuracySummary> = emptyList()
) {
    val handsPlayed: Int get() = outcomes.sumOf { it.count }
    val netProfit: Double get() = outcomes.sumOf { it.totalPayout ?: 0.0 }
    val winRate: Double get() {
        val decided = outcomes.filter { it.outcome.name != "PUSH" }
        val wins = decided.filter { it.outcome.name == "WIN" || it.outcome.name == "BLACKJACK" }.sumOf { it.count }
        val total = decided.sumOf { it.count }
        return if (total == 0) 0.0 else wins.toDouble() / total
    }
    val adherenceRate: Double get() {
        val total = adherence.sumOf { it.count }
        val good = adherence.filter { it.deviationClassification.name == "CORRECT_PLAY" || it.deviationClassification.name == "CORRECT_DEVIATION" }
            .sumOf { it.count }
        return if (total == 0) 0.0 else good.toDouble() / total
    }
}

class StatsViewModel(repository: StatsRepository) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = combine(
        repository.observeOutcomeSummary(),
        repository.observeAdherenceSummary(),
        repository.observeBankrollPoints(),
        repository.observeCountAccuracyByMode()
    ) { outcomes, adherence, bankrollPoints, countAccuracy ->
        StatsUiState(outcomes, adherence, bankrollPoints, countAccuracy)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsUiState()
    )
}
