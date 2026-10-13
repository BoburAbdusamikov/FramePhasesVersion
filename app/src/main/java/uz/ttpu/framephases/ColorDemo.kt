package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorDemo() {
    var color by remember { mutableStateOf(Color.Red) }
    SideEffect { Log.d("Phases", "ColorDemo: composition") }
    Column {
        Button(onClick = { color = if (color == Color.Red) Color.Blue else Color.Red }) {
            Text("Change color")
        }
        Canvas(Modifier.size(120.dp).logPhases("color demo")) {
            drawRect(color)
        }
    }
}

@Composable
fun ColorDemoBackground() {
    var color by remember { mutableStateOf(Color.Red) }
    SideEffect { Log.d("Phases", "ColorDemoBackground: composition") }
    Column {
        Button(onClick = { color = if (color == Color.Red) Color.Blue else Color.Red }) {
            Text("Change color")
        }
        Box(Modifier.size(120.dp).background(color).logPhases("color demo"))
    }
}
