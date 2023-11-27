package com.example.androidapp.viewModels.MessagesViewModel.functions

import com.example.androidapp.FileTypes.FileType
import java.util.Locale

fun getFileType(uri: String): FileType {
    val fileExtension = uri.substringAfterLast(".")
    return when (fileExtension.lowercase(Locale.ROOT)) {
        "jpg", "jpeg", "png", "gif" -> FileType.image
        "mp4", "mkv", "avi" -> FileType.video
        "mp3", "wav", "ogg", "m4a" -> FileType.music
        else -> FileType.other
    }
}