package com.example.androidapp.DataClass


data class Message(
    val content: String,
    val owner: Int,
    val timeStamp: String,
    val hasYour: Boolean
)

data class MenuItem(
    val id: Int,
    val name: String
)
