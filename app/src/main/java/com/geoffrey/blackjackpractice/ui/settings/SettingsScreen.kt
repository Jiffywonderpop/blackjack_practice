package com.geoffrey.blackjackpractice.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.geoffrey.blackjackpractice.domain.model.BetSpreadMode
import com.geoffrey.blackjackpractice.domain.model.BlackjackPayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val settings by viewModel.settings.collectAsState()
    val rules = settings.tableRules

    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Table rules", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)

            LabeledSlider(
                label = "Decks: ${rules.numDecks}",
                value = rules.numDecks.toFloat(),
                range = 1f..8f,
                steps = 6,
                onChange = { viewModel.setNumDecks(it.toInt()) }
            )

            LabeledSlider(
                label = "Penetration: ${(rules.penetrationPercent * 100).toInt()}%",
                value = rules.penetrationPercent,
                range = 0.3f..0.9f,
                steps = 11,
                onChange = { viewModel.setPenetration(it) }
            )

            SwitchRow("Dealer hits soft 17 (H17)", rules.dealerHitsSoft17, viewModel::setDealerHitsSoft17)
            SwitchRow("Double after split (DAS)", rules.doubleAfterSplit, viewModel::setDoubleAfterSplit)
            SwitchRow("Late surrender allowed", rules.surrenderAllowed, viewModel::setSurrenderAllowed)
            SwitchRow("Resplit aces allowed", rules.resplitAcesAllowed, viewModel::setResplitAcesAllowed)

            Text("Blackjack payout")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BlackjackPayout.entries.forEach { payout ->
                    FilterChip(
                        selected = rules.blackjackPayout == payout,
                        onClick = { viewModel.setBlackjackPayout(payout) },
                        label = { Text(payout.label) }
                    )
                }
            }

            HorizontalDivider()
            Text("Bankroll & betting", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)

            LabeledSlider(
                label = "Starting bankroll: $${settings.startingBankroll.toInt()}",
                value = settings.startingBankroll.toFloat(),
                range = 100f..10000f,
                steps = 98,
                onChange = { viewModel.setStartingBankroll(it.toDouble()) }
            )

            Text("Bet sizing")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BetSpreadMode.entries.forEach { mode ->
                    FilterChip(
                        selected = settings.betSpreadMode == mode,
                        onClick = { viewModel.setBetSpreadMode(mode) },
                        label = { Text(if (mode == BetSpreadMode.FLAT) "Flat bet" else "Spread by true count") }
                    )
                }
            }

            LabeledSlider(
                label = "Base bet: $${settings.flatBetAmount.toInt()}",
                value = settings.flatBetAmount.toFloat(),
                range = 5f..500f,
                steps = 98,
                onChange = { viewModel.setFlatBetAmount(it.toDouble()) }
            )

            HorizontalDivider()
            Text("Counting practice", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)

            SwitchRow("Show live running/true count", settings.showLiveCount, viewModel::setShowLiveCount)

            LabeledSlider(
                label = "Count-guess prompt every ${settings.countGuessPromptIntervalHands} hands",
                value = settings.countGuessPromptIntervalHands.toFloat(),
                range = 1f..15f,
                steps = 13,
                onChange = { viewModel.setCountGuessInterval(it.toInt()) }
            )

            Text(
                "Note: Illustrious 18 index numbers are calibrated for a 6-deck, dealer-stands-on-17 " +
                    "game. They're still a good approximation for other rule sets, but true optimal " +
                    "indices shift slightly with deck count and H17/S17.",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )

            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Done") }
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    onChange: (Float) -> Unit
) {
    Column {
        Text(label)
        Slider(value = value, onValueChange = onChange, valueRange = range, steps = steps)
    }
}
