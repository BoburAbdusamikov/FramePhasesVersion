package uz.ttpu.framephases

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class Demo {
    LECTURE_TREE,
    PADDING,
    OFFSET,
    OFFSET_EXPERIMENT,
    COLOR,
    COLOR_BACKGROUND,
    PARALLAX_SLOW,
    PARALLAX_FAST,
    SCROLL_TO_TOP_NAIVE,
    SCROLL_TO_TOP_DERIVED,
    SKIP_VALUE,
    SKIP_LAMBDA,
    CYCLIC,
    CYCLIC_FIXED,
    STACK_BELOW,
    STACK_BELOW_THREE,
}

val CURRENT_DEMO = Demo.LECTURE_TREE

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    Box(Modifier.safeDrawingPadding()) {
                        ShowDemo(CURRENT_DEMO)
                    }
                }
            }
        }
    }
}

@Composable
fun ShowDemo(demo: Demo) {
    when (demo) {
        Demo.LECTURE_TREE -> LectureTree()
        Demo.PADDING -> PaddingDemo()
        Demo.OFFSET -> OffsetDemo()
        Demo.OFFSET_EXPERIMENT -> OffsetDemoExperiment()
        Demo.COLOR -> ColorDemo()
        Demo.COLOR_BACKGROUND -> ColorDemoBackground()
        Demo.PARALLAX_SLOW -> ParallaxSlow()
        Demo.PARALLAX_FAST -> ParallaxFast()
        Demo.SCROLL_TO_TOP_NAIVE -> ScrollToTopNaive()
        Demo.SCROLL_TO_TOP_DERIVED -> ScrollToTopDerived()
        Demo.SKIP_VALUE -> Parent()
        Demo.SKIP_LAMBDA -> ParentDeferred()
        Demo.CYCLIC -> CyclicDemo()
        Demo.CYCLIC_FIXED -> CyclicFixed()
        Demo.STACK_BELOW -> StackBelowDemo()
        Demo.STACK_BELOW_THREE -> StackBelowThreeDemo()
    }
}
