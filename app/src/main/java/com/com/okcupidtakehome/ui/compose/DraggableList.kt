package com.com.okcupidtakehome.ui.compose

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.UUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "DraggableList"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableList(
    list: List<ReorderItem>,
    onMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()

    var overscrollJob by remember { mutableStateOf<Job?>(null) }

    val dragDropListState = rememberDragDropListState(onMove = onMove)

    LazyVerticalGrid(
        state = dragDropListState.lazyGridState,
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .padding(horizontal = 6.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDrag = { change, offset ->
                        change.consume()
                        dragDropListState.onDrag(offset)

                        if (overscrollJob?.isActive == true) {
                            return@detectDragGesturesAfterLongPress
                        }

                        dragDropListState.checkForOverScroll()
                            .takeIf { it != 0f }
                            ?.let {
                                overscrollJob = scope.launch { dragDropListState.lazyGridState.scrollBy(it) }
                            }
                            ?: run { overscrollJob?.cancel() }
                    },
                    onDragStart = { offset ->
                        dragDropListState.onDragStart(offset)
                    },
                    onDragEnd = {
                        dragDropListState.onDragInterrupted()
                    },
                    onDragCancel = {
                        dragDropListState.onDragInterrupted()
                    }
                )
            }
    ) {
        itemsIndexed(
            items = list,
            key = { index, item -> item.id  }
        ) {index, item ->
            Box(
                modifier = Modifier
                    .animateItemPlacement()
                    .graphicsLayer {
                        val offsetOrNull = dragDropListState.elementDisplacement.takeIf {
                            index == dragDropListState.currentIndexOfDraggedItem
                        }
                        translationY = offsetOrNull ?: 0f
                    }
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .background(Color.White)
            ) {
                Text(
                    text = "Item ${item.title}",
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
    /*
    LazyColumn(
        state = dragDropListState.lazyListState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDrag = { change, offset ->
                        change.consume()
                        dragDropListState.onDrag(offset)

                        if (overscrollJob?.isActive == true) {
                            return@detectDragGesturesAfterLongPress
                        }

                        dragDropListState.checkForOverScroll()
                            .takeIf { it != 0f }
                            ?.let {
                                overscrollJob =
                                    scope.launch { dragDropListState.lazyListState.scrollBy(it) }
                            }
                            ?: run { overscrollJob?.cancel() }
                    },
                    onDragStart = { offset ->
                        dragDropListState.onDragStart(offset)
                    },
                    onDragEnd = {
                        dragDropListState.onDragInterrupted()
                    },
                    onDragCancel = {
                        dragDropListState.onDragInterrupted()
                    }
                )
            }
    ) {
        itemsIndexed(
            items = list,
            key = { index, item -> item.id }
        ) { index, item ->
            Box(
                modifier = Modifier
                    .animateItemPlacement()
                    .graphicsLayer {
                        val offsetOrNull = dragDropListState.elementDisplacement.takeIf {
                            index == dragDropListState.currentIndexOfDraggedItem
                        }
                        translationY = offsetOrNull ?: 0f
                    }
                    .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Item ${item.title}",
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
     */
}

data class ReorderItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String
) {
    companion object {
        fun getList(): List<ReorderItem> {
            return (1..10).map {
                ReorderItem(
                    title = it.toString()
                )
            }
        }
    }
}
