package com.example.androidapp.viewModels.MessagesViewModel.functions

import java.io.File

fun isFileExistsInFolder(fileName: String, appDir: File): Boolean {
    val file = File(appDir, fileName)
    return file.exists()
}