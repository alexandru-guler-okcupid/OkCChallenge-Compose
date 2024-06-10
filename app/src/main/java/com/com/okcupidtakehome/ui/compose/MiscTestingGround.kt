package com.com.okcupidtakehome.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiscTestingGround() {

    val bottomSheetState: SheetState = rememberStandardBottomSheetState()
    val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState
    )

    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetContainerColor = Color.Blue,
        containerColor = Color.White,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(800.dp)
            )
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                Text(
                    text = "Image would go here",
                    fontSize = 26.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/*

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .background(Color.Gray)
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            Text(
                text = "Image would go here",
                fontSize = 26.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Button(
            onClick = {

            }
        ) {
            Text(
                text = "Open",
                fontSize = 26.sp,
                color = Color.White
            )
        }

        val sheetState: SheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        var showBottomSheet by remember { mutableStateOf(false) }
        ModalBottomSheet(
            onDismissRequest = {
                Log.d("TAG", "alex: on onDismissRequest")
            },
            sheetState = sheetState
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Blue)
                    .height(800.dp)
            )
        }
    }


 */


/*
   val state = rememberModalBottomSheetState(
       initialValue = ModalBottomSheetValue.HalfExpanded,
       confirmValueChange = { modalBottomSheetValue ->
           modalBottomSheetValue != ModalBottomSheetValue.Hidden
       }
   )
   ModalBottomSheetLayout(
       modifier = Modifier.fillMaxSize(),
       sheetState = state,
       sheetContent = {
           Box(
               modifier = Modifier
                   .fillMaxWidth()
                   .background(Color.Blue)
                   .height(800.dp)
           )
       },
   ) {
       Box(
           modifier = Modifier
               .fillMaxSize()
               .background(Color.White)
       ) {

           Box(
               modifier = Modifier
                   .background(Color.Gray)
                   .fillMaxWidth()
                   .aspectRatio(1f)
           ) {
               Text(
                   text = "Image would go here",
                   fontSize = 26.sp,
                   color = Color.White,
                   modifier = Modifier.align(Alignment.Center)
               )
           }
       }
   }
    */
