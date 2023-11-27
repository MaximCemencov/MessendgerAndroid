package com.example.androidapp.viewModels.MessagesViewModel.functions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

fun decodeImage(fileName: String, appDir: File): Bitmap? {
    val file = File(appDir, fileName)

    return try {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        BitmapFactory.decodeFile(file.path, options)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}