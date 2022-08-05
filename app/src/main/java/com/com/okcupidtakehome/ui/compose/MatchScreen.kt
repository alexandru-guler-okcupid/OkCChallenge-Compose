package com.com.okcupidtakehome.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.com.okcupidtakehome.ui.MainComposeViewModel
import com.com.okcupidtakehome.ui.MatchUiState

@Composable
fun MatchScreen(viewModel: MainComposeViewModel) {
    when (val state = viewModel.matchUiState.observeAsState().value) {
        is MatchUiState.UpdateTopPetsList -> {
            PetsGrid(
                pets = state.pets,
                onPetSelected = viewModel::petSelected,
                onPetCancelled = {}
            )
        }
        null -> Unit
    }
}
