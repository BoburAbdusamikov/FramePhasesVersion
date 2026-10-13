package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun CyclicDemo() {
    var imageHeightPx by remember { mutableIntStateOf(0) }
    val heightInThisComposition = imageHeightPx
    val compositions = remember { intArrayOf(0) }
    SideEffect {
        Log.d(
            "Cycle",
            "composition #${++compositions[0]}: imageHeightPx=$heightInThisComposition",
        )
    }
    Box {
        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFF6650A4))
                .onSizeChanged { size ->
                    Log.d("Cycle", "onSizeChanged: height=${size.height}")
                    imageHeightPx = size.height
                },
        )
        Text(
            text = "I'm below the image",
            modifier = Modifier.padding(
                top = with(LocalDensity.current) { heightInThisComposition.toDp() },
            ),
        )
    }
}

@Composable
fun CyclicFixed() {
    val compositions = remember { intArrayOf(0) }
    SideEffect { Log.d("Cycle", "fixed: composition #${++compositions[0]}") }
    Column {
        Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF6650A4)))
        Text("I'm below the image")
    }
}
