package com.geoffrey.blackjackpractice.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.geoffrey.blackjackpractice.ui.theme.CasinoColors

/** Green felt with a soft spotlight in the centre and a darkened vignette towards the rail. */
@Composable
fun FeltBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CasinoColors.FeltEdge)
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(CasinoColors.FeltLight, CasinoColors.Felt, CasinoColors.FeltDark, CasinoColors.FeltEdge),
                        center = Offset(size.width / 2f, size.height * 0.42f),
                        radius = maxOf(size.width, size.height) * 0.75f
                    )
                )
                // Fine felt grain: faint diagonal hatching.
                val step = 6.dp.toPx()
                val grain = Color.Black.copy(alpha = 0.035f)
                var x = -size.height
                while (x < size.width) {
                    drawLine(grain, Offset(x, 0f), Offset(x + size.height, size.height), 1f)
                    x += step
                }
            },
        content = content
    )
}

/** The printed table arc ("BLACKJACK PAYS 3 TO 2 / Dealer stands on 17") seen on real felts. */
@Composable
fun TableInscription(payoutLabel: String, dealerRuleLabel: String, modifier: Modifier = Modifier) {
    val gold = CasinoColors.Gold.copy(alpha = 0.55f)
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.size(width = 280.dp, height = 18.dp)) {
            drawArc(
                color = gold,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height * 6f),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
        Text(
            "BLACKJACK PAYS ${payoutLabel.replace(":", " TO ")}",
            color = gold,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            letterSpacing = 2.sp
        )
        Text(
            dealerRuleLabel.uppercase(),
            color = gold.copy(alpha = 0.45f),
            fontSize = 10.sp,
            letterSpacing = 1.5.sp
        )
    }
}

enum class ChipDenomination(val value: Int, val base: Color, val accent: Color, val ink: Color) {
    ONE(1, Color(0xFFF2F2F2), Color(0xFF3A6FD8), Color(0xFF1E3E80)),
    FIVE(5, Color(0xFFD32F2F), Color.White, Color.White),
    TWENTY_FIVE(25, Color(0xFF1F8F4A), Color.White, Color.White),
    HUNDRED(100, Color(0xFF222222), Color(0xFFE2B84A), Color.White),
    FIVE_HUNDRED(500, Color(0xFF6A2BB0), Color(0xFFF7DE8A), Color.White);

    companion object {
        /** Greedy decomposition of an amount into chips, largest first (rounded down to $1). */
        fun breakdown(amount: Double, maxChips: Int = 8): List<ChipDenomination> {
            var remaining = amount.toInt()
            val chips = mutableListOf<ChipDenomination>()
            for (denom in entries.reversed()) {
                while (remaining >= denom.value && chips.size < maxChips) {
                    chips.add(denom)
                    remaining -= denom.value
                }
            }
            return chips.reversed()
        }
    }
}

/** A casino chip: coloured body, six edge inserts, an inner dashed ring and the value. */
@Composable
fun CasinoChip(
    denomination: ChipDenomination,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    showLabel: Boolean = true,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(if (selected) 10.dp else 3.dp, CircleShape)
            .clip(CircleShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val radius = this.size.minDimension / 2f
            drawCircle(denomination.base)
            // Edge inserts.
            val insertSize = Size(radius * 0.36f, radius * 0.26f)
            repeat(6) { i ->
                rotate(degrees = i * 60f) {
                    drawRect(
                        color = denomination.accent,
                        topLeft = Offset(center.x - insertSize.width / 2f, 0f),
                        size = insertSize
                    )
                }
            }
            drawCircle(Color.Black.copy(alpha = 0.18f), radius = radius * 0.7f)
            drawCircle(denomination.base, radius = radius * 0.64f)
            drawCircle(
                color = denomination.accent,
                radius = radius * 0.58f,
                style = Stroke(
                    width = radius * 0.06f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(radius * 0.18f, radius * 0.1f))
                )
            )
            drawCircle(
                Brush.radialGradient(
                    listOf(Color.White.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(center.x - radius * 0.3f, center.y - radius * 0.4f),
                    radius = radius
                )
            )
            drawCircle(Color.Black.copy(alpha = 0.25f), radius = radius - 0.5.dp.toPx(), style = Stroke(1.dp.toPx()))
            if (selected) {
                drawCircle(CasinoColors.GoldLight, radius = radius - 1.5.dp.toPx(), style = Stroke(3.dp.toPx()))
            }
        }
        if (showLabel) {
            Text(
                text = denomination.value.toString(),
                color = denomination.ink,
                fontWeight = FontWeight.Black,
                fontSize = (size.value * 0.27f).sp
            )
        }
    }
}

/** Ring painted on the felt holding a stack of chips for the current wager. */
@Composable
fun BetSpot(amount: Double, modifier: Modifier = Modifier, label: String? = null) {
    val chips = ChipDenomination.breakdown(amount)
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(76.dp)
                .drawBehind {
                    drawCircle(Color.Black.copy(alpha = 0.18f))
                    drawCircle(
                        CasinoColors.Gold.copy(alpha = 0.7f),
                        style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)))
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (chips.isEmpty()) {
                Text("BET", color = CasinoColors.Gold.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            } else {
                Box(Modifier.size(52.dp)) {
                    chips.forEachIndexed { i, chip ->
                        CasinoChip(
                            chip,
                            size = 50.dp,
                            showLabel = i == chips.lastIndex,
                            modifier = Modifier.offset(y = (-(i * 4)).dp + ((chips.size - 1) * 2).dp)
                        )
                    }
                }
            }
        }
        Text(
            label ?: "$${formatMoney(amount)}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

enum class TotalTone { NORMAL, ACTIVE, BUST, BLACKJACK }

/** Pill showing a hand total, e.g. "7/17" for soft hands. */
@Composable
fun TotalBadge(text: String, tone: TotalTone = TotalTone.NORMAL, modifier: Modifier = Modifier) {
    val (bg, fg) = when (tone) {
        TotalTone.NORMAL -> Color.Black.copy(alpha = 0.6f) to Color.White
        TotalTone.ACTIVE -> CasinoColors.Gold to Color(0xFF2A1E00)
        TotalTone.BUST -> CasinoColors.Lose to Color.White
        TotalTone.BLACKJACK -> CasinoColors.GoldLight to Color(0xFF2A1E00)
    }
    Box(
        modifier
            .shadow(3.dp, RoundedCornerShape(50))
            .background(bg, RoundedCornerShape(50))
            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(text, color = fg, fontWeight = FontWeight.Black, fontSize = 14.sp)
    }
}

/** Glossy, gradient-filled game button used for table actions and primary CTAs. */
@Composable
fun CasinoButton(
    label: String,
    top: Color,
    bottom: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    highlighted: Boolean = false,
    textColor: Color = Color.White,
    height: Dp = 52.dp,
    fontSize: Int = 15
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .height(height)
            .alpha(if (enabled) 1f else 0.35f)
            .shadow(if (enabled) 6.dp else 0.dp, shape)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(top, bottom)))
            .border(
                width = if (highlighted) 3.dp else 1.dp,
                color = if (highlighted) CasinoColors.GoldLight else Color.White.copy(alpha = 0.3f),
                shape = shape
            )
            .clickable(enabled = enabled, onClick = onClick)
            .drawBehind {
                // Top gloss.
                drawRect(
                    Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.28f), Color.Transparent)),
                    size = Size(size.width, size.height * 0.5f)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            label.uppercase(),
            color = textColor,
            fontWeight = FontWeight.Black,
            fontSize = fontSize.sp,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GoldButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, height: Dp = 56.dp) {
    CasinoButton(
        label = label,
        top = CasinoColors.GoldLight,
        bottom = CasinoColors.GoldDark,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        textColor = Color(0xFF2A1E00),
        height = height,
        fontSize = 18
    )
}

/** Small dark translucent pill used in the HUD (bankroll, count, shoe). */
@Composable
fun HudPill(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Row(
        modifier
            .background(CasinoColors.Panel, RoundedCornerShape(50))
            .border(1.dp, CasinoColors.Gold.copy(alpha = 0.35f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) { content() }
}

/** Fanned A♠/K♥ logo used on the home screen. */
@Composable
fun CardFanLogo(modifier: Modifier = Modifier) {
    Box(modifier.size(width = 150.dp, height = 120.dp), contentAlignment = Alignment.Center) {
        PlayingCardView(
            card = com.geoffrey.blackjackpractice.domain.model.Card(
                com.geoffrey.blackjackpractice.domain.model.Rank.ACE,
                com.geoffrey.blackjackpractice.domain.model.Suit.SPADES
            ),
            width = 72.dp,
            modifier = Modifier.offset(x = (-22).dp).rotate(-12f)
        )
        PlayingCardView(
            card = com.geoffrey.blackjackpractice.domain.model.Card(
                com.geoffrey.blackjackpractice.domain.model.Rank.KING,
                com.geoffrey.blackjackpractice.domain.model.Suit.HEARTS
            ),
            width = 72.dp,
            modifier = Modifier.offset(x = 22.dp, y = 4.dp).rotate(12f)
        )
        CasinoChip(ChipDenomination.HUNDRED, size = 44.dp, modifier = Modifier.align(Alignment.BottomCenter).offset(y = 6.dp))
    }
}

fun formatMoney(amount: Double): String =
    if (amount % 1.0 == 0.0) "%,.0f".format(amount) else "%,.2f".format(amount)

fun formatSignedMoney(amount: Double): String =
    (if (amount >= 0) "+$" else "-$") + formatMoney(kotlin.math.abs(amount))

/** Gradient (top, bottom) for each action, shared by the table and the quiz. */
fun actionGradient(action: com.geoffrey.blackjackpractice.domain.model.HandAction): Pair<Color, Color> = when (action) {
    com.geoffrey.blackjackpractice.domain.model.HandAction.HIT -> CasinoColors.HitTop to CasinoColors.HitBottom
    com.geoffrey.blackjackpractice.domain.model.HandAction.STAND -> CasinoColors.StandTop to CasinoColors.StandBottom
    com.geoffrey.blackjackpractice.domain.model.HandAction.DOUBLE -> CasinoColors.DoubleTop to CasinoColors.DoubleBottom
    com.geoffrey.blackjackpractice.domain.model.HandAction.SPLIT -> CasinoColors.SplitTop to CasinoColors.SplitBottom
    com.geoffrey.blackjackpractice.domain.model.HandAction.SURRENDER -> CasinoColors.SurrenderTop to CasinoColors.SurrenderBottom
}
