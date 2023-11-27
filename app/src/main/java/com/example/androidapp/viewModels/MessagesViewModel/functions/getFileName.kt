package com.example.androidapp.viewModels.MessagesViewModel.functions

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun getFileName(uri: Uri, context: Context): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (displayNameIndex != -1) {
                val displayName = it.getString(displayNameIndex)
                if (!displayName.isNullOrBlank()) {
                    return displayName
                }
            }
        }
    }
    return "file"
}