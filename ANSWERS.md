# Jetpack Compose Phases — Practice Class Answers

Project: FramePhases (`uz.ttpu.framephases`). To run a hands-on demo, change `CURRENT_DEMO` in `MainActivity.kt` (for example `Demo.PADDING`) and filter Logcat by the tag named in the question.

---

## Part 1 — The frame

### Question 1 — The three phases of a frame

**(a)** The order is Composition → Layout → Drawing.
- **Composition** runs the composable functions and outputs the UI tree: layout nodes with their modifiers. This is *what* to show.
- **Layout** measures and places every node and outputs the size and the (x, y) position of each one. This is *where* to show it.
- **Drawing** renders every node into the canvas and outputs the pixels of the frame. This is *how* it looks.

**(b)** Layout has two steps: **measurement** (measure the children, then decide your own size) and **placement** (place the children).

**(c)** A `LazyColumn` composes only the items that fit on the screen. How many items fit is known only after layout, when the list knows its own height (the constraints from the parent) and the measured height of each item. So the list composes its children *during* its own layout phase (it is a `SubcomposeLayout`). Example: a contacts screen with 2,000 contacts composes only the 10–12 rows that are visible. When the user scrolls, the next rows are composed during layout. `BoxWithConstraints` is similar: a screen that shows one column on a phone and two columns on a tablet can only decide this after it knows its available width.

**(d)** Compose does not really redo all the work in every frame. It tracks which snapshot states each phase reads. When a state changes, it re-runs only the phases and the restart scopes that read it, and it **skips** composable functions whose inputs did not change. The mechanism is called **smart recomposition (skipping)**, and it is based on **state-read tracking**. Layout and drawing work the same way: only the invalidated nodes are measured, placed or drawn again.

### Question 2 — Walk the layout algorithm

**(a)** The order of measure and place events, in the lecture's style:
1. Row: measure children
2. Image: measured (no children) → decides its size 48×48
3. Column: measure children
4. Text A: measured → 100×20
5. Text B: measured → 80×20
6. Column: decides its own size → 100×40
7. Column: places Text A at (0, 0) and Text B at (0, 20)
8. Row: decides its own size → 148×48
9. Row: places Image at (0, 0) and Column at (48, 0)

**(b)**
- Column = **100×40** (width = widest child 100, height = 20 + 20)
- Row = **148×48** (width = 48 + 100, height = max(48, 40))

| Node   | Size   | (x, y) relative to the Row |
|--------|--------|----------------------------|
| Row    | 148×48 | (0, 0)                     |
| Image  | 48×48  | (0, 0)                     |
| Column | 100×40 | (48, 0)                    |
| Text A | 100×20 | (48, 0)                    |
| Text B | 80×20  | (48, 20)                   |

**(c)** Each node is visited **once**: it is measured once and placed once (single-pass measurement). The lecture stresses this because the cost then grows linearly with the number of nodes, so even deep trees stay fast. If every node measured its children twice, the work would double at every level of the tree. For a tree of depth *d* this means 2^d visits: at depth 10 that is about 1,000 times more work, and at depth 20 about a million times more.

**(d)** The **composition phase** produced this tree. Each node is a layout node that contains its modifier chain (size, background, padding and so on), its measure policy (how it measures and places children, such as Row or Column) and its children. The leaf nodes also hold their content, for example the image painter or the text string and its style.

### Question 3 — State reads and restart scopes

**(a)** Yes, they are equivalent. A property delegate is only a shortcut: reading `padding` calls `paddingState.value` behind the scenes. In both snippets the read happens **when the state is used**, which is while `Modifier.padding(...)` is built during composition. Creating the state does not read it.

**(b)**
- **Text("A")**: `offsetX` is read inside the `offset { }` lambda, so it is read in the **layout phase (placement step)**. When `offsetX` changes, only placement (and drawing) runs again. Composition is skipped.
- **Text("B")**: `offsetX` is read in the body (`offsetX.roundToPx()` while computing `offsetPx`), so it is read in the **composition phase**. When `offsetX` changes, the whole composable recomposes. Then the new `offsetPx` reaches the lambda, and layout and drawing run as well.

**(c)** A **restart scope** is the smallest piece of code that Compose can run again by itself when a state that it read changes. Compose remembers which scope was running at the moment of the read, and when the state changes it re-executes only that scope, not the whole screen. For a read made directly in the body of a composable function, the restart scope is the **nearest enclosing non-inline composable function**, i.e. that function itself. Inline composables such as `Column`, `Row` and `Box` do not create their own scope. A read inside a layout or draw lambda has the lambda (the measure, placement or draw block) as its scope.

---

## Part 2 — Seeing the phases in Android Studio

### Question 4 — The first frame (`Demo.LECTURE_TREE`, tag:Phases)

Logcat of the first frame:
```
LectureTree: composition
Row: layout/measure
Image: layout/measure
Column: layout/measure
Text A: layout/measure
Text B: layout/measure
Row: layout/place
Image: layout/place
Column: layout/place
Text A: layout/place
Text B: layout/place
Row: draw
Image: draw
Column: draw
Text A: draw
Text B: draw
```
- Composition comes first, then layout (all measure lines, then all place lines), then drawing. This is the one-way order of the three phases.
- **The measure order matches the lecture's trace:** Row → Image → Column → Text A → Text B, depth-first, parent before its children. Each node is measured once. The log shows the moment each node *starts* to be measured. Its size is decided after its children, as in Question 2.
- **On rotation everything runs again:** composition, all measure, all place and all draw lines. Rotation destroys and recreates the Activity, so `setContent` builds a new composition from scratch (the old tree and its `remember`ed values are gone). The new screen size also changes the constraints, so every node must be measured, placed and drawn again.

### Question 5 — Reading state in composition (`Demo.PADDING`)

**Prediction:** `padding` is read while the modifier chain is built, so it is a composition read. After one tap, all four lines appear:
```
PaddingDemo: composition
padding demo: layout/measure
padding demo: layout/place
padding demo: draw
```
**Log:** every tap prints exactly these four lines (the Column measures and places again as well). Composition runs because the state is read in the body. Then the new padding changes the text's constraints, size and position, so layout and drawing must run too. The prediction matches the log.

### Question 6 — Reading state in layout (`Demo.OFFSET`, then `Demo.OFFSET_EXPERIMENT`)

**Original (read inside the lambda):** after tapping **Move**:
```
offset demo: layout/place
offset demo: draw
```
- **No composition line** appears, because `OffsetDemo` did not read `offsetX` during composition.
- **No layout/measure line** appears either. The read happens in the placement step, and placement has its own restart scope, separate from measurement. The size of the text does not change, so only placement runs again. Drawing follows because the text is now at a new position.

**Experiment (read moved above the Text):** each tap now prints:
```
OffsetDemoExperiment: composition
offset demo: layout/measure
offset demo: layout/place
offset demo: draw
```
`offsetX` is now read in the body, so changing it invalidates the composition. The composable runs again and rebuilds the modifier chain with a new `offsetPx`, which also brings back the measure step. Moving the read from the lambda into the body moved it from the layout phase to the composition phase, and that made every change more expensive.

### Question 7 — Reading state in drawing (`Demo.COLOR`, then `Demo.COLOR_BACKGROUND`)

**Canvas version:** after tapping **Change color** only one line appears:
```
color demo: draw
```
`color` is read inside `onDraw`, so only the drawing phase runs again. There is no composition and no layout.

**`background(color)` version:** each tap prints:
```
ColorDemoBackground: composition
color demo: layout/measure
color demo: layout/place
color demo: draw
```
`background(Color)` receives the value while the modifier is built, so the read moved to composition. The function recomposes and its modifier chain is rebuilt. Rebuilding also recreates the lambdas of `logPhases`, which is why the measure and place lines appear here even though the size did not change.

**Conclusion:** in both versions the state is *stored* in the same place (`remember { mutableStateOf(...) }` in the same function). Only the place where it is *read* is different, and that alone decides which phases run. As the lecture says, where a state is created has little bearing on the phases.

### Question 8 — Which phase re-runs?

| | Read in phase | Composition | Layout | Drawing |
|---|---|---|---|---|
| (a) `Modifier.padding(p)` | Composition | runs | runs | only if size or position changed |
| (b) `Modifier.offset { IntOffset(p, 0) }` | Layout (placement) | skipped | runs (placement only) | only if size or position changed |
| (c) `Modifier.offset(p)` (Dp overload) | Composition | runs | runs | only if size or position changed |
| (d) `Canvas { drawRect(c) }` | Drawing | skipped | skipped | runs |
| (e) `Layout(content) { … s … }` | Layout (measurement) | skipped | runs (measure + place) | only if size or position changed |
| (f) `Modifier.drawWithContent { … c … }` | Drawing | skipped | skipped | runs |

**For a value that changes on every animation frame:** use a deferred read in the latest possible phase. For a color or alpha, use **(d) or (f)**: only drawing runs, which is the cheapest. For a moving position, use **(b)**: only placement and drawing run. Either way the animation never triggers recomposition, which is about 60 times a second saved.

---

## Part 3 — Optimizing state reads

### Question 9 — Parallax (`Demo.PARALLAX_SLOW`, then `Demo.PARALLAX_FAST`, tag:Parallax)

- **ParallaxSlow:** after about 3 seconds of scrolling, the last line is around `slow: composition #150`–`#200` (roughly one composition per frame).
- **ParallaxFast:** the log stays at `fast: composition #1`; scrolling adds nothing.

In the slow version every scroll event triggers **composition, layout and drawing**, because `firstVisibleItemScrollOffset` is read while the modifier is built. In the fast version every scroll event triggers only **layout (placement) and drawing**, because the read was moved into the `offset { }` lambda.

### Question 10 — derivedStateOf (`Demo.SCROLL_TO_TOP_NAIVE`, then `Demo.SCROLL_TO_TOP_DERIVED`, tag:Derived)

- **Naive:** scrolling slowly through about 30 items ends near `naive: composition #31`. That is one composition for every change of `firstVisibleItemIndex`, plus the first one.
- **derivedStateOf:** the log ends at `derived: composition #2`. The first composition is at the top, the second when the index passes 0 and the button appears. Scrolling back to the top adds a third.

**Why the difference is so large:** the naive version reads `firstVisibleItemIndex` directly in composition, so every new index (0 → 1 → 2 → …) recomposes the screen, even though `index > 0` stays `true` all the time. `derivedStateOf` reads the index inside its own calculation and notifies the composable only when the **result** (the Boolean) changes, and that happens only twice.

The **Top** button works in both versions: it calls `scope.launch { listState.animateScrollToItem(0) }` with a scope from `rememberCoroutineScope()` (TODO 3).

### Question 11 — Skipping and deferred reads (`Demo.SKIP_VALUE`, then `Demo.SKIP_LAMBDA`, tag:Skip)

**(1) Passing the value:** first frame plus 3 taps:

| Function | Composition lines |
|---|---|
| Parent | 4 (1 + 3) |
| Child | 4 (1 + 3) |
| Sibling | 1 (skipped on every tap) |

**(2) Passing a lambda:**

| Function | Composition lines |
|---|---|
| Parent | 1 (no recomposition after taps) |
| Child | 4 (1 + 3) |
| Sibling | 1 |

**(3)** In the first version `count` is read in Parent's body (to pass it to `Child(count)`), so Parent's restart scope is invalidated on every tap, and Child recomposes because its parameter changed. In the lambda version the read happens inside `count()` in Child's body, so only **Child's restart scope** is invalidated: Parent never reads the state, and its inputs (the same lambda instance) did not change, so Parent and Sibling are skipped.

---

## Part 4 — Cyclic phase dependency

### Question 12 — Two frames for one layout

| | Frame 1 | Frame 2 |
|---|---|---|
| `imageHeightPx` read by composition | 0 | 360 |
| Top padding of the text | 0 dp | 360 px converted to dp |
| `onSizeChanged` during layout | the image is measured at 360 px, and the size changed from nothing to 360, so it sets `imageHeightPx = 360` | the size is still 360, so it is not called again (or writes the same value) |
| Recomposition scheduled? | **Yes**, the state changed after composition was already done, so a new frame is needed | **No** |
| What is drawn | the text **overlaps** the top of the image | the text is correctly **below** the image |

**(a)** `toDp()` converts a value that came from the layout phase (pixels from `onSizeChanged`) so that it can be used in composition. Layout results going back into composition means the data flows backwards, from a later phase to an earlier one.

**(b)** The pattern to be careful of has three steps:
1. Read a size or position in the layout phase (`onSizeChanged`, `onGloballyPositioned`).
2. Write it into a state.
3. Read that state in composition to change the layout.

This always costs an extra frame.

**(c)** It breaks **unidirectional data flow / single source of truth**. The phases should run in one direction within a frame, and the layout of siblings should be decided by their parent layout, not synced by hand through state.

### Question 13 — Reproduce the loop and fix it (`Demo.CYCLIC`, then `Demo.CYCLIC_FIXED`, tag:Cycle)

**CyclicDemo:**
```
composition #1: imageHeightPx=0
onSizeChanged: height=315
composition #2: imageHeightPx=315
```
There are **2 compositions**: first with 0, then with the real height. The exact pixel value depends on the device: 120 dp × density, for example 315 at 2.625 or 330 at 2.75. For one frame the user sees the text on top of the purple box, and then it jumps down.

**CyclicFixed:**
```
fixed: composition #1
```
There is **1 composition**, and the text is below the box from the first frame.

**Single source of truth:** in the fixed version the **Column** node is the single source of truth. During its single layout pass it measures the box, sees its height, and places the text right below it. The position is decided inside one layout phase, with no state and no second frame.

### Question 14 — Custom layout (`Demo.STACK_BELOW`, then `Demo.STACK_BELOW_THREE`, tag:StackBelow)

The TODOs are completed in `StackBelow.kt`:
- **TODO 1:** log `measure child`, then `measurable.measure(loose)`.
- **TODO 2:** `width` is the widest child; `height` is the sum of the heights plus `spacingPx × (n − 1)`.
- **TODO 3:** log `place children`, then place each child at `(0, y)` and increase `y` by the child's height plus `spacingPx`.

**Log with two children:**
```
measure child
measure child
place children
```
There are **2 `measure child` lines before every `place children`** line, one per child. This shows the one-pass rule: in a single layout pass every child is measured exactly once, the parent decides its size, and then it places them. If the same child were measured twice in one pass, Compose would throw an exception.

**With a third child:** 3 `measure child` lines, then 1 `place children`. The count grows with the number of children, still exactly once per child. The screen looks like CyclicFixed with an 8 dp gap, and it needs no state at all.
