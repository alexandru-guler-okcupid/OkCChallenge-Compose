package com.com.okcupidtakehome.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.com.okcupidtakehome.R
import com.com.okcupidtakehome.theme.ProgressBarColor
import com.com.okcupidtakehome.theme.White
import com.com.okcupidtakehome.ui.MainComposeViewModel
import com.com.okcupidtakehome.ui.UiState

@Composable
fun SpecialBlendScreen(viewModel: MainComposeViewModel) {
    when (val state = viewModel.uiState.observeAsState(UiState.Loading).value) {
        UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = ProgressBarColor,
                )
            }
        }
        UiState.ShowError -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Button(
                    onClick = viewModel::getPets,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ProgressBarColor
                    )
                ) {
                    Text(
                        text = LocalContext.current.getString(R.string.retry),
                        color = White
                    )
                }
            }
        }
        is UiState.UpdateList -> {
            PetsGrid(
                pets = state.pets,
                onPetSelected = viewModel::petSelected,
                onPetCancelled = viewModel::onPetCancelled,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
