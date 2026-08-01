package com.geoffrey.blackjackpractice.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.geoffrey.blackjackpractice.ui.stats.charts.AdherenceBarChart
import com.geoffrey.blackjackpractice.ui.stats.charts.BankrollLineChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsDashboardScreen(viewModel: StatsViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stats Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Hands played: ${state.handsPlayed}")
                    Text("Win rate: ${"%.1f".format(state.winRate * 100)}%")
                    Text("Net earnings: $${"%.2f".format(state.netProfit)}")
                    Text("Strategy adherence: ${"%.1f".format(state.adherenceRate * 100)}%")
                }
            }

            Text("Bankroll over time", style = MaterialTheme.typography.titleMedium)
            if (state.bankrollPoints.size < 2) {
                Text("Play a few hands to see your bankroll trend.")
            } else {
                BankrollLineChart(state.bankrollPoints)
            }

            HorizontalDivider()
            Text("Basic strategy / Illustrious 18 adherence", style = MaterialTheme.typography.titleMedium)
            AdherenceBarChart(state.adherence)

            HorizontalDivider()
            Text("Count-guess accuracy", style = MaterialTheme.typography.titleMedium)
            if (state.countAccuracy.isEmpty()) {
                Text("No count guesses logged yet.")
            } else {
                state.countAccuracy.forEach { summary ->
                    Text(
                        "${summary.mode}: ${summary.exactMatches}/${summary.totalGuesses} exact " +
                            "(avg error ${"%.2f".format(summary.avgAbsError)})"
                    )
                }
            }
        }
    }
}
