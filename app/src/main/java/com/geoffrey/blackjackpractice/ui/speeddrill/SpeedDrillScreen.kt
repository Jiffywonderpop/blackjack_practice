package com.geoffrey.blackjackpractice.ui.speeddrill

import com.geoffrey.blackjackpractice.ui.components.PlayingCardView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedDrillScreen(viewModel: SpeedDrillViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Speed Count Drill") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.endSession(); onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.isRunning) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    // Static (no deal animation): flashes can be faster than the animation itself.
                    state.currentCard?.let { PlayingCardView(it, width = 180.dp) }
                }
            } else if (state.isFinished) {
                GuessEntry(onSubmit = { viewModel.submitGuess(it) })
            } else {
                Text("Cards to show: ${state.totalCards}")
                Slider(
                    value = state.totalCards.toFloat(),
                    onValueChange = { viewModel.configure(it.toInt(), state.flashIntervalMs) },
                    valueRange = 5f..60f,
                    steps = 10
                )
                Text("Flash speed: ${state.flashIntervalMs} ms/card")
                Slider(
                    value = state.flashIntervalMs.toFloat(),
                    onValueChange = { viewModel.configure(state.totalCards, it.toLong()) },
                    valueRange = 200f..1500f,
                    steps = 12
                )
                state.lastGuessResult?.let {
                    Text(it, style = MaterialTheme.typography.titleMedium)
                }
                Button(onClick = { viewModel.startDrill() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Start")
                }
            }
        }
    }
}

@Composable
private fun GuessEntry(onSubmit: (Int) -> Unit) {
    var text by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("What's the running count?", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Running count") })
        Button(
            onClick = { onSubmit(text.toIntOrNull() ?: 0) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}
