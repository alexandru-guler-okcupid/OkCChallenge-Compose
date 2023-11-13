package com.com.okcupidtakehome.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun MiscTestingGround() {
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Text(
            text = "Will be testing QR codes here",
            color = Color.Black,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


//@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
//@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//private fun MiscTestingGround_Preview() {
//    OkCupidTakeHomeTheme {
//        MiscTestingGround()
//    }
//}
