package com.com.okcupidtakehome.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun AnimationPlayground() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Hello here we will test animations",
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
