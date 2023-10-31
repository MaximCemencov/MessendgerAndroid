package com.example.androidapp.features

import android.content.Context

fun getUserIdFromSharedPreferences(context: Context): Int {
    val sharedPreferences = context.getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
    return sharedPreferences.getInt("userId", 0)
}