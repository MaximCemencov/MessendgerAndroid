package com.example.androidapp.viewModels.MessagesViewModel.functions

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun createFileFromInputStream(
    inputStream: InputStream?,
    directory: File,
    fileName: String
): File? {
    val file = File(directory, fileName)

    if (file.exists()) {
        return null
    }

    inputStream?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }

    return file
}