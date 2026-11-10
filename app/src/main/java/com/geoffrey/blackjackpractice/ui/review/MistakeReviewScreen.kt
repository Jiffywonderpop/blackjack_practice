package com.geoffrey.blackjackpractice.ui.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.geoffrey.blackjackpractice.domain.model.CardCodec
import com.geoffrey.blackjackpractice.domain.model.PracticeMode
import com.geoffrey.blackjackpractice.domain.model.Suit
import com.geoffrey.blackjackpractice.domain.review.DecisionRecord
import com.geoffrey.blackjackpractice.domain.review.MistakeCase
import com.geoffrey.blackjackpractice.domain.review.MistakeReport
import com.geoffrey.blackjackpractice.domain.review.displayName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MistakeReviewScreen(viewModel: MistakeReviewViewModel, onBack: () -> Unit, onStartDrill: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mistake Review") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val report = state.report
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReviewSource.entries.forEach { src ->
                        FilterChip(
                            selected = state.source == src,
                            onClick = { viewModel.setSource(src) },
                            label = { Text(src.label) }
                        )
                    }
                }
            }
            item { SummaryCard(report) }

            if (report.cases.isEmpty()) {
                item {
                    Text(
                        if (report.totalDecisions == 0) {
                            "No decisions tracked yet. Play a shoe or take the strategy quiz and your " +
                                "choices will be compared against count-adjusted optimal play."
                        } else {
                            "No mistakes so far — every tracked decision matched optimal play for the count."
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                item {
                    Button(onClick = onStartDrill, modifier = Modifier.fillMaxWidth()) {
                        Text("Drill my most-missed cases")
                    }
                }
                item {
                    Text("Most-missed cases", style = MaterialTheme.typography.titleMedium)
                }
                itemsIndexed(report.cases, key = { _, c -> c.key.toString() }) { index, case ->
                    MistakeCaseCard(rank = index + 1, case = case)
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(report: MistakeReport) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Decisions tracked: ${report.totalDecisions}", style = MaterialTheme.typography.titleMedium)
            if (report.totalDecisions > 0) {
                Text("Matched optimal play for the count: ${percent(report.accuracy)}")
                LinearProgressIndicator(
                    progress = { report.accuracy.toFloat() },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Basic strategy errors: ${report.basicStrategyErrors}")
                Text("Missed count deviations: ${report.missedDeviations}")
                Text("Deviated when the count didn't call for it: ${report.incorrectDeviations}")
            }
        }
    }
}

@Composable
private fun MistakeCaseCard(rank: Int, case: MistakeCase) {
    var expanded by rememberSaveable(case.key.toString()) { mutableStateOf(false) }

    Card(
        Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#$rank  ${case.key.situation.label}", style = MaterialTheme.typography.titleMedium)
                AssistChip(
                    onClick = { expanded = !expanded },
                    label = { Text(if (case.isCountCase) "Count play" else "Basic") }
                )
            }
            Text("Wrong ${case.mistakes} of ${case.attempts} (${percent(case.errorRate)})")
            val usual = case.wrongActions.first()
            Text(
                "Optimal: ${case.correctAction.displayName()}  ·  You usually: " +
                    "${usual.first.displayName()} (${usual.second}×)",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(case.countGuidance, style = MaterialTheme.typography.bodySmall)

            if (expanded) {
                HorizontalDivider(Modifier.padding(vertical = 6.dp))
                if (case.wrongActions.size > 1) {
                    Text(
                        "Wrong choices: " + case.wrongActions.joinToString { "${it.first.displayName()} ${it.second}×" },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (case.isCountCase) {
                    Text(
                        "Missed deviations: ${case.missedDeviations} · Unwarranted deviations: " +
                            "${case.incorrectDeviations} · Other: ${case.basicStrategyErrors}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                case.mistakeTrueCountRange?.let { range ->
                    val text = if (range.start == range.endInclusive) {
                        "Missed at TC ${tc(range.start)}"
                    } else {
                        "Missed at TC ${tc(range.start)} to ${tc(range.endInclusive)}"
                    }
                    Text(text, style = MaterialTheme.typography.bodySmall)
                }
                Text("Recent mistakes", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 4.dp))
                case.recentMistakes.forEach { MistakeRow(it) }
            } else {
                Text("Tap for details", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun MistakeRow(record: DecisionRecord) {
    val source = if (record.mode == PracticeMode.QUIZ) "Quiz" else "Play"
    Text(
        "${prettyCards(record.playerCards)} vs ${prettyCards(record.dealerUpcard)} · TC ${tc(record.trueCount)} · " +
            "you ${record.actionTaken.displayName()}, optimal ${record.optimalFinalAction.displayName()} · $source",
        style = MaterialTheme.typography.bodySmall
    )
}

private fun prettyCards(encoded: String): String =
    CardCodec.decodeAll(encoded)?.joinToString(" ") { "${it.rank.label}${suitSymbol(it.suit)}" } ?: encoded

private fun suitSymbol(suit: Suit): String = when (suit) {
    Suit.CLUBS -> "♣"
    Suit.DIAMONDS -> "♦"
    Suit.HEARTS -> "♥"
    Suit.SPADES -> "♠"
}

private fun percent(v: Double) = "${"%.0f".format(v * 100)}%"

private fun tc(v: Double): String {
    val s = "%.1f".format(v)
    return if (v > 0) "+$s" else s
}
