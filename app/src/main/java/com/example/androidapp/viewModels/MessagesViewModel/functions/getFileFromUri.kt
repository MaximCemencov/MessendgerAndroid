package com.example.androidapp.viewModels.MessagesViewModel.functions

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.IOException


fun getFileFromUri(uri: Uri, context: Context): File? {
    val contentResolver = context.contentResolver
    val displayName = getDisplayName(contentResolver, uri)
    val file = File(context.cacheDir, displayName ?: "file")
    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
    return file
}