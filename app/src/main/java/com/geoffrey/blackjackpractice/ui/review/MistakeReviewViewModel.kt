package com.geoffrey.blackjackpractice.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geoffrey.blackjackpractice.data.repository.StatsRepository
import com.geoffrey.blackjackpractice.domain.model.PracticeMode
import com.geoffrey.blackjackpractice.domain.review.MistakeAnalyzer
import com.geoffrey.blackjackpractice.domain.review.MistakeReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class ReviewSource(val label: String, val mode: PracticeMode?) {
    ALL("All", null),
    GAME("Play", PracticeMode.GAME),
    QUIZ("Quiz", PracticeMode.QUIZ)
}

data class MistakeReviewUiState(
    val source: ReviewSource = ReviewSource.ALL,
    val report: MistakeReport = MistakeReport.EMPTY,
    val isLoading: Boolean = true
)

class MistakeReviewViewModel(repository: StatsRepository) : ViewModel() {

    private val source = MutableStateFlow(ReviewSource.ALL)

    val uiState: StateFlow<MistakeReviewUiState> = combine(
        repository.observeDecisionRecords(),
        source
    ) { records, src ->
        val filtered = src.mode?.let { mode -> records.filter { it.mode == mode } } ?: records
        MistakeReviewUiState(source = src, report = MistakeAnalyzer.analyze(filtered), isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MistakeReviewUiState()
    )

    fun setSource(value: ReviewSource) {
        source.value = value
    }
}
