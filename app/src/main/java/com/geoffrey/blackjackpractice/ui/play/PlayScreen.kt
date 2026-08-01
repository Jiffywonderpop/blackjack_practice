package com.geoffrey.blackjackpractice.ui.play

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.geoffrey.blackjackpractice.domain.RoundState
import com.geoffrey.blackjackpractice.domain.model.Card as PlayingCard
import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.model.HandAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(viewModel: PlayViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Play") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.endSession(); onBack() }) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Bankroll: $${"%.2f".format(state.bankroll)}", style = MaterialTheme.typography.titleMedium)
                Text("Hands: ${state.handsPlayed}")
            }

            if (state.showLiveCount) {
                Text("Running count: ${state.runningCount}   True count: ${"%.1f".format(state.trueCount)}")
            }

            val round = state.roundState
            if (round == null || round.roundOver) {
                Spacer(Modifier.height(24.dp))
                round?.let { RoundResultSummary(it) }
                Button(onClick = { viewModel.dealRound() }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (round == null) "Deal" else "Deal next hand")
                }
            } else {
                DealerRow(round)
                Spacer(Modifier.height(8.dp))
                PlayerHandsRow(round)
                Spacer(Modifier.height(8.dp))
                ActionButtons(viewModel, round)
            }
        }
    }

    if (state.awaitingCountGuess) {
        CountGuessDialog(onSubmit = { viewModel.submitCountGuess(it) })
    }
}

@Composable
private fun DealerRow(round: RoundState) {
    Column {
        Text("Dealer", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            round.dealerHand.cards.forEachIndexed { index, card ->
                val faceDown = index == 1 && !round.dealerHoleRevealed
                PlayingCardChip(if (faceDown) null else card)
            }
        }
        if (round.dealerHoleRevealed) {
            Text("Total: ${round.dealerHand.bestTotal}")
        }
    }
}

@Composable
private fun PlayerHandsRow(round: RoundState) {
    Column {
        Text("Your hand(s)", style = MaterialTheme.typography.labelLarge)
        round.playerHands.forEachIndexed { index, hand ->
            val isActive = index == round.activeHandIndex
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(Modifier.padding(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        hand.cards.forEach { PlayingCardChip(it) }
                    }
                    Text(
                        "Total: ${hand.bestTotal}${if (hand.isSoft) " (soft)" else ""}  " +
                            "Bet: $${"%.2f".format(hand.bet)}" + if (isActive) "  <- acting" else ""
                    )
                }
            }
        }
    }
}

@Composable
private fun RoundResultSummary(round: RoundState) {
    val outcomes = round.handOutcomes ?: return
    val payouts = round.handPayouts ?: return
    Column(Modifier.padding(bottom = 8.dp)) {
        outcomes.forEachIndexed { i, outcome ->
            val payout = payouts.getOrElse(i) { 0.0 }
            Text("Hand ${i + 1}: $outcome (${if (payout >= 0) "+" else ""}${"%.2f".format(payout)})")
        }
    }
}

@Composable
private fun ActionButtons(viewModel: PlayViewModel, round: RoundState) {
    val idx = round.activeHandIndex
    if (idx !in round.playerHands.indices) return
    val actions = listOf(HandAction.HIT, HandAction.STAND, HandAction.DOUBLE, HandAction.SPLIT, HandAction.SURRENDER)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        actions.forEach { action ->
            OutlinedButton(onClick = { viewModel.act(action) }) {
                Text(action.name.lowercase().replaceFirstChar { it.uppercase() })
            }
        }
    }
}

@Composable
private fun PlayingCardChip(card: PlayingCard?) {
    Card(modifier = Modifier.padding(2.dp)) {
        Box(Modifier.padding(8.dp)) {
            Text(if (card == null) "🂠" else "${card.rank.label}${suitSymbol(card.suit)}")
        }
    }
}

private fun suitSymbol(suit: com.geoffrey.blackjackpractice.domain.model.Suit): String = when (suit) {
    com.geoffrey.blackjackpractice.domain.model.Suit.CLUBS -> "♣"
    com.geoffrey.blackjackpractice.domain.model.Suit.DIAMONDS -> "♦"
    com.geoffrey.blackjackpractice.domain.model.Suit.HEARTS -> "♥"
    com.geoffrey.blackjackpractice.domain.model.Suit.SPADES -> "♠"
}

@Composable
private fun CountGuessDialog(onSubmit: (Int) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {},
        title = { Text("What's the running count?") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Running count") }
            )
        },
        confirmButton = {
            Button(onClick = { onSubmit(text.toIntOrNull() ?: 0) }) { Text("Submit") }
        }
    )
}
