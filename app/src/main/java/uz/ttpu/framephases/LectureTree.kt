package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LectureTree() {
    SideEffect { Log.d("Phases", "LectureTree: composition") }
    Row(Modifier.logPhases("Row")) {
        Box(Modifier.size(40.dp).background(Color.Gray).logPhases("Image"))
        Column(Modifier.logPhases("Column")) {
            Text("Title text", Modifier.logPhases("Text A"))
            Text("Subtitle", Modifier.logPhases("Text B"))
        }
    }
}
