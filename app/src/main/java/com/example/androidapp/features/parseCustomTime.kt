package com.example.androidapp.features

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

fun parseCustomTime(inputString: String): String? {
    try {
        // Создаем SimpleDateFormat для разбора времени из строки
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Устанавливаем временную зону UTC

        // Разбираем строку в объект Date
        val date = inputFormat.parse(inputString)

        // Добавляем 3 часа к времени
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR_OF_DAY, 3)

        // Создаем SimpleDateFormat для форматирования времени в "HH:mm"
        val outputFormat = SimpleDateFormat("HH:mm")

        // Форматируем и возвращаем время
        return outputFormat.format(calendar.time)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}


