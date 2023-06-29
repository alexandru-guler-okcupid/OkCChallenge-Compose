package com.com.okcupidtakehome.ui.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.sp

private const val TAG = "DraggableList"

private const val scaleSelected = 1.2f
private const val alphaSelected = 0.85f

/**
 * TODO: need a comment talking about moving around the "fake" item in order to hide the weird [animateItemPlacement]
 *  animate while dragging
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableList(
    state: DragDropState,
    onMove: (Int, Int) -> Unit,
    canBeMoved: (index: Int) -> Boolean,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val dragDropGridListState = rememberDragDropGridListState(
            onMove = onMove,
            canBeMoved = canBeMoved
        )

        val animScale by animateFloatAsState(if (dragDropGridListState.currentElement != null) scaleSelected else 1f)
        val alphaScale by animateFloatAsState(if (dragDropGridListState.currentElement != null) alphaSelected else 1f)

        val allAroundPadding = 6.dp
        LazyVerticalGrid(
            state = dragDropGridListState.lazyGridState,
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
                            dragDropGridListState.onDrag(offset)
                        },
                        onDragStart = dragDropGridListState::onDragStart,
                        onDragEnd = dragDropGridListState::onDragInterrupted,
                        onDragCancel = dragDropGridListState::onDragInterrupted
                    )
                }
        ) {
            itemsIndexed(
                items = state.list,
                key = { _, item -> item.id }
            ) { index, item ->
                when (item) {
                    is ReorderItem.Photo -> Box(
                        modifier = Modifier
                            .animateItemPlacement()
                            .graphicsLayer {
                                val offsetOrNull =
                                    dragDropGridListState.positionalDraggedDistance.takeIf {
                                        index == dragDropGridListState.currentIndexOfDraggedItem
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
                    ReorderItem.Add -> AddButton(
                        onClick = onAdd,
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                    )
                }
            }
        }
        val currentElement = dragDropGridListState.currentElement
        if (currentElement != null) {
            val item = state.list[currentElement.index] as? ReorderItem.Photo
            if (item != null) {
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
                            val offsetOrNull = dragDropGridListState.positionalDraggedDistance
                            scaleX = animScale
                            scaleY = animScale
                            alpha = alphaScale
                            translationX = offsetOrNull.x.toFloat()
                            translationY = offsetOrNull.y.toFloat()
                        }
                        .size(
                            width = width,
                            height = height
                        )
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
    }
}

@Composable
private fun AddButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(Color.LightGray)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = "+",
            color = Color.Black,
            fontSize = 36.sp,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}
