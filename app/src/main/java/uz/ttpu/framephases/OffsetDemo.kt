package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun OffsetDemo() {
    var offsetX by remember { mutableStateOf(0.dp) }
    SideEffect { Log.d("Phases", "OffsetDemo: composition") }
    Column {
        Button(onClick = { offsetX = if (offsetX == 0.dp) 80.dp else 0.dp }) {
            Text("Move")
        }
        Text(
            text = "Offset demo",
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToPx(), 0) }
                .logPhases("offset demo"),
        )
    }
}

@Composable
fun OffsetDemoExperiment() {
    var offsetX by remember { mutableStateOf(0.dp) }
    SideEffect { Log.d("Phases", "OffsetDemoExperiment: composition") }
    Column {
        Button(onClick = { offsetX = if (offsetX == 0.dp) 80.dp else 0.dp }) {
            Text("Move")
        }
        val offsetPx = with(LocalDensity.current) { offsetX.roundToPx() }
        Text(
            text = "Offset demo",
            modifier = Modifier
                .offset { IntOffset(offsetPx, 0) }
                .logPhases("offset demo"),
        )
    }
}
