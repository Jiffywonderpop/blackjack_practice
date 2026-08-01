package com.geoffrey.blackjackpractice.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(viewModel: SessionHistoryViewModel, sessionId: Long, onBack: () -> Unit) {
    val handLogsFlow = remember(sessionId) { viewModel.observeHandLogs(sessionId) }
    val handLogs by handLogsFlow.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Session #$sessionId") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (handLogs.isEmpty()) {
            Column(Modifier.padding(padding).padding(16.dp)) {
                Text("No hands logged for this session.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(handLogs, key = { it.id }) { log ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("${log.playerCards} vs dealer ${log.dealerUpcard} — TC ${"%.1f".format(log.trueCountAtDecision)}")
                            Text("Action: ${log.actionTaken}  Optimal: ${log.optimalFinalAction}  ${log.deviationClassification}")
                            log.outcome?.let { Text("Outcome: $it (${"%.2f".format(log.payout ?: 0.0)})") }
                        }
                    }
                }
            }
        }
    }
}
