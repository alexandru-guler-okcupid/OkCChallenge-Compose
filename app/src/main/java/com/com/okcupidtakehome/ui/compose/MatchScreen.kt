package com.com.okcupidtakehome.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.com.okcupidtakehome.ui.MainComposeViewModel
import com.com.okcupidtakehome.ui.UiState

@Composable
fun MatchScreen(viewModel: MainComposeViewModel) {
    when (val state = viewModel.uiState.observeAsState().value) {
        is UiState.UpdateList -> {
            PetsGrid(
                pets = state.topPets,
                onPetSelected = viewModel::petSelected,
                onPetCancelled = {}
            )
        }
        else -> Unit // Match Screen only needs the "topPets" list
    }
}
