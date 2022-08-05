package com.com.okcupidtakehome.ui.compose

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.com.okcupidtakehome.ui.MainComposeViewModel
import com.com.okcupidtakehome.ui.UiState

@Composable
fun SpecialBlendScreen(viewModel: MainComposeViewModel) {
    when (val state = viewModel.uiState.observeAsState(UiState.Loading).value) {
        UiState.Loading -> {
            Log.d("TAG", "SpecialBlendScreen: UiState.Loading")
            Text(text = "Loading")
        }
        UiState.ShowError -> {
            Log.d("TAG", "SpecialBlendScreen: UiState.ShowError")
            Text(text = "Showing Error")
        }
        is UiState.UpdateList -> {
            Log.d("TAG", "SpecialBlendScreen: is UiState.UpdateList")
            PetsGrid(
                pets = state.pets,
                onPetSelected = viewModel::petSelected,
                onPetCancelled = viewModel::onPetCancelled,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
