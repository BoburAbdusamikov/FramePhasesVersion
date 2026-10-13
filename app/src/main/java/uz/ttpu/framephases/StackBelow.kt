package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp

@Composable
fun StackBelow(
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { measurable ->
            Log.d("StackBelow", "measure child")
            measurable.measure(loose)
        }
        val spacingPx = spacing.roundToPx()
        val width = placeables.maxOfOrNull { it.width } ?: 0
        val height = placeables.sumOf { it.height } +
            spacingPx * (placeables.size - 1).coerceAtLeast(0)
        layout(constraints.constrainWidth(width), constraints.constrainHeight(height)) {
            Log.d("StackBelow", "place children")
            var y = 0
            placeables.forEach { placeable ->
                placeable.place(0, y)
                y += placeable.height + spacingPx
            }
        }
    }
}

@Composable
fun StackBelowDemo() {
    StackBelow(Modifier.fillMaxWidth(), spacing = 8.dp) {
        Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF6650A4)))
        Text("I'm below the image")
    }
}

@Composable
fun StackBelowThreeDemo() {
    StackBelow(Modifier.fillMaxWidth(), spacing = 8.dp) {
        Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF6650A4)))
        Text("I'm below the image")
        Text("I'm the third child")
    }
}
