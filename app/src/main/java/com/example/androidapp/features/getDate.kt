package com.example.androidapp.features

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

fun getCurrentDateTimeInUTC(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    sdf.timeZone = TimeZone.getTimeZone("GMT") // Используем GMT (часовой пояс Гринвича) напрямую
    return sdf.format(Date())
}