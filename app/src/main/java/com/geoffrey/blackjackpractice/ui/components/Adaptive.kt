package com.geoffrey.blackjackpractice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Centres list/form screens at a readable width so they don't stretch across the ~980dp-wide
 * unfolded Galaxy Z Fold inner screen. No effect on the cover screen or other phones.
 */
@Composable
fun ReadableWidth(content: @Composable () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(Modifier.widthIn(max = 720.dp).fillMaxSize()) { content() }
    }
}
