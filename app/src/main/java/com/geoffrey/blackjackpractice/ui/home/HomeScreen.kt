package com.geoffrey.blackjackpractice.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.geoffrey.blackjackpractice.ui.components.CardFanLogo
import com.geoffrey.blackjackpractice.ui.components.FeltBackground
import com.geoffrey.blackjackpractice.ui.components.GoldButton
import com.geoffrey.blackjackpractice.ui.theme.CasinoColors

private data class MenuTile(val title: String, val subtitle: String, val icon: ImageVector, val accent: Color, val onClick: () -> Unit)

@Composable
fun HomeScreen(
    onPlay: () -> Unit,
    onSpeedDrill: () -> Unit,
    onQuiz: () -> Unit,
    onStats: () -> Unit,
    onReview: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit
) {
    val tiles = listOf(
        MenuTile("Speed Count", "Flash-card drill", Icons.Filled.Refresh, CasinoColors.SplitTop, onSpeedDrill),
        MenuTile("Strategy Quiz", "Basic + I18", Icons.Filled.Star, CasinoColors.DoubleTop, onQuiz),
        MenuTile("Stats", "Accuracy & bankroll", Icons.AutoMirrored.Filled.List, CasinoColors.HitTop, onStats),
        MenuTile("Mistakes", "Review & drill", Icons.Filled.Warning, CasinoColors.StandTop, onReview),
        MenuTile("History", "Past sessions", Icons.Filled.DateRange, Color(0xFFB07CE8), onHistory),
        MenuTile("Settings", "Table rules", Icons.Filled.Settings, CasinoColors.SurrenderTop, onSettings)
    )

    FeltBackground {
        BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding()) {
            // Unfolded Fold 8 (4:3 landscape) or any wide window: brand + Play on the left, tiles on the right.
            if (maxWidth >= 600.dp && maxWidth > maxHeight) {
                Row(
                    Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Brand()
                        Spacer(Modifier.height(28.dp))
                        GoldButton("Play a shoe", onClick = onPlay, modifier = Modifier.widthIn(max = 380.dp).fillMaxWidth(), height = 68.dp)
                    }
                    TileGrid(tiles, columns = 2, modifier = Modifier.weight(1.1f).verticalScroll(rememberScrollState()))
                }
            } else {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Capped so the unfolded screen held portrait doesn't stretch the menu edge to edge.
                    Column(Modifier.widthIn(max = 560.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(12.dp))
                        Brand()
                        Spacer(Modifier.height(28.dp))
                        GoldButton("Play a shoe", onClick = onPlay, modifier = Modifier.fillMaxWidth(), height = 64.dp)
                        Spacer(Modifier.height(20.dp))
                        // Compact row-style tiles so all six fit on the Fold cover screen without scrolling.
                        TileGrid(tiles, columns = 2, compact = true)
                    }
                }
            }
        }
    }
}

@Composable
private fun Brand() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CardFanLogo()
        Spacer(Modifier.height(12.dp))
        Text(
            "BLACKJACK",
            style = androidx.compose.ui.text.TextStyle(
                brush = Brush.verticalGradient(listOf(CasinoColors.GoldLight, CasinoColors.Gold, CasinoColors.GoldDark))
            ),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Black,
            fontSize = 40.sp,
            letterSpacing = 3.sp
        )
        Text(
            "PRACTICE  ·  COUNT  ·  WIN",
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp
        )
    }
}

@Composable
private fun TileGrid(tiles: List<MenuTile>, columns: Int, modifier: Modifier = Modifier, compact: Boolean = false) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        tiles.chunked(columns).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { tile -> MenuTileView(tile, compact, Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun MenuTileView(tile: MenuTile, compact: Boolean, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    val container = modifier
        .shadow(6.dp, shape)
        .clip(shape)
        .background(Brush.verticalGradient(listOf(Color(0xE61A2E21), Color(0xF00A1610))))
        .border(1.dp, CasinoColors.Gold.copy(alpha = 0.45f), shape)
        .clickable(onClick = tile.onClick)
    val icon = @Composable {
        Box(
            Modifier
                .size(if (compact) 36.dp else 40.dp)
                .background(Brush.verticalGradient(listOf(tile.accent, tile.accent.copy(alpha = 0.6f))), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(tile.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
    if (compact) {
        Row(
            container.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            icon()
            Column {
                Text(tile.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                Text(tile.subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, maxLines = 1)
            }
        }
    } else {
        Column(container.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            icon()
            Text(tile.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(tile.subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
        }
    }
}
