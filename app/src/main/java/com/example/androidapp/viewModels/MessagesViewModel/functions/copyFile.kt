package com.example.androidapp.viewModels.MessagesViewModel.functions

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

fun copyFile(sourceFile: File, appDir: File) {
    val destinationFile = File(appDir, sourceFile.name)

    try {
        FileInputStream(sourceFile).channel.use { source ->
            FileOutputStream(destinationFile).channel.use { destination ->
                destination.transferFrom(source, 0, source.size())
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}