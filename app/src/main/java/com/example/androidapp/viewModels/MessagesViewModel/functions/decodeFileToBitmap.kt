package com.example.androidapp.viewModels.MessagesViewModel.functions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File

fun decodeFileToBitmap(file: File?): Bitmap? {
    return file?.let {
        try {
            BitmapFactory.decodeFile(it.absolutePath)
        } catch (e: Exception) {
            Log.e("DecodeBitmap", "Error decoding bitmap: ${e.message}")
            null
        }
    }
}