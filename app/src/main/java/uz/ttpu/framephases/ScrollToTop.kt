package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ScrollToTopNaive() {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val compositions = remember { intArrayOf(0) }
    SideEffect { Log.d("Derived", "naive: composition #${++compositions[0]}") }
    val showButton = listState.firstVisibleItemIndex > 0
    Box(Modifier.fillMaxSize()) {
        LazyColumn(state = listState) {
            items(200) { index ->
                Text("Item $index", Modifier.fillMaxWidth().padding(16.dp))
            }
        }
        if (showButton) {
            Button(
                onClick = { scope.launch { listState.animateScrollToItem(0) } },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            ) {
                Text("Top")
            }
        }
    }
}

@Composable
fun ScrollToTopDerived() {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val compositions = remember { intArrayOf(0) }
    SideEffect { Log.d("Derived", "derived: composition #${++compositions[0]}") }
    val showButton by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    Box(Modifier.fillMaxSize()) {
        LazyColumn(state = listState) {
            items(200) { index ->
                Text("Item $index", Modifier.fillMaxWidth().padding(16.dp))
            }
        }
        if (showButton) {
            Button(
                onClick = { scope.launch { listState.animateScrollToItem(0) } },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            ) {
                Text("Top")
            }
        }
    }
}
