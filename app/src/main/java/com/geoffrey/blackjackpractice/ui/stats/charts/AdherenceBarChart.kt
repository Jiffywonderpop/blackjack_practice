package com.geoffrey.blackjackpractice.ui.stats.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.geoffrey.blackjackpractice.data.local.dao.AdherenceSummary
import com.geoffrey.blackjackpractice.domain.model.DeviationClassification

private val classificationColors = mapOf(
    DeviationClassification.CORRECT_PLAY to Color(0xFF2E7D32),
    DeviationClassification.CORRECT_DEVIATION to Color(0xFF1565C0),
    DeviationClassification.MISSED_DEVIATION to Color(0xFFF9A825),
    DeviationClassification.INCORRECT_DEVIATION to Color(0xFFEF6C00),
    DeviationClassification.INCORRECT_PLAY to Color(0xFFC62828)
)

@Composable
fun AdherenceBarChart(adherence: List<AdherenceSummary>, modifier: Modifier = Modifier) {
    val total = adherence.sumOf { it.count }.coerceAtLeast(1)
    val order = DeviationClassification.entries
    val counts = order.associateWith { cls -> adherence.find { it.deviationClassification == cls }?.count ?: 0 }
    val outlineColor = MaterialTheme.colorScheme.outline

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            val barWidth = size.width / order.size
            val maxCount = counts.values.maxOrNull()?.coerceAtLeast(1) ?: 1
            order.forEachIndexed { index, cls ->
                val count = counts.getValue(cls)
                val barHeight = (count.toFloat() / maxCount) * size.height
                val left = index * barWidth + barWidth * 0.15f
                val top = size.height - barHeight
                drawRect(
                    color = classificationColors[cls] ?: outlineColor,
                    topLeft = Offset(left, top),
                    size = androidx.compose.ui.geometry.Size(barWidth * 0.7f, barHeight)
                )
            }
        }
        order.forEach { cls ->
            val count = counts.getValue(cls)
            val pct = if (total == 0) 0 else (count * 100 / total)
            Text(
                "${cls.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }}: $count ($pct%)",
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}
