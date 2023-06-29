package com.com.okcupidtakehome.ui.compose

import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round

private const val TAG = "DragDropListState"

@Composable
fun rememberDragDropGridListState(
    lazyGridState: LazyGridState = rememberLazyGridState(),
    onMove: (Int, Int) -> Unit,
    canBeMoved: (index: Int) -> Boolean
): DragDropGridListState {
    return remember {
        DragDropGridListState(
            lazyGridState = lazyGridState,
            onMove = onMove,
            canBeMoved = canBeMoved
        )
    }
}

class DragDropGridListState(
    val lazyGridState: LazyGridState,
    private val onMove: (Int, Int) -> Unit,
    private val canBeMoved: (index: Int) -> Boolean
) {
    /**
     * This variable keeps track of the distance moved by the dragged item relative to their placement.
     * Meaning, when we move an item from an index to another we have to recompute its dragged distance:
     *
     * Example: Lets say you have 2 items (Item 1 and Item 2) and their placements are
     * Item 1: (0, 0)
     * Item 2: (0, 100)
     *
     * Lets say the user drags Item 2 up 30 pixels so the distance dragged is now (0, -30). After Item 2 and
     * Item 1 get switched, Item 2's placement offset is now (0, 0) and distance CANNOT be (0, -30) anymore as
     * that would mean Item 2 is off the screen. So we have to recompute its distance relative to its NEW position.
     * In this example the recomputed position would be (0, 70). How you get this is:
     *
     * old offset + distance dragged - new offset = New distance dragged
     *
     * Or in the above example
     * (0, 100) + (0, -30) - (0, 0) = (0, 70)
     */
    var positionalDraggedDistance = IntOffset.Zero
        private set

    // used to obtain initial offsets on drag start
    private var initiallyDraggedElement by mutableStateOf<LazyGridItemInfo?>(null)

    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
        private set

    val currentElement: LazyGridItemInfo?
        get() = currentIndexOfDraggedItem?.let {
            lazyGridState.getVisibleItemInfoFor(absoluteIndex = it)
        }

    /**
     * This method's job is to find what item we're dragging by seeing if the current [Offset] x and y values are inside of
     * the item's bounds -> x..(x + item.width) and y..(y + item.height)
     */
    fun onDragStart(offset: Offset) {
        lazyGridState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                val dragOffset = offset.round()
                dragOffset.x in item.offset.x..(item.offset.x + item.size.width)
                        && dragOffset.y in item.offset.y..(item.offset.y + item.size.height)
            }
            ?.also {
                if (canBeMoved.invoke(it.index)) {
                    currentIndexOfDraggedItem = it.index
                    initiallyDraggedElement = it
                } else {
                    onDragInterrupted()
                }
            }
    }

    fun onDragInterrupted() {
        positionalDraggedDistance = IntOffset.Zero
        currentIndexOfDraggedItem = null
        initiallyDraggedElement = null
    }

    /**
     * This method's job is to figure out what items in the grid are the currently dragging over and
     * if our "moveTriggerTarget" bounds are touching any of the items we must trigger a move.
     *
     * What is a moveTriggerTarget? The "moveTriggerTarget" here is just the bounds at which we should
     * trigger a move if they are hit/touched.
     *
     * Lets say we have an Item A below. We don't want to trigger a move when A's bounds are touching
     * any of the other items in the grid since the moving would be too aggressive and we would never
     * hover over any items. Instead, we should trigger a move when A's inner B's bounds are touching
     * any of the other items. The [MOVE_TRIGGER_BOUNDS_PERCENTAGE] is used to compute the inner size
     * and offset. It describes how big the inner box should be. If that value is 100% then the move trigger
     * would happen if ANY part of A's bounds is touched. 50% would describe whats shown below. Here if
     * B's bounds are touched the move is triggered.
     *
     * -----------------
     * |       A       |
     * |    -------    |
     * |    |  B  |    |
     * |    -------    |
     * |               |
     * -----------------
     *
     * After the move happens we also must recompute [positionalDraggedDistance]. See the comment above
     * this variable's declaration that explains the math behind it.
     */
    fun onDrag(offset: Offset) {
        positionalDraggedDistance += offset.round()
        currentElement?.let { hovered ->
            val actualOffset = hovered.offset + positionalDraggedDistance
            val moveTriggerTargetSize = hovered.size.let {
                IntSize(
                    width = (it.width * MOVE_TRIGGER_BOUNDS_PERCENTAGE).toInt(),
                    height = (it.height * MOVE_TRIGGER_BOUNDS_PERCENTAGE).toInt()
                )
            }

            val distanceX = (hovered.size.width - moveTriggerTargetSize.width) / 2
            val distanceY = (hovered.size.height - moveTriggerTargetSize.height) / 2
            val moveTriggerTargetOffset = IntOffset(
                x = actualOffset.x + distanceX,
                y = actualOffset.y + distanceY
            )

            lazyGridState.layoutInfo.visibleItemsInfo
                .firstOrNull { item ->
                    (moveTriggerTargetOffset.x in item.offset.x..(item.offset.x + item.size.width) ||
                            (moveTriggerTargetOffset.x + moveTriggerTargetSize.width) in item.offset.x..(item.offset.x + item.size.width))
                            &&
                            (moveTriggerTargetOffset.y in item.offset.y..(item.offset.y + item.size.height) ||
                                    (moveTriggerTargetOffset.y + moveTriggerTargetSize.height) in item.offset.y..(item.offset.y + item.size.height))
                }
                ?.let {
                    if (hovered.index != it.index && canBeMoved.invoke(it.index)) {
                        onMove.invoke(hovered.index, it.index)
                        currentIndexOfDraggedItem = it.index
                        // old offset + distance dragged - new offset = New distance dragged
                        positionalDraggedDistance = hovered.offset + positionalDraggedDistance - it.offset
                    }
                }
        }
    }

    companion object {
        private const val MOVE_TRIGGER_BOUNDS_PERCENTAGE = 0.1f
    }
}

/**
 * LazyGridState.index is the item's absolute index in the list
 * Based on the item's "relative position" with the "currently top" visible item,
 * this returns LazyListItemInfo corresponding to it
 */
fun LazyGridState.getVisibleItemInfoFor(absoluteIndex: Int): LazyGridItemInfo? {
    return this.layoutInfo.visibleItemsInfo.getOrNull(absoluteIndex - this.layoutInfo.visibleItemsInfo.first().index)
}
