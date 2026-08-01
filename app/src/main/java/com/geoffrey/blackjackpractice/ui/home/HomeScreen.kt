package com.geoffrey.blackjackpractice.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onPlay: () -> Unit,
    onSpeedDrill: () -> Unit,
    onQuiz: () -> Unit,
    onStats: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Blackjack Practice", style = MaterialTheme.typography.headlineMedium)
            Text("Hi-Lo counting, basic strategy, and Illustrious 18 drills.", style = MaterialTheme.typography.bodyMedium)

            Button(onClick = onPlay, modifier = Modifier.fillMaxWidth()) { Text("Play a shoe") }
            Button(onClick = onSpeedDrill, modifier = Modifier.fillMaxWidth()) { Text("Speed count drill") }
            Button(onClick = onQuiz, modifier = Modifier.fillMaxWidth()) { Text("Strategy quiz") }
            OutlinedButton(onClick = onStats, modifier = Modifier.fillMaxWidth()) { Text("Stats dashboard") }
            OutlinedButton(onClick = onHistory, modifier = Modifier.fillMaxWidth()) { Text("Session history") }
            OutlinedButton(onClick = onSettings, modifier = Modifier.fillMaxWidth()) { Text("Settings") }
        }
    }
}
