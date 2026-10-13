package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PaddingDemo() {
    var padding by remember { mutableStateOf(8.dp) }
    SideEffect { Log.d("Phases", "PaddingDemo: composition") }
    Column {
        Button(onClick = { padding = if (padding == 8.dp) 32.dp else 8.dp }) {
            Text("Toggle padding")
        }
        Text(
            text = "Padding demo",
            modifier = Modifier
                .padding(padding)
                .logPhases("padding demo"),
        )
    }
}
