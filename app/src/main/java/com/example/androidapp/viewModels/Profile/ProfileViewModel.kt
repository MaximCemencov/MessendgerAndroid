package com.example.androidapp.viewModels.Profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.features.mainUrl
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

class ProfileViewModel(
    private val context: Context,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {
    fun base64ToImageBitmap(base64: String): Bitmap? {
        try {
            val imageBytes = Base64.decode(base64, Base64.DEFAULT)

            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun uriToCompressedBase64(
        context: Context,
        uri: Uri,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ): String? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)

            // Декодируем изображение из InputStream в Bitmap
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, options)

            // Если изображение превышает максимальные размеры, устанавливаем inSampleSize на 4
            if (options.outWidth > maxWidth || options.outHeight > maxHeight) {
                options.inSampleSize = 8
            }

            // Закрываем текущий InputStream и открываем новый
            inputStream?.close()
            val newInputStream = context.contentResolver.openInputStream(uri)

            // Загружаем изображение с учетом масштаба
            options.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeStream(newInputStream, null, options)

            // Компрессия изображения в формат JPEG с заданным качеством
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

            // Кодируем сжатое изображение в Base64
            val bytes = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun sendNewAvatarToServer(avatarUri: Uri) {
        val jsonBody = JSONObject()

        val base64 = uriToCompressedBase64(context, avatarUri, 1500, 1500, 100)

        jsonBody.put("user_id", sharedViewModel.userId)
        jsonBody.put("login", sharedViewModel.login)
        jsonBody.put("password", sharedViewModel.password)
        jsonBody.put("file", base64)

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$mainUrl/avatar_upload")
            .post(requestBody)
            .build()

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    sharedViewModel.httpsClient.newCall(request).execute()
                }

                if (response.isSuccessful) {
                    sharedViewModel.avatarBase64 = base64
                    sharedViewModel.saveToSharedPreferences()
                    Log.d("aboba", response.toString())
                }
            } catch (e: IOException) {
                Log.e("aboba", e.toString())
            }
        }
    }

    suspend fun getAvatarFromServer(userId: Int): String? {
        val jsonBody = JSONObject()
        jsonBody.put("user_id", userId)
        var byte64: String? = null


        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$mainUrl/avatar_download")
            .post(requestBody)
            .build()


        withContext(Dispatchers.IO) {
            val response = sharedViewModel.httpsClient.newCall(request).execute()
            Log.d("aboba", response.toString())
            if (response.isSuccessful) {
                val data = response.body?.string()
                val jsonArray = JSONObject(data!!)

                byte64 = jsonArray.getString("avatar")
            }
        }


        return byte64
    }

    fun removeAvatar() {
        val jsonBody = JSONObject()

        jsonBody.put("user_id", sharedViewModel.userId)
        jsonBody.put("login", sharedViewModel.login)
        jsonBody.put("password", sharedViewModel.password)


        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$mainUrl/avatar_delete")
            .post(requestBody)
            .build()


        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    sharedViewModel.httpsClient.newCall(request).execute()
                }

                if (response.isSuccessful) {
                    sharedViewModel.avatarBase64 = null
                    sharedViewModel.saveToSharedPreferences()
                }

            } catch (_: IOException) {
            }
        }
    }

    init {
         viewModelScope.launch {
             sharedViewModel.avatarBase64 = getAvatarFromServer(sharedViewModel.userId)
             sharedViewModel.saveToSharedPreferences()
        }
    }

}