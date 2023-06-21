package com.com.okcupidtakehome.ui.compose

import android.util.Log
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
import androidx.compose.ui.unit.IntOffset.Companion
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import kotlinx.coroutines.Job

private const val TAG = "DragDropListState"

@Composable
fun rememberDragDropListState(
    lazyGridState: LazyGridState = rememberLazyGridState(),
    onMove: (Int, Int) -> Unit,
): DragDropListState {
    return remember { DragDropListState(lazyGridState = lazyGridState, onMove = onMove) }
}

class DragDropListState(
    val lazyGridState: LazyGridState,
    private val onMove: (Int, Int) -> Unit
) {
    var draggedDistance by mutableStateOf(IntOffset.Zero)

    var positionalDraggedDistance = IntOffset.Zero

    // used to obtain initial offsets on drag start
    var initiallyDraggedElement by mutableStateOf<LazyGridItemInfo?>(null)

    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)

    val elementDisplacement: IntOffset?
        get() = currentIndexOfDraggedItem
            ?.let { lazyGridState.getVisibleItemInfoFor(absoluteIndex = it) }
            ?.let { item -> (initiallyDraggedElement?.offset ?: IntOffset.Zero) + draggedDistance - item.offset }

    private val currentElement: LazyGridItemInfo?
        get() = currentIndexOfDraggedItem?.let {
            lazyGridState.getVisibleItemInfoFor(absoluteIndex = it)
        }

    var overscrollJob by mutableStateOf<Job?>(null)

    fun onDragStart(offset: Offset) {
        /**
         * Find what item we're dragging by seeing if the current [Offset] x and y values are inside of
         * the item's bounds -> x..(x + item.width) and y..(y + item.height)
         */
        Log.d(TAG, "alex: onDragStart: offset: $offset")
//        Log.d(TAG, "alex: onDragStart: all visible items info.size: ${lazyGridState.layoutInfo.visibleItemsInfo.size}")
//        lazyGridState.layoutInfo.visibleItemsInfo.forEach { lazyGridItemInfo ->
//            Log.d(TAG, "alex: onDragStart: index: ${lazyGridItemInfo.index} , offset: ${lazyGridItemInfo.offset}")
//        }

        lazyGridState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                val dragOffset = offset.round()
                dragOffset.x in item.offset.x..(item.offset.x + item.size.width)
                        && dragOffset.y in item.offset.y..(item.offset.y + item.size.height)
            }
            ?.also {
                currentIndexOfDraggedItem = it.index
                initiallyDraggedElement = it
            }
    }

    fun onDragInterrupted() {
        draggedDistance = IntOffset.Zero
        positionalDraggedDistance = IntOffset.Zero
        currentIndexOfDraggedItem = null
        initiallyDraggedElement = null
        overscrollJob?.cancel()
    }

    /*
         -----------------
         |               |
         |    -------    |
         |    |  B  |    |
         |    -------    |
         |               |
         -----------------

         TODO: Left off at 6/16/2023 4:40pm - Need to get the offset of the middle part here [B].
          Once we get that offset we must check if the bounds of B are inside of any of the [visibleItemsInfo] bounds
          then we must move the current index to that index.
      */
    fun onDrag(offset: Offset) {
        draggedDistance += offset.round()
        positionalDraggedDistance += offset.round()
        currentElement?.let { hovered ->
            val actualOffset = hovered.offset + positionalDraggedDistance
            Log.d(TAG, "alex: onDrag currentElement: offset: ${hovered.offset} , index: ${hovered.index} , offset.round(): ${offset.round()} , draggedDistance: $draggedDistance , positionalDraggedDistance: $positionalDraggedDistance, actualOffset: $actualOffset")
//            Log.d(TAG, "alex: actualOffset hovered.offset + draggedDistance: $actualOffset")

            val moveTriggerTargetIntSize = hovered.size.let {
                IntSize(
                    width = (it.width * MOVE_TRIGGER_BOUNDS_PERCENTAGE).toInt(),
                    height = (it.height * MOVE_TRIGGER_BOUNDS_PERCENTAGE).toInt()
                )
            }

            // TODO: hold a reference to all of this inside of `startDrag`
            val distanceX = (hovered.size.width - moveTriggerTargetIntSize.width) / 2
            val distanceY = (hovered.size.height - moveTriggerTargetIntSize.height) / 2
            val targetMoveTriggerOffset = IntOffset(
                x = actualOffset.x + distanceX,
                y = actualOffset.y + distanceY
            )

            Log.d(TAG, "alex: onDrag: targetMoveTriggerOffset: $targetMoveTriggerOffset")

            lazyGridState.layoutInfo.visibleItemsInfo
                .firstOrNull { item ->

                    (targetMoveTriggerOffset.x in item.offset.x..(item.offset.x + item.size.width) ||
                            (targetMoveTriggerOffset.x + moveTriggerTargetIntSize.width) in item.offset.x..(item.offset.x + item.size.width))
                            &&
                            (targetMoveTriggerOffset.y in item.offset.y..(item.offset.y + item.size.height) ||
                                    (targetMoveTriggerOffset.y + moveTriggerTargetIntSize.height) in item.offset.y..(item.offset.y + item.size.height))
                }
                ?.let {
                    Log.d(TAG, "alex: hovering OVER: ${it.index} currently moving ${currentElement?.index}")
                    // TODO: this is not working idk why -- see if this if statement helps
                    if (hovered.index != it.index) {
                        Log.d(TAG, "alex: onDrag: moving from : ${hovered.index} to: ${it.index}")
                        onMove.invoke(hovered.index, it.index)
                        currentIndexOfDraggedItem = it.index
                        // old offset + distance - new offset
                        positionalDraggedDistance = hovered.offset + positionalDraggedDistance - it.offset

                        /*
                        TODO: Bugs
                         1. Moving out of bounds too much will case the movement to cancel
                         2. moving 2 down to 5 (item 3 to item 6) messes up not sure why.
                            ^ This might just be how we're moving the items
                         */

                    }
                }
        }
    }

//
    /**
     * TODO: I don't think we need this since we're not really scrolling.
     *  Except we might in the actual app since we can have lots of photos.
     *  6/16/2023 1:02pm - ignore for now
     */
    fun checkForOverScroll(): Float {
//        return initiallyDraggedElement?.let {
//            val startOffset = it.offset.y + draggedDistance
//            val endOffset = it.offsetEnd + draggedDistance
//
//            return@let when {
//                draggedDistance > 0 -> (endOffset - lazyGridState.layoutInfo.viewportEndOffset).takeIf { diff -> diff > 0 }
//                draggedDistance < 0 -> (startOffset - lazyGridState.layoutInfo.viewportStartOffset).takeIf { diff -> diff < 0 }
//                else -> null
//            }
//        } ?: 0f
        return 0f
    }

    companion object {
        private const val MOVE_TRIGGER_BOUNDS_PERCENTAGE = 0.1f
    }
}

/*
    LazyGridState.index is the item's absolute index in the list
    Based on the item's "relative position" with the "currently top" visible item,
    this returns LazyListItemInfo corresponding to it
*/
fun LazyGridState.getVisibleItemInfoFor(absoluteIndex: Int): LazyGridItemInfo? {
    return this.layoutInfo.visibleItemsInfo.getOrNull(absoluteIndex - this.layoutInfo.visibleItemsInfo.first().index)
}

/*
  Bottom offset of the element in Vertical list
*/
//val LazyGridItemInfo.offsetEnd: Int
//    get() = this.offset.y + this.size.height

/*
   Moving element in the list
*/
// TODO: move this
fun <T> MutableList<T>.move(from: Int, to: Int) {
    if (from == to)
        return

    val element = this.removeAt(from) ?: return
    this.add(to, element)
}
