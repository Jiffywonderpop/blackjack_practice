package com.geoffrey.blackjackpractice.ui.play

import androidx.activity.compose.BackHandler
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.geoffrey.blackjackpractice.domain.RoundState
import com.geoffrey.blackjackpractice.domain.model.BetSpreadMode
import com.geoffrey.blackjackpractice.domain.model.DeviationClassification
import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.HandOutcome
import com.geoffrey.blackjackpractice.ui.components.BetSpot
import com.geoffrey.blackjackpractice.ui.components.CasinoButton
import com.geoffrey.blackjackpractice.ui.components.CasinoChip
import com.geoffrey.blackjackpractice.ui.components.ChipDenomination
import com.geoffrey.blackjackpractice.ui.components.DealtCard
import com.geoffrey.blackjackpractice.ui.components.FeltBackground
import com.geoffrey.blackjackpractice.ui.components.GoldButton
import com.geoffrey.blackjackpractice.ui.components.HudPill
import com.geoffrey.blackjackpractice.ui.components.TableInscription
import com.geoffrey.blackjackpractice.ui.components.TotalBadge
import com.geoffrey.blackjackpractice.ui.components.TotalTone
import com.geoffrey.blackjackpractice.ui.components.formatMoney
import com.geoffrey.blackjackpractice.ui.components.formatSignedMoney
import com.geoffrey.blackjackpractice.ui.theme.CasinoColors
import kotlinx.coroutines.delay

private const val DEAL_STEP_MS = 280L
private val HUD_HEIGHT = 52.dp
private val STACKED_PANEL_HEIGHT = 150.dp
private val MAX_CONTROLS_WIDTH = 560.dp

private enum class TableLayout { STACKED, SIDE_RAIL }

/**
 * Galaxy Z Fold 8: the 10:16 cover screen (~450dp wide) stacks controls under the table; the
 * unfolded 4:3 landscape inner screen (~980x740dp) and any short landscape window use a side rail.
 */
private fun tableLayoutFor(width: Dp, height: Dp): TableLayout = when {
    height < 480.dp -> TableLayout.SIDE_RAIL
    width >= 600.dp && width > height -> TableLayout.SIDE_RAIL
    else -> TableLayout.STACKED
}
private const val DEALER_DRAW_STEP_MS = 380L

@Composable
fun PlayScreen(viewModel: PlayViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val leave = { viewModel.endSession(); onBack() }
    BackHandler(onBack = leave)

    PlayContent(
        state = state,
        onBack = leave,
        onChip = viewModel::addChip,
        onClear = viewModel::clearBet,
        onDeal = viewModel::dealRound,
        onAction = viewModel::act,
        onHint = viewModel::showHint,
        onDismissFeedback = viewModel::dismissFeedback,
        onSubmitCountGuess = viewModel::submitCountGuess
    )
}

/** Stateless table UI, separated from [PlayViewModel] so it can be previewed and screenshot-tested. */
@Composable
fun PlayContent(
    state: PlayUiState,
    onBack: () -> Unit,
    onChip: (Int) -> Unit,
    onClear: () -> Unit,
    onDeal: () -> Unit,
    onAction: (HandAction) -> Unit,
    onHint: () -> Unit,
    onDismissFeedback: (Int) -> Unit,
    onSubmitCountGuess: (Int) -> Unit
) {
    FeltBackground {
        if (state.isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center), color = CasinoColors.Gold)
            return@FeltBackground
        }

        BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding()) {
            val layout = tableLayoutFor(maxWidth, maxHeight)
            val rail = layout == TableLayout.SIDE_RAIL
            val railWidth = (maxWidth * 0.3f).coerceIn(260.dp, 340.dp)
            val tableHeight = maxHeight - HUD_HEIGHT - if (rail) 0.dp else STACKED_PANEL_HEIGHT
            val tableWidth = if (rail) maxWidth - railWidth else maxWidth
            // Scale cards to the felt: ~60dp on the Fold cover screen, ~90dp on the open inner screen.
            val cardWidth = minOf(tableHeight * 0.155f, tableWidth * 0.2f).coerceIn(52.dp, 112.dp)

            val table: @Composable (Modifier) -> Unit = { modifier ->
                Box(modifier) {
                    key(state.roundNumber) {
                        TableArea(state, cardWidth)
                    }
                    FeedbackToast(
                        feedback = state.feedback,
                        onTimeout = onDismissFeedback,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)
                    )
                }
            }
            val controls: @Composable (Modifier) -> Unit = { modifier ->
                val round = state.roundState
                if (round == null || round.roundOver) {
                    BettingPanel(state, rail, modifier, onChip = onChip, onClear = onClear, onDeal = onDeal)
                } else {
                    ActionPanel(
                        legal = state.legalActions,
                        hint = state.hintAction,
                        rail = rail,
                        modifier = modifier,
                        onAction = onAction,
                        onHint = onHint
                    )
                }
            }

            Column(Modifier.fillMaxSize()) {
                TableHud(state, onBack = onBack)
                if (rail) {
                    Row(Modifier.weight(1f).fillMaxWidth()) {
                        table(Modifier.weight(1f).fillMaxHeight())
                        controls(Modifier.width(railWidth).fillMaxHeight())
                    }
                } else {
                    table(Modifier.weight(1f).fillMaxWidth())
                    controls(Modifier.fillMaxWidth())
                }
            }
        }
    }

    if (state.awaitingCountGuess) {
        CountGuessDialog(onSubmit = onSubmitCountGuess)
    }
}

@Composable
private fun TableHud(state: PlayUiState, onBack: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .background(CasinoColors.Panel, CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CasinoColors.GoldLight)
        }
        HudPill {
            CasinoChip(ChipDenomination.HUNDRED, size = 18.dp, showLabel = false)
            Text("$${formatMoney(state.bankroll)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(Modifier.weight(1f))
        if (state.showLiveCount) {
            HudPill {
                Text("RC", color = CasinoColors.Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(signed(state.runningCount), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("TC", color = CasinoColors.Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("%+.1f".format(state.trueCount), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
        HudPill {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("SHOE", color = CasinoColors.Gold, fontSize = 9.sp, fontWeight = FontWeight.Bold, lineHeight = 10.sp)
                LinearProgressIndicator(
                    progress = { if (state.totalCards == 0) 0f else state.cardsRemaining / state.totalCards.toFloat() },
                    modifier = Modifier.width(36.dp).height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = CasinoColors.GoldLight,
                    trackColor = Color.White.copy(alpha = 0.15f),
                    drawStopIndicator = {}
                )
            }
        }
    }
}

@Composable
private fun TableArea(state: PlayUiState, cardWidth: Dp) {
    val round = state.roundState
    val dealerRule = if (state.rules.dealerHitsSoft17) "Dealer hits soft 17" else "Dealer stands on all 17s"
    // TableArea is keyed by round number, so this captures whether the round was settled on the deal itself.
    val endedOnDeal = remember { round?.roundOver == true }
    val resultDelay = round?.let { dealerFinishDelay(it, endedOnDeal) } ?: 0L

    Column(
        Modifier.fillMaxSize().padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            if (round != null) DealerArea(round, resultDelay, cardWidth)
        }
        TableInscription(state.rules.blackjackPayout.label, "$dealerRule · ${state.rules.numDecks} decks")
        Box(Modifier.weight(1.3f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (round == null) {
                BetSpot(state.betAmount)
            } else {
                PlayerArea(round, resultDelay, cardWidth * 1.06f)
            }
        }
    }

    if (round != null && round.roundOver) {
        // Short felt (e.g. the cover screen in landscape): a one-line banner that fits the inscription band.
        ResultBanner(round, resultDelay, compact = cardWidth < 70.dp)
    }
}

/** Delay before the hole card flips once the player finishes acting. */
private const val DEALER_TURN_START_MS = 450L

/** Milliseconds until the dealer's last card has landed, so results don't spoil the draw. */
private fun dealerFinishDelay(round: RoundState, endedOnDeal: Boolean): Long =
    (if (endedOnDeal) DEAL_STEP_MS * 4 else DEALER_TURN_START_MS) +
        (round.dealerHand.cards.size - 2).coerceAtLeast(0) * DEALER_DRAW_STEP_MS + 350L

@Composable
private fun DealerArea(round: RoundState, resultDelay: Long, cardWidth: Dp) {
    val hand = round.dealerHand
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("DEALER", color = CasinoColors.Gold.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        FannedCards(count = hand.cards.size, cardWidth = cardWidth) { i ->
            val delayMs = if (i < 2) DEAL_STEP_MS * (1 + 2 * i) else DEALER_TURN_START_MS + (i - 1) * DEALER_DRAW_STEP_MS
            DealtCard(
                card = hand.cards[i],
                faceUp = i != 1 || round.dealerHoleRevealed,
                dealDelayMillis = delayMs,
                width = cardWidth
            )
        }
        val total = if (round.dealerHoleRevealed) hand else Hand(listOf(hand.cards[0]))
        // Re-keyed on reveal so the final total waits for the dealer's draws to land.
        key(round.dealerHoleRevealed) {
            DelayedVisibility(delayMillis = if (round.dealerHoleRevealed) resultDelay else DEAL_STEP_MS * 2) {
                TotalBadge(
                    text = totalLabel(total),
                    tone = when {
                        round.dealerHoleRevealed && hand.isBust -> TotalTone.BUST
                        round.dealerHoleRevealed && hand.isBlackjack -> TotalTone.BLACKJACK
                        else -> TotalTone.NORMAL
                    }
                )
            }
        }
    }
}

@Composable
private fun PlayerArea(round: RoundState, resultDelay: Long, baseCardWidth: Dp) {
    val multi = round.playerHands.size > 1
    val cardWidth = if (round.playerHands.size > 2) baseCardWidth * 0.8f else baseCardWidth
    Row(
        Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Bottom
    ) {
        round.playerHands.forEachIndexed { index, hand ->
            val isActive = !round.roundOver && index == round.activeHandIndex
            PlayerHandView(
                hand = hand,
                isActive = isActive && multi,
                isActing = isActive,
                outcome = round.handOutcomes?.getOrNull(index),
                payout = round.handPayouts?.getOrNull(index),
                resultDelay = resultDelay,
                initialDeal = !multi,
                cardWidth = cardWidth
            )
        }
    }
}

@Composable
private fun PlayerHandView(
    hand: Hand,
    isActive: Boolean,
    isActing: Boolean,
    outcome: HandOutcome?,
    payout: Double?,
    resultDelay: Long,
    initialDeal: Boolean,
    cardWidth: Dp
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        Modifier
            .then(
                if (isActive) Modifier
                    .border(2.dp, CasinoColors.GoldLight, shape)
                    .background(CasinoColors.Gold.copy(alpha = 0.12f), shape)
                else Modifier
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TotalBadge(
            text = totalLabel(hand),
            tone = when {
                hand.isBust -> TotalTone.BUST
                hand.isBlackjack -> TotalTone.BLACKJACK
                isActing -> TotalTone.ACTIVE
                else -> TotalTone.NORMAL
            }
        )
        FannedCards(count = hand.cards.size, cardWidth = cardWidth) { i ->
            val delayMs = if (initialDeal && i < 2) DEAL_STEP_MS * 2 * i else 0L
            DealtCard(card = hand.cards[i], faceUp = true, dealDelayMillis = delayMs, width = cardWidth)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ChipStackMini(hand.bet)
            Text("$${formatMoney(hand.bet)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            if (hand.isDoubled) Text("×2", color = CasinoColors.GoldLight, fontWeight = FontWeight.Black, fontSize = 12.sp)
        }
        if (outcome != null) {
            DelayedVisibility(resultDelay) { OutcomeTag(outcome, hand, payout ?: 0.0) }
        }
    }
}

/** Cards overlap left-to-right like a hand laid out on the felt. */
@Composable
private fun FannedCards(count: Int, cardWidth: Dp, card: @Composable (Int) -> Unit) {
    val step = cardWidth * 0.42f
    Box(Modifier.width(cardWidth + step * (count - 1).coerceAtLeast(0))) {
        for (i in 0 until count) {
            key(i) {
                Box(Modifier.offset(x = step * i)) { card(i) }
            }
        }
    }
}

@Composable
private fun ChipStackMini(amount: Double) {
    val chips = ChipDenomination.breakdown(amount, maxChips = 5)
    Box(Modifier.size(24.dp, 30.dp), contentAlignment = Alignment.BottomCenter) {
        chips.forEachIndexed { i, chip ->
            CasinoChip(chip, size = 22.dp, showLabel = false, modifier = Modifier.offset(y = (-i * 3).dp))
        }
    }
}

@Composable
private fun OutcomeTag(outcome: HandOutcome, hand: Hand, payout: Double) {
    val (label, color) = when (outcome) {
        HandOutcome.BLACKJACK -> "BLACKJACK" to CasinoColors.GoldLight
        HandOutcome.WIN -> "WIN" to CasinoColors.Win
        HandOutcome.PUSH -> "PUSH" to CasinoColors.Push
        HandOutcome.SURRENDER -> "SURRENDER" to CasinoColors.Push
        HandOutcome.LOSS -> (if (hand.isBust) "BUST" else "LOSE") to CasinoColors.Lose
    }
    Row(
        Modifier
            .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
            .border(1.dp, color, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(label, color = color, fontWeight = FontWeight.Black, fontSize = 12.sp)
        if (outcome != HandOutcome.PUSH) {
            Text(formatSignedMoney(payout), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ResultBanner(round: RoundState, resultDelay: Long, compact: Boolean) {
    val outcomes = round.handOutcomes ?: return
    val net = round.handPayouts?.sum() ?: 0.0
    val allBust = round.playerHands.all { it.isBust }
    val (title, color) = when {
        HandOutcome.BLACKJACK in outcomes -> "BLACKJACK!" to CasinoColors.GoldLight
        net > 0 && round.dealerHand.isBust -> "DEALER BUSTS" to CasinoColors.Win
        net > 0 -> "YOU WIN" to CasinoColors.Win
        net == 0.0 -> "PUSH" to CasinoColors.Push
        allBust -> "BUST" to CasinoColors.Lose
        outcomes.all { it == HandOutcome.SURRENDER } -> "SURRENDERED" to CasinoColors.Push
        else -> "DEALER WINS" to CasinoColors.Lose
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        DelayedVisibility(resultDelay + 150, enter = scaleIn(initialScale = 0.6f) + fadeIn()) {
            val bannerModifier = Modifier
                .shadow(16.dp, RoundedCornerShape(16.dp))
                .background(Color(0xE6101A12), RoundedCornerShape(16.dp))
                .border(2.dp, color, RoundedCornerShape(16.dp))
            if (compact) {
                Row(
                    bannerModifier.padding(horizontal = 18.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(title, color = color, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    if (net != 0.0) {
                        Text(formatSignedMoney(net), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            } else {
                Column(
                    bannerModifier.padding(horizontal = 28.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(title, color = color, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Black, fontSize = 30.sp)
                    if (net != 0.0) {
                        Text(formatSignedMoney(net), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DelayedVisibility(
    delayMillis: Long,
    enter: androidx.compose.animation.EnterTransition = fadeIn(),
    content: @Composable () -> Unit
) {
    val inspection = androidx.compose.ui.platform.LocalInspectionMode.current
    var visible by remember { mutableStateOf(inspection) }
    LaunchedEffect(Unit) {
        delay(delayMillis)
        visible = true
    }
    AnimatedVisibility(visible = visible, enter = enter) { content() }
}

@Composable
private fun FeedbackToast(feedback: DecisionFeedback?, onTimeout: (Int) -> Unit, modifier: Modifier = Modifier) {
    // Plain holder (not state) so the last message stays visible while it animates out.
    val lastShown = remember { arrayOfNulls<DecisionFeedback>(1) }
    if (feedback != null) lastShown[0] = feedback
    LaunchedEffect(feedback?.id) {
        val id = feedback?.id ?: return@LaunchedEffect
        delay(2600)
        onTimeout(id)
    }
    AnimatedVisibility(
        visible = feedback != null,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier
    ) {
        val fb = lastShown[0] ?: return@AnimatedVisibility
        val color = if (fb.isCorrect) CasinoColors.Win else CasinoColors.Lose
        val correct = actionLabel(fb.correctAction)
        val message = when (fb.classification) {
            DeviationClassification.CORRECT_PLAY -> "Correct — $correct"
            DeviationClassification.CORRECT_DEVIATION -> "Correct index play — $correct"
            DeviationClassification.MISSED_DEVIATION -> "Missed index play — should $correct"
            DeviationClassification.INCORRECT_DEVIATION -> "Unneeded deviation — should $correct"
            DeviationClassification.INCORRECT_PLAY -> "Basic strategy says $correct"
        }
        Row(
            Modifier
                .shadow(8.dp, RoundedCornerShape(50))
                .background(Color(0xF0101A12), RoundedCornerShape(50))
                .border(1.5.dp, color, RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(Modifier.size(22.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
                Text(if (fb.isCorrect) "✓" else "✗", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
            Text(message, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

/**
 * Background for the controls: a bottom bar (content capped at a comfortable width so buttons
 * don't stretch across an unfolded screen) or a full-height side rail with controls at the bottom.
 */
@Composable
private fun ControlSurface(rail: Boolean, modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    val bg = Modifier.background(CasinoColors.Rail.copy(alpha = 0.85f))
    if (rail) {
        Column(
            modifier.then(bg).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Bottom),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    } else {
        Box(modifier.then(bg), contentAlignment = Alignment.TopCenter) {
            Column(
                Modifier
                    .widthIn(max = MAX_CONTROLS_WIDTH)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        }
    }
}

@Composable
private fun ActionPanel(
    legal: Set<HandAction>,
    hint: HandAction?,
    rail: Boolean,
    modifier: Modifier,
    onAction: (HandAction) -> Unit,
    onHint: () -> Unit
) {
    val hintButton = @Composable {
        IconButton(
            onClick = onHint,
            modifier = Modifier
                .size(44.dp)
                .background(CasinoColors.Panel, CircleShape)
                .border(1.dp, CasinoColors.Gold.copy(alpha = 0.6f), CircleShape)
        ) {
            Icon(Icons.Filled.Info, contentDescription = "Hint", tint = CasinoColors.GoldLight)
        }
    }
    ControlSurface(rail, modifier) {
        if (rail) {
            // Grid: secondary actions above, Hit/Stand nearest the thumb.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                ActionButton(HandAction.SURRENDER, legal, hint, onAction, CasinoColors.SurrenderTop, CasinoColors.SurrenderBottom, Modifier.weight(1f), 48.dp)
                hintButton()
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(HandAction.DOUBLE, legal, hint, onAction, CasinoColors.DoubleTop, CasinoColors.DoubleBottom, Modifier.weight(1f), 50.dp)
                ActionButton(HandAction.SPLIT, legal, hint, onAction, CasinoColors.SplitTop, CasinoColors.SplitBottom, Modifier.weight(1f), 50.dp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(HandAction.HIT, legal, hint, onAction, CasinoColors.HitTop, CasinoColors.HitBottom, Modifier.weight(1f), 64.dp)
                ActionButton(HandAction.STAND, legal, hint, onAction, CasinoColors.StandTop, CasinoColors.StandBottom, Modifier.weight(1f), 64.dp)
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                ActionButton(HandAction.DOUBLE, legal, hint, onAction, CasinoColors.DoubleTop, CasinoColors.DoubleBottom, Modifier.weight(1f), 44.dp)
                ActionButton(HandAction.SPLIT, legal, hint, onAction, CasinoColors.SplitTop, CasinoColors.SplitBottom, Modifier.weight(1f), 44.dp)
                ActionButton(HandAction.SURRENDER, legal, hint, onAction, CasinoColors.SurrenderTop, CasinoColors.SurrenderBottom, Modifier.weight(1f), 44.dp)
                hintButton()
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(HandAction.HIT, legal, hint, onAction, CasinoColors.HitTop, CasinoColors.HitBottom, Modifier.weight(1f), 58.dp)
                ActionButton(HandAction.STAND, legal, hint, onAction, CasinoColors.StandTop, CasinoColors.StandBottom, Modifier.weight(1f), 58.dp)
            }
        }
    }
}

@Composable
private fun ActionButton(
    action: HandAction,
    legal: Set<HandAction>,
    hint: HandAction?,
    onAction: (HandAction) -> Unit,
    top: Color,
    bottom: Color,
    modifier: Modifier,
    height: Dp
) {
    CasinoButton(
        label = actionLabel(action),
        top = top,
        bottom = bottom,
        onClick = { onAction(action) },
        enabled = action in legal,
        highlighted = action == hint,
        height = height,
        fontSize = if (height > 50.dp) 18 else 13,
        modifier = modifier
    )
}

@Composable
private fun BettingPanel(
    state: PlayUiState,
    rail: Boolean,
    modifier: Modifier,
    onChip: (Int) -> Unit,
    onClear: () -> Unit,
    onDeal: () -> Unit
) {
    ControlSurface(rail, modifier) {
        var lastTapped by remember { mutableIntStateOf(-1) }
        if (rail) {
            Text("YOUR BET", color = CasinoColors.Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        }
        // One row of five on the bottom bar; 3 + 2 in the narrower side rail.
        ChipDenomination.entries.chunked(if (rail) 3 else 5).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { denom ->
                    CasinoChip(
                        denom,
                        size = 54.dp,
                        selected = lastTapped == denom.value,
                        onClick = { lastTapped = denom.value; onChip(denom.value) }
                    )
                }
            }
        }
        if (state.betSpreadMode == BetSpreadMode.BY_TRUE_COUNT) {
            Text(
                "Bet unit $${formatMoney(state.betAmount)} × (TC + 1) when you deal",
                color = CasinoColors.GoldLight.copy(alpha = 0.8f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
        val dealLabel = if (state.betAmount > 0) "Deal  $${formatMoney(state.betAmount)}" else "Place a bet"
        val clear = @Composable { m: Modifier, h: Dp ->
            CasinoButton(
                label = "Clear",
                top = CasinoColors.SurrenderTop,
                bottom = CasinoColors.SurrenderBottom,
                onClick = onClear,
                enabled = state.betAmount > 0,
                modifier = m,
                height = h
            )
        }
        if (rail) {
            clear(Modifier.fillMaxWidth(), 44.dp)
            GoldButton(dealLabel, onClick = onDeal, enabled = state.betAmount > 0, modifier = Modifier.fillMaxWidth(), height = 64.dp)
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                clear(Modifier.weight(1f), 56.dp)
                GoldButton(dealLabel, onClick = onDeal, enabled = state.betAmount > 0, modifier = Modifier.weight(2.2f))
            }
        }
    }
}

@Composable
private fun CountGuessDialog(onSubmit: (Int) -> Unit) {
    var value by remember { mutableIntStateOf(0) }
    AlertDialog(
        onDismissRequest = {},
        containerColor = CasinoColors.PanelSolid,
        title = { Text("What's the running count?", color = CasinoColors.GoldLight) },
        text = {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CasinoButton("−", CasinoColors.StandTop, CasinoColors.StandBottom, { value-- }, Modifier.width(64.dp), fontSize = 26)
                Text(
                    signed(value),
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.width(110.dp),
                    textAlign = TextAlign.Center
                )
                CasinoButton("+", CasinoColors.HitTop, CasinoColors.HitBottom, { value++ }, Modifier.width(64.dp), fontSize = 26)
            }
        },
        confirmButton = {
            GoldButton("Submit", onClick = { onSubmit(value) }, modifier = Modifier.width(140.dp), height = 46.dp)
        }
    )
}

private fun totalLabel(hand: Hand): String = when {
    hand.isBlackjack -> "BJ"
    hand.isSoft && hand.bestTotal < 21 -> "${hand.bestTotal - 10}/${hand.bestTotal}"
    else -> hand.bestTotal.toString()
}

private fun actionLabel(action: HandAction): String = when (action) {
    HandAction.HIT -> "Hit"
    HandAction.STAND -> "Stand"
    HandAction.DOUBLE -> "Double"
    HandAction.SPLIT -> "Split"
    HandAction.SURRENDER -> "Surrender"
}

private fun signed(n: Int): String = if (n > 0) "+$n" else n.toString()
