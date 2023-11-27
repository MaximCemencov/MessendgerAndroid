package com.example.androidapp.viewModels.MessagesViewModel.functions

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

fun getBitmapState(fileName: String, bitmap:  MutableMap<String, MutableState<Bitmap?>>): MutableState<Bitmap?> {
    return bitmap.getOrPut(fileName) {
        mutableStateOf(null)
    }
}