package com.example.androidapp.DataClass


data class Message(
    val id: Int,
    val content: String,
    val owner: Int,
    val timeStamp: String,
    val hasYour: Boolean,
    var hasViewed: Boolean,
    val hasEdited: Boolean,
    val fileName: String,
    val fileType: String
)

data class MenuItem(
    val id: Int,
    val name: String
)
