package com.geoffrey.blackjackpractice.ui.quiz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.geoffrey.blackjackpractice.ui.components.CasinoButton
import com.geoffrey.blackjackpractice.ui.components.FeltBackground
import com.geoffrey.blackjackpractice.ui.components.HudPill
import com.geoffrey.blackjackpractice.ui.components.PlayingCardView
import com.geoffrey.blackjackpractice.ui.components.TotalBadge
import com.geoffrey.blackjackpractice.ui.components.TotalTone
import com.geoffrey.blackjackpractice.ui.components.actionGradient
import com.geoffrey.blackjackpractice.ui.theme.CasinoColors
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
                QuizTable(question)

                val result = state.lastResult
                if (result == null) {
                    val actions = buildList {
                        add(HandAction.HIT)
                        add(HandAction.STAND)
                        if (question.canDouble) add(HandAction.DOUBLE)
                        if (question.hand.isPair && question.hand.cards.size == 2) add(HandAction.SPLIT)
                        if (question.canSurrender) add(HandAction.SURRENDER)
                    }
                    actions.chunked(3).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { action ->
                                val (top, bottom) = actionGradient(action)
                                CasinoButton(
                                    label = action.name,
                                    top = top,
                                    bottom = bottom,
                                    onClick = { viewModel.answer(action) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                } else {
                    val isCorrect = state.lastActionTaken == result.optimalFinalAction
                    Text(
                        if (isCorrect) "✓ Correct!" else "✗ Not quite — optimal play was ${result.optimalFinalAction}.",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isCorrect) CasinoColors.Win else CasinoColors.Lose
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

@Composable
private fun QuizTable(question: QuizQuestion) {
    FeltBackground(Modifier.clip(RoundedCornerShape(20.dp)).height(300.dp)) {
        Column(
            Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("DEALER", color = CasinoColors.Gold, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontSize = 11.sp)
                HudPill {
                    Text("TC", color = CasinoColors.Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("%+d".format(question.truncatedTrueCount), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                PlayingCardView(question.dealerUpcard, width = 60.dp)
                PlayingCardView(null, width = 60.dp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                question.hand.cards.forEach { PlayingCardView(it, width = 66.dp) }
            }
            TotalBadge(describeHand(question.hand), TotalTone.ACTIVE)
            question.reviewCaseLabel?.let {
                Text("From your mistakes: $it", color = CasinoColors.GoldLight, fontSize = 12.sp)
            }
        }
    }
}

private fun describeHand(hand: Hand): String {
    return if (hand.isSoft) "Soft ${hand.bestTotal}" else "Hard ${hand.bestTotal}"
}
