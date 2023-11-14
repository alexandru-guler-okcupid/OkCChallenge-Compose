package com.com.okcupidtakehome.ui.compose

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

sealed class QRCodeState {
    data object Idle : QRCodeState()
    data object Running : QRCodeState()
    data object Done : QRCodeState()
    data object Error : QRCodeState()
}

@Composable
fun MiscTestingGround(

) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var qrCodeState by remember { mutableStateOf<QRCodeState>(QRCodeState.Idle) }

    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxSize()
    ) {
        Text(
            text = "Will be testing QR codes here",
            color = Color.Black,
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(20.dp)
        )

        Text(
            text = "$qrCodeState",
            color = Color.Black,
            fontSize = 21.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(20.dp)
        )

        Spacer(modifier = Modifier.size(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val bitmap = imageBitmap
            if (bitmap != null) {
                Log.d("TAG", "alex: setting bitmap: $bitmap")
                Image(
                    bitmap = bitmap,
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            } else {
                Text(
                    text = "QR code will go here",
                    color = Color.Black,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(20.dp)
                )
            }
        }

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                qrCodeState = QRCodeState.Running
//                val qrCodeBitmap = createQRBitmapFromUrl("https://www.google.com/")
                val qrCodeBitmap = createQRBitmapFromUrl("https://www.archerapp.com/")
                if (qrCodeBitmap != null) {
                    qrCodeState = QRCodeState.Done
                    imageBitmap = qrCodeBitmap

                    Log.d("TAG", "alex: MiscTestingGround: got back imageBitmap: $qrCodeBitmap")
                    Log.d("TAG", "alex: qrCodeBitmap.height: ${qrCodeBitmap.height}")
                    Log.d("TAG", "alex: qrCodeBitmap.width: ${qrCodeBitmap.width}")

                } else {
                    qrCodeState = QRCodeState.Error
                }
            }
        ) {
            Text(
                text = "Generate",
                color = Color.Black,
                fontSize = 24.sp,
                modifier = Modifier.padding(20.dp)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

    }
}

private fun createQRBitmapFromUrl(url: String): ImageBitmap? {

//    String text="" // Whatever you need to encode in the QR code
//    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
//    try {
//        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
//        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
//        imageView.setImageBitmap(bitmap);
//    } catch (WriterException e) {
//        e.printStackTrace();
//    }

    val multiFormatWriter = MultiFormatWriter()
    return try {
        val bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, 1500, 1500)
        createBitmap(bitMatrix).asImageBitmap()
    } catch (e: Exception) {
        Log.e("TAG", "error trying to make QR code", e)
        null
    }
}

/**
 * This was copied directly from here:
 * https://github.com/journeyapps/zxing-android-embedded/blob/master/zxing-android-embedded/src/com/journeyapps/barcodescanner/BarcodeEncoder.java
 *
 * Because I don't want to add another dependency.
 */
private fun createBitmap(matrix: BitMatrix): Bitmap {
    val width = matrix.width
    val height = matrix.height
    val bgColor = 0xFFFFFFFF
    val fgColor = 0xFF000000
    val pixels = IntArray(width * height)
    for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
            pixels[offset + x] = if (matrix.get(x, y)) {
                fgColor.toInt()
            } else {
                bgColor.toInt()
            }
        }
    }

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}

/*
 public Bitmap createBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? fgColor : bgColor;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
 */


//@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
//@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//private fun MiscTestingGround_Preview() {
//    OkCupidTakeHomeTheme {
//        MiscTestingGround()
//    }
//}
