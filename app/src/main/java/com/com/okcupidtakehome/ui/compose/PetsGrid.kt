package com.com.okcupidtakehome.ui.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.com.okcupidtakehome.models.Pet
import com.com.okcupidtakehome.theme.OkCupidTakeHomeTheme
import com.com.okcupidtakehome.ui.PetCard
import com.com.okcupidtakehome.ui.testPetCard

@Composable
fun PetsGrid(
    pets: List<PetCard>,
    modifier: Modifier = Modifier,
    onPetSelected: (Pet) -> Unit,
    onPetCancelled: (Pet) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(bottom = 10.dp)
    ) {
        items(pets) { pet ->
            PetItem(
                petCard = pet,
                onPetSelected = onPetSelected,
                onPetCancelled = onPetCancelled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(
    name = "Pets Grid",
    showBackground = true
)
@Composable
fun Preview_PetsGrid() {
    OkCupidTakeHomeTheme {
        PetsGrid(
            pets = listOf(
                testPetCard,
                testPetCard.copy(pet = testPetCard.pet.copy(userName = "Name 2")),
                testPetCard.copy(pet = testPetCard.pet.copy(userName = "Name 3")),
                testPetCard.copy(pet = testPetCard.pet.copy(userName = "Name 4")),
            ),
            onPetSelected = {},
            onPetCancelled = {}
        )
    }
}
