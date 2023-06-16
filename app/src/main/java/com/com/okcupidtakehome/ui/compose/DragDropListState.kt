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
    var draggedDistance by mutableStateOf(0f)

    // used to obtain initial offsets on drag start
    var initiallyDraggedElement by mutableStateOf<LazyGridItemInfo?>(null)

    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)

    val initialOffsets: Pair<Int, Int>?
        get() = initiallyDraggedElement?.let { Pair(it.offset.y, it.offsetEnd) }

    val elementDisplacement: Float?
        get() = currentIndexOfDraggedItem
            ?.let { lazyGridState.getVisibleItemInfoFor(absoluteIndex = it) }
            ?.let { item -> (initiallyDraggedElement?.offset?.y ?: 0f).toFloat() + draggedDistance - item.offset.y }

    val currentElement: LazyGridItemInfo?
        get() = currentIndexOfDraggedItem?.let {
            lazyGridState.getVisibleItemInfoFor(absoluteIndex = it)
        }

    var overscrollJob by mutableStateOf<Job?>(null)

    fun onDragStart(offset: Offset) {
        lazyGridState.layoutInfo.visibleItemsInfo
            .firstOrNull { item -> offset.y.toInt() in item.offset.y..(item.offset.y + item.size.height) }
            ?.also {
                currentIndexOfDraggedItem = it.index
                initiallyDraggedElement = it
            }
    }

    fun onDragInterrupted() {
        draggedDistance = 0f
        currentIndexOfDraggedItem = null
        initiallyDraggedElement = null
        overscrollJob?.cancel()
    }

    fun onDrag(offset: Offset) {
        draggedDistance += offset.y


        initialOffsets?.let { (topOffset, bottomOffset) ->
            val startOffset = topOffset + draggedDistance
            val endOffset = bottomOffset + draggedDistance

            currentElement?.let { hovered ->
                lazyGridState.layoutInfo.visibleItemsInfo
                    .filterNot { item -> item.offsetEnd < startOffset || item.offset.y > endOffset || hovered.index == item.index }
                    .firstOrNull { item ->
                        val delta = startOffset - hovered.offset.y
                        when {
                            delta > 0 -> (endOffset > item.offsetEnd)
                            else -> (startOffset < item.offset.y)
                        }
                    }
                    ?.also { item ->
                        currentIndexOfDraggedItem?.let { current ->
//                            onMove.invoke(current, item.index)
                        }
//                        currentIndexOfDraggedItem = item.index
                    }
            }
        }
    }


    fun checkForOverScroll(): Float {
        return initiallyDraggedElement?.let {
            val startOffset = it.offset.y + draggedDistance
            val endOffset = it.offsetEnd + draggedDistance

            return@let when {
                draggedDistance > 0 -> (endOffset - lazyGridState.layoutInfo.viewportEndOffset).takeIf { diff -> diff > 0 }
                draggedDistance < 0 -> (startOffset - lazyGridState.layoutInfo.viewportStartOffset).takeIf { diff -> diff < 0 }
                else -> null
            }
        } ?: 0f
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
val LazyGridItemInfo.offsetEnd: Int
    get() = this.offset.y + this.size.height

/*
   Moving element in the list
*/
fun <T> MutableList<T>.move(from: Int, to: Int) {
    if (from == to)
        return

    val element = this.removeAt(from) ?: return
    this.add(to, element)
}
