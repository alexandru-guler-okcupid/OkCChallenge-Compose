package com.com.okcupidtakehome.ui.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import java.util.UUID

private const val TAG = "DraggableList"

private const val scaleSelected = 1.2f
private const val alphaSelected = 0.85f

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableList(
    list: List<ReorderItem>,
    onMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val dragDropListState = rememberDragDropListState(onMove = onMove)

        val animScale by animateFloatAsState(if (dragDropListState.currentElement != null) scaleSelected else 1f)
        val alphaScale by animateFloatAsState(if (dragDropListState.currentElement != null) alphaSelected else 1f)

        val allAroundPadding = 6.dp
        LazyVerticalGrid(
            state = dragDropListState.lazyGridState,
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(vertical = allAroundPadding),
            verticalArrangement = Arrangement.spacedBy(allAroundPadding),
            horizontalArrangement = Arrangement.spacedBy(allAroundPadding),
            modifier = modifier
                .padding(horizontal = allAroundPadding)
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDrag = { change, offset ->
                            change.consume()
                            dragDropListState.onDrag(offset)
                        },
                        onDragStart = dragDropListState::onDragStart,
                        onDragEnd = dragDropListState::onDragInterrupted,
                        onDragCancel = dragDropListState::onDragInterrupted
                    )
                }
        ) {
            itemsIndexed(
                items = list,
                key = { _, item -> item.id }
            ) { index, item ->
                Box(
                    modifier = Modifier
                        .animateItemPlacement()
                        .graphicsLayer {
                            val offsetOrNull = dragDropListState.elementDisplacement.takeIf {
                                index == dragDropListState.currentIndexOfDraggedItem
                            }
                            alpha = if (offsetOrNull != null) 0.0f else 1.0f
                            translationX = offsetOrNull?.x?.toFloat() ?: 0f
                            translationY = offsetOrNull?.y?.toFloat() ?: 0f
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
        val currentElement = dragDropListState.currentElement
        if (currentElement != null) {
            val localDensity = LocalDensity.current
            val (width, height) = with(localDensity) {
                currentElement.size.width.toDp() to currentElement.size.height.toDp()
            }
            Box(
                modifier = Modifier
                    .offset {
                        currentElement.offset + with(localDensity) {
                            IntOffset(
                                x = allAroundPadding.roundToPx(),
                                y = allAroundPadding.roundToPx()
                            )
                        }
                    }
                    .graphicsLayer {
                        val offsetOrNull = dragDropListState.elementDisplacement
                        scaleX = animScale
                        scaleY = animScale
                        alpha = alphaScale
                        translationX = offsetOrNull?.x?.toFloat() ?: 0f
                        translationY = offsetOrNull?.y?.toFloat() ?: 0f
                    }
                    .size(
                        width = width,
                        height = height
                    )
                    .background(Color.White)
            ) {
                val item = list[currentElement.index]
                Text(
                    text = "Item ${item.title}",
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
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
