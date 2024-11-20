package com.example.mipapalotedigital.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import com.example.mipapalotedigital.R

val imageResources = listOf(
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement,
    R.drawable.vgachievement
    /*R.drawable.image2,
    R.drawable.image3,
    R.drawable.image4,
    R.drawable.image5,
    R.drawable.image6,*/
    // ... more image resources
)

val grayScaleMatrix = floatArrayOf(
    0.33f, 0.33f, 0.33f, 0f, 0f,
    0.33f, 0.33f, 0.33f, 0f, 0f,
    0.33f, 0.33f, 0.33f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f
)
val grayScaleColorFilter = ColorFilter.colorMatrix(ColorMatrix(grayScaleMatrix))


@Composable
fun AlbumLogroScreen(){
    ImageGrid(imageResources, isGrayScale = true) // Display images in grayscale
}

@Composable
fun ImageRow(imageResources: List<Int>, isGrayScale: Boolean = false) {
    Row {
        imageResources.forEach { imageResource ->
            Image(
                painter = painterResource(id = imageResource),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                colorFilter = if (isGrayScale) {
                    grayScaleColorFilter
                } else {
                    null
                }
            )
        }
    }
}

@Composable
fun ImageGrid(imageResources: List<Int>, isGrayScale: Boolean = false) {
    LazyColumn {
        items(imageResources.chunked(3)) { rowImages ->
            ImageRow(rowImages, isGrayScale)
        }
    }
}