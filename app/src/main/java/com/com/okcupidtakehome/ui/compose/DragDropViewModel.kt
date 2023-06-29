package com.com.okcupidtakehome.ui.compose

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class DragDropViewModel @Inject constructor() : ViewModel() {

    private val _state: MutableStateFlow<DragDropState> = MutableStateFlow(
        DragDropState(
            list = ReorderItem.getList()
        )
    )
    val state: StateFlow<DragDropState> = _state

    fun onMove(from: Int, to: Int) {
        _state.update {
            val mutableList = it.list.toMutableList()
            mutableList.move(from, to)
            it.copy(
                list = mutableList
            )
        }
    }

    fun canBeMoved(index: Int): Boolean {
        val currentList = state.value.list
        return if (index in currentList.indices) {
            currentList[index] is ReorderItem.Photo
        } else {
            Log.w(TAG, "Index out of bounds! index: $index, size: ${currentList.size} ")
            false
        }
    }

    fun onAdd() {
        _state.update {
            val mutableList = it.list.toMutableList()
            mutableList.add(
                it.list.size - 1,
                ReorderItem.Photo(
                    title = "${it.list.size + 1}"
                )
            )
            it.copy(
                list = mutableList
            )
        }
    }

    companion object {
        private const val TAG = "DragDropViewModel"
    }
}

data class DragDropState(
    val list: List<ReorderItem>
)

fun <T> MutableList<T>.move(from: Int, to: Int) {
    if (from == to)
        return

    val element = this.removeAt(from) ?: return
    this.add(to, element)
}

sealed class ReorderItem {
    abstract val id: String

    data class Photo(
        override val id: String = UUID.randomUUID().toString(),
        val title: String
    ) : ReorderItem()

    object Add : ReorderItem() {
        override val id: String = UUID.randomUUID().toString()
    }

    companion object {
        fun getList(): List<ReorderItem> {
            val list = mutableListOf<ReorderItem>()
            list.addAll(
                (1..5).map {
                    Photo(
                        title = it.toString()
                    )
                }
            )
            list.add(Add)
            return list
        }
    }
}
