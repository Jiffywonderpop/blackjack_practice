package com.geoffrey.blackjackpractice.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.model.HandAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrategyQuizScreen(viewModel: StrategyQuizViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isReviewDrill) "Mistake Drill" else "Strategy Quiz") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.endSession(); onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        val question = state.question
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Score: ${state.correctCount}/${state.questionsAnswered}")
            if (state.noMistakesToReview) {
                Text(
                    "No mistakes logged yet — showing random questions instead.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (question != null) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Your hand: ${describeHand(question.hand)}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text("Dealer shows: ${question.dealerUpcard.rank.label}")
                        Text("True count: ${question.truncatedTrueCount}")
                        question.reviewCaseLabel?.let {
                            Text("From your mistakes: $it", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                val result = state.lastResult
                if (result == null) {
                    val actions = buildList {
                        add(HandAction.HIT)
                        add(HandAction.STAND)
                        if (question.canDouble) add(HandAction.DOUBLE)
                        if (question.hand.isPair && question.hand.cards.size == 2) add(HandAction.SPLIT)
                        if (question.canSurrender) add(HandAction.SURRENDER)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        actions.forEach { action ->
                            OutlinedButton(onClick = { viewModel.answer(action) }) {
                                Text(action.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                } else {
                    val isCorrect = state.lastActionTaken == result.optimalFinalAction
                    Text(
                        if (isCorrect) "Correct!" else "Not quite — optimal play was ${result.optimalFinalAction}.",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Classification: ${result.classification}")
                    result.indexPlay?.let { Text("Index play in effect: ${it.label}") }
                    Button(onClick = { viewModel.nextQuestion() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Next question")
                    }
                }
            }
        }
    }
}

private fun describeHand(hand: Hand): String {
    val cardsText = hand.cards.joinToString(", ") { it.rank.label }
    val totalText = if (hand.isSoft) "soft ${hand.bestTotal}" else "hard ${hand.bestTotal}"
    return "$cardsText ($totalText)"
}
