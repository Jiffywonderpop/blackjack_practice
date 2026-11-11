package com.geoffrey.blackjackpractice.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.geoffrey.blackjackpractice.domain.model.Card
import com.geoffrey.blackjackpractice.domain.model.Rank
import com.geoffrey.blackjackpractice.domain.model.Suit
import com.geoffrey.blackjackpractice.ui.theme.CasinoColors
import kotlinx.coroutines.delay

const val CARD_ASPECT = 2.5f / 3.5f

fun Suit.symbol(): String = when (this) {
    Suit.CLUBS -> "♣"
    Suit.DIAMONDS -> "♦"
    Suit.HEARTS -> "♥"
    Suit.SPADES -> "♠"
}

fun Suit.isRed(): Boolean = this == Suit.HEARTS || this == Suit.DIAMONDS

fun Card.shortLabel(): String = "${rank.label}${suit.symbol()}"

/**
 * A playing card drawn entirely in Compose: corner indices, a large centre pip (or framed letter
 * for court cards), and a patterned back. [faceUp] changes animate as a 3D flip.
 */
@Composable
fun PlayingCardView(
    card: Card?,
    modifier: Modifier = Modifier,
    faceUp: Boolean = card != null,
    width: Dp = 64.dp
) {
    val rotation by animateFloatAsState(
        targetValue = if (faceUp && card != null) 0f else 180f,
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "cardFlip"
    )
    val density = LocalDensity.current.density
    val shape = RoundedCornerShape(width * 0.1f)

    Box(
        modifier = modifier
            .width(width)
            .aspectRatio(CARD_ASPECT)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .shadow(elevation = 4.dp, shape = shape)
    ) {
        if (rotation <= 90f && card != null) {
            CardFace(card, width)
        } else {
            // Counter-rotate so the back pattern isn't mirrored mid-flip.
            Box(Modifier.fillMaxSize().graphicsLayer { rotationY = 180f }) { CardBack() }
        }
    }
}

@Composable
private fun CardFace(card: Card, width: Dp) {
    val ink = if (card.suit.isRed()) CasinoColors.CardRed else CasinoColors.CardBlack
    val shape = RoundedCornerShape(width * 0.1f)
    val indexSize = (width.value * 0.24f).sp
    val pipSize = (width.value * 0.18f).sp
    val isCourt = card.rank == Rank.JACK || card.rank == Rank.QUEEN || card.rank == Rank.KING

    Box(
        Modifier
            .fillMaxSize()
            .border(1.dp, CasinoColors.CardBorder, shape)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val r = CornerRadius(size.width * 0.1f)
            drawRoundRect(color = CasinoColors.CardFace, cornerRadius = r)
            if (isCourt) {
                val inset = Offset(size.width * 0.22f, size.height * 0.2f)
                val frame = Size(size.width - inset.x * 2, size.height - inset.y * 2)
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(ink.copy(alpha = 0.10f), ink.copy(alpha = 0.22f))),
                    topLeft = inset,
                    size = frame,
                    cornerRadius = CornerRadius(size.width * 0.05f)
                )
                drawRoundRect(
                    color = ink.copy(alpha = 0.6f),
                    topLeft = inset,
                    size = frame,
                    cornerRadius = CornerRadius(size.width * 0.05f),
                    style = Stroke(width = size.width * 0.02f)
                )
            }
        }
        CornerIndex(card, ink, indexSize, pipSize, Modifier.align(Alignment.TopStart).padding(start = width * 0.07f, top = width * 0.04f))
        CornerIndex(
            card, ink, indexSize, pipSize,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = width * 0.07f, bottom = width * 0.04f)
                .rotate(180f)
        )
        Text(
            text = if (isCourt) card.rank.label else card.suit.symbol(),
            color = ink,
            fontSize = (width.value * if (isCourt) 0.42f else 0.5f).sp,
            fontFamily = if (isCourt) FontFamily.Serif else FontFamily.Default,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun CornerIndex(card: Card, ink: Color, indexSize: androidx.compose.ui.unit.TextUnit, pipSize: androidx.compose.ui.unit.TextUnit, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(card.rank.label, color = ink, fontSize = indexSize, fontWeight = FontWeight.Bold, lineHeight = indexSize)
        Text(card.suit.symbol(), color = ink, fontSize = pipSize, lineHeight = pipSize)
    }
}

@Composable
private fun CardBack() {
    Canvas(Modifier.fillMaxSize()) {
        val r = CornerRadius(size.width * 0.1f)
        drawRoundRect(color = Color.White, cornerRadius = r)
        val inset = size.width * 0.07f
        val inner = Size(size.width - inset * 2, size.height - inset * 2)
        val innerRadius = CornerRadius(size.width * 0.06f)
        drawRoundRect(
            brush = Brush.linearGradient(listOf(CasinoColors.CardBack, CasinoColors.CardBackDark)),
            topLeft = Offset(inset, inset),
            size = inner,
            cornerRadius = innerRadius
        )
        // Diamond lattice pattern.
        clipRect(inset, inset, size.width - inset, size.height - inset) {
            val step = size.width * 0.14f
            val lineColor = Color.White.copy(alpha = 0.22f)
            val stroke = size.width * 0.012f
            var x = -size.height
            while (x < size.width + size.height) {
                drawLine(lineColor, Offset(x, 0f), Offset(x + size.height, size.height), stroke)
                drawLine(lineColor, Offset(x + size.height, 0f), Offset(x, size.height), stroke)
                x += step
            }
        }
        drawRoundRect(
            color = CasinoColors.GoldLight.copy(alpha = 0.7f),
            topLeft = Offset(inset * 1.6f, inset * 1.6f),
            size = Size(size.width - inset * 3.2f, size.height - inset * 3.2f),
            cornerRadius = innerRadius,
            style = Stroke(width = size.width * 0.015f)
        )
    }
}

/**
 * Wraps a card so it slides in from the shoe (top-right) and fades in the first time it is
 * composed, mimicking the dealing animation that casino-style apps use.
 */
@Composable
fun DealtCard(
    card: Card?,
    faceUp: Boolean,
    dealDelayMillis: Long,
    modifier: Modifier = Modifier,
    width: Dp = 64.dp
) {
    // Previews/screenshot tests render a single frame, so start fully dealt there.
    val inspection = LocalInspectionMode.current
    val progress = remember { Animatable(if (inspection) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (inspection) return@LaunchedEffect
        delay(dealDelayMillis)
        progress.animateTo(1f, tween(durationMillis = 320, easing = FastOutSlowInEasing))
    }
    val density = LocalDensity.current.density
    PlayingCardView(
        card = card,
        faceUp = faceUp,
        width = width,
        modifier = modifier.graphicsLayer {
            val p = progress.value
            translationX = (1f - p) * 220f * density
            translationY = (1f - p) * -260f * density
            rotationZ = (1f - p) * 25f
            alpha = p
        }
    )
}
