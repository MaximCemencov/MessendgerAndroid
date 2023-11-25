package com.example.androidapp.features

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

fun generateRandomColor(): Color {
    val random = Random.Default
    val red = random.nextFloat()
    val green = random.nextFloat()
    val blue = random.nextFloat()
    return Color(red, green, blue)
}
