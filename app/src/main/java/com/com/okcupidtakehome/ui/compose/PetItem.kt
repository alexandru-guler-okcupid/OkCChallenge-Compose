package com.com.okcupidtakehome.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.com.okcupidtakehome.R
import com.com.okcupidtakehome.models.Pet
import com.com.okcupidtakehome.theme.Black
import com.com.okcupidtakehome.theme.Black50
import com.com.okcupidtakehome.theme.Liked
import com.com.okcupidtakehome.theme.OkCupidTakeHomeTheme
import com.com.okcupidtakehome.theme.Purple500
import com.com.okcupidtakehome.theme.White
import com.com.okcupidtakehome.ui.PetCard
import com.com.okcupidtakehome.ui.testPetCard

@Composable
fun PetItem(
    petCard: PetCard,
    onPetSelected: (Pet) -> Unit,
    onPetCancelled: (Pet) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable {
                if (!petCard.isLoading) {
                    onPetSelected.invoke(petCard.pet)
                }
            },
        shape = RoundedCornerShape(5.dp),
        backgroundColor = if (petCard.pet.liked) Liked else White,
        elevation = 5.dp,
    ) {
        val context = LocalContext.current
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(petCard.pet.photo.original)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = petCard.pet.userName,
                color = Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = context.getString(
                    R.string.location,
                    petCard.pet.age,
                    petCard.pet.location.cityName,
                    petCard.pet.location.stateName
                ),
                color = Black50,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = context.getString(R.string.pet_match, petCard.pet.matchPerc),
                color = Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            if (petCard.isLoading) {
                Button(
                    onClick = { onPetCancelled.invoke(petCard.pet) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Purple500
                    )
                ) {
                    Text(
                        text = context.getString(R.string.cancel),
                        color = White,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Preview(
    name = "Pets Item",
    showBackground = true
)
@Composable
fun Preview_PetItem() {
    OkCupidTakeHomeTheme {
        PetItem(
            petCard = testPetCard,
            onPetSelected = {},
            onPetCancelled = {}
        )
    }
}
