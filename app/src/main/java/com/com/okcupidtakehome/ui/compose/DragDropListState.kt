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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round

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
    private var draggedDistance by mutableStateOf(IntOffset.Zero)

    /**
     * TODO: Explain this
     */
    var positionalDraggedDistance = IntOffset.Zero

    // used to obtain initial offsets on drag start
    var initiallyDraggedElement by mutableStateOf<LazyGridItemInfo?>(null)

    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)

    // TODO: Can we just return `draggedDistance` here?
    val elementDisplacement: IntOffset?
        get() = currentIndexOfDraggedItem
            ?.let { lazyGridState.getVisibleItemInfoFor(absoluteIndex = it) }
            ?.let { item -> (initiallyDraggedElement?.offset ?: IntOffset.Zero) + draggedDistance - item.offset }

    val currentElement: LazyGridItemInfo?
        get() = currentIndexOfDraggedItem?.let {
            lazyGridState.getVisibleItemInfoFor(absoluteIndex = it)
        }

    fun onDragStart(offset: Offset) {
        /**
         * Find what item we're dragging by seeing if the current [Offset] x and y values are inside of
         * the item's bounds -> x..(x + item.width) and y..(y + item.height)
         */
        Log.d(TAG, "alex: onDragStart: offset: $offset")
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
    }

    /*
         -----------------
         |               |
         |    -------    |
         |    |  B  |    |
         |    -------    |
         |               |
         -----------------
      */
    fun onDrag(offset: Offset) {
        draggedDistance += offset.round()
        positionalDraggedDistance += offset.round()
        currentElement?.let { hovered ->
            val actualOffset = hovered.offset + positionalDraggedDistance
            Log.d(TAG, "alex: onDrag currentElement: offset: ${hovered.offset} , index: ${hovered.index} , offset.round(): ${offset.round()} , draggedDistance: $draggedDistance , positionalDraggedDistance: $positionalDraggedDistance, actualOffset: $actualOffset")

            val moveTriggerTargetIntSize = hovered.size.let {
                IntSize(
                    width = (it.width * MOVE_TRIGGER_BOUNDS_PERCENTAGE).toInt(),
                    height = (it.height * MOVE_TRIGGER_BOUNDS_PERCENTAGE).toInt()
                )
            }

            // TODO: hold a reference to all of this inside of `startDrag`? Some type of optimization
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
