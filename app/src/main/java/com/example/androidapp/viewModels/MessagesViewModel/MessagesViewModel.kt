package com.example.androidapp.viewModels.MessagesViewModel

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.DataClass.Message
import com.example.androidapp.features.getCurrentDateTimeInUTC
import com.example.androidapp.features.limit
import com.example.androidapp.features.mainUrl
import com.example.androidapp.features.parseCustomTime
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MessagesViewModel(
    private val sharedViewModel: SharedViewModel,
    private val lazyListState: LazyListState,
    private val coroutineScope: CoroutineScope,
) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

//    var selectedFiles = mutableStateListOf<Pair<Uri, FileType>>()

    val textState = mutableStateOf("")

    var currentPage by mutableIntStateOf(0)
    val isLoading = mutableStateOf(false)

    val isEdit = mutableStateOf(false)
    val editedTextState = mutableStateOf("")
    val editedFinished = mutableStateOf(false)
//    val appDir = File(context.filesDir, "messenger_folder")


    fun clearMessages() {
        if (_messages.value.isNotEmpty()) {
            _messages.value = emptyList()
        }
    }

//    fun getFileUri(fileName: String): Uri? {
//        val file = File(appDir, fileName)
//
//        return if (file.exists()) {
//            // Передайте в FileProvider контекст вашего приложения и имя файла
//            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
//        } else {
//            null
//        }
//    }
//    fun getFileType(uri: String): FileType {
//        val fileExtension = uri.substringAfterLast(".")
//        return when (fileExtension.lowercase(Locale.ROOT)) {
//            "jpg", "jpeg", "png", "gif" -> FileType.image
//            "mp4", "mkv", "avi" -> FileType.video
//            "mp3", "wav", "ogg", "m4a" -> FileType.music
//            else -> FileType.other
//        }
//    }
//    fun getFileName(uri: Uri): String {
//        val cursor = context.contentResolver.query(uri, null, null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                if (displayNameIndex != -1) {
//                    val displayName = it.getString(displayNameIndex)
//                    if (!displayName.isNullOrBlank()) {
//                        return displayName
//                    }
//                }
//            }
//        }
//        return "file"
//    }
//    fun isFileExistsInDirectory(fileName: String): Boolean {
//        val filePath = File(appDir, fileName)
//        return filePath.exists()
//    }

    suspend fun getMessages(offset: Int, lazyListState: LazyListState, coroutineScope: CoroutineScope) {
        val jsonBody = JSONObject()
        jsonBody.put("chat_id", sharedViewModel.currentChatId)
        jsonBody.put("offset", offset)
        jsonBody.put("id", sharedViewModel.userId)
        jsonBody.put("login", sharedViewModel.login)
        jsonBody.put("password", sharedViewModel.password)

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$mainUrl/get_past_messages")
            .post(requestBody)
            .build()

        try {
            val data = withContext(Dispatchers.IO) {
                val response = sharedViewModel.httpsClient.newCall(request).execute()
                if (response.isSuccessful)
                    response.body?.string()
                else
                    null
            }

            if (data != null) {
                val jsonArray = JSONArray(data)

                val newMessages = mutableListOf<Message>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    val messageId = jsonObject.getInt("id")
                    val senderId = jsonObject.getInt("sender_id")
                    val content = jsonObject.getString("content")
                    val timeStamp = jsonObject.getString("time_stamp")
                    val hasViewed = jsonObject.getBoolean("is_readed")
                    val hasEdited = jsonObject.getBoolean("is_edited")
//                    val fileName = jsonObject.getString("originalfile")
//                    val file = jsonObject.getString("file_type")

                    val message = Message(
                        messageId,
                        content,
                        senderId,
                        parseCustomTime(timeStamp)!!,
                        senderId == sharedViewModel.userId,
                        hasViewed,
                        hasEdited,
//                        fileName,
//                        file
                    )
                    newMessages.add(message)
                }

                _messages.value = _messages.value + newMessages


                coroutineScope.launch {
                    if (offset == 0) {
                        val lastIndex = _messages.value.size - 1
                        lazyListState.scrollToItem(lastIndex)
                    } else {
                        lazyListState.scrollToItem(limit + 1)
                    }
                }
            }
        } catch (e: IOException) {
            Log.d("advadsva", e.toString())
        }
    }

    fun newMessage(messageObject: JSONObject) {
        val messageId = messageObject.getInt("message_id")
        val senderId = messageObject.getInt("sender_id")
        val chatId = messageObject.getInt("chat_id")
        val timeStamp = messageObject.getString("time_of_day")
        val content = messageObject.getString("content")
        val hasViewed = messageObject.getBoolean("is_readed")
        val hasEdited = messageObject.getBoolean("is_edited")


        if (chatId != sharedViewModel.currentChatId) {
            return
        }
        val newMessage = Message(
            messageId,
            content,
            senderId,
            parseCustomTime(timeStamp)!!,
            sharedViewModel.userId == senderId,
            hasViewed,
            hasEdited,
//            "null",
//            "null"
        )

        _messages.value = listOf(newMessage) + _messages.value
        coroutineScope.launch {
            val lastIndex = _messages.value.size - 1
            lazyListState.scrollToItem(lastIndex)
        }

//        if (selectedFiles.isNotEmpty()) {
//            val jsonBase64 = JSONObject()
//            jsonBase64.put("base64", uriToBase64(selectedFiles[0].first))
//            jsonBase64.put("id", messageId)
//            jsonBase64.put("type", "new_file_message")
//            sharedViewModel.webSocket.send(jsonBase64.toString())
//            selectedFiles.removeAt(0)
//        }
    }

    fun updatedMessage(messageObject: JSONObject) {
        val messageId = messageObject.getInt("message_id")
        val senderId = messageObject.getInt("sender_id")
        val chatId = messageObject.getInt("chat_id")
        val timeStamp = messageObject.getString("time_of_day")
        val newContent = messageObject.getString("new_content")
        val hasEdited = messageObject.getBoolean("is_edited")
//        val fileName = messageObject.getString("file")
//        val fileType = messageObject.getString("fyletype")

//        Log.d("avopa", messageObject.toString())

        val updatedMessageIndex = _messages.value.indexOfFirst { it.id == messageId }

        if (chatId != sharedViewModel.currentChatId || updatedMessageIndex == -1) {
            return
        }

        val mess = Message(
            id = messageId,
            content = newContent,
            owner = senderId,
            timeStamp = parseCustomTime(timeStamp)!!,
            hasYour = sharedViewModel.userId == senderId,
            hasViewed = false,
            hasEdited = hasEdited,
//            fileName = if (selectedFiles.isNotEmpty()) fileName else "null",
//            fileType = if (selectedFiles.isNotEmpty()) fileType else "null"
        )

        _messages.value = _messages.value.toMutableList().apply {
            set(
                updatedMessageIndex,
                mess
            )
        }

//        downloadFile(mess)
    }

    fun deleteMessage(messageObject: JSONObject) {
        val messageId = messageObject.getInt("message_id")

        _messages.value = _messages.value.filterNot { it.id == messageId }
    }

    fun sendMessage() {
//        if (selectedFiles.isEmpty()) {
            if (textState.value.isBlank()) return
//        }

        val jsonBody = JSONObject()
        sharedViewModel.loadFromSharedPreferences()
        jsonBody.put("chat_id", sharedViewModel.currentChatId)
        jsonBody.put("sender_id", sharedViewModel.userId)
        jsonBody.put("recipient_id", sharedViewModel.userId2)
        jsonBody.put("content", textState.value)
        textState.value = ""
        jsonBody.put("time_of_day", getCurrentDateTimeInUTC())
        jsonBody.put("type", "new_message")

//        if (selectedFiles.isNotEmpty()) {
//            jsonBody.put("originalfile", getFileName(selectedFiles[0].first))
//            jsonBody.put("file_type", selectedFiles[0].second)
//        }
        sharedViewModel.webSocket.send(jsonBody.toString())
    }

//    private val lock = Object()
//    suspend fun uriToImageBitmap(uri: Uri): ImageBitmap {
//        return withContext(Dispatchers.IO) {
//            synchronized(lock) {
//                try {
//                    Log.d("YourTag", "Start decoding image from URI: $uri")
//
//                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
//
//                    if (inputStream == null) {
//                        Log.e("YourTag", "InputStream is null")
//                        throw IllegalArgumentException("Failed to open InputStream")
//                    }
//
//                    Log.d("YourTag", "InputStream opened successfully")
//
//                    val bitmap = BitmapFactory.decodeStream(inputStream)
//                    inputStream.close() // закрываем поток после использования
//
//                    Log.d("YourTag", "Image decoded successfully")
//
//                    if (bitmap == null) {
//                        Log.e("YourTag", "Decoded bitmap is null")
//                        throw IllegalArgumentException("Failed to decode bitmap")
//                    }
//
//                    return@withContext bitmap.asImageBitmap()
//                } catch (e: IOException) {
//                    Log.e("YourTag", "Error opening InputStream", e)
//                    throw IllegalArgumentException("Failed to open InputStream", e)
//                } catch (e: OutOfMemoryError) {
//                    Log.e("YourTag", "Out of memory", e)
//                    throw IllegalArgumentException("Out of memory", e)
//                }
//            }
//        }
//    }
//    fun uriToBase64(fileUri: Uri): String {
//        val contentResolver: ContentResolver = context.contentResolver
//        val inputStream: InputStream? = contentResolver.openInputStream(fileUri)
//
//        val outputStream = ByteArrayOutputStream()
//
//        inputStream?.use { input ->
//            val buffer = ByteArray(4096)
//            var bytesRead: Int
//            while (input.read(buffer).also { bytesRead = it } != -1) {
//                outputStream.write(buffer, 0, bytesRead)
//            }
//        }
//
//        val byteArray = outputStream.toByteArray()
//        return Base64.encodeToString(byteArray, Base64.DEFAULT)
//    }
//
//    fun downloadFile(mess: Message) {
//        val jsonBody = JSONObject()
//        jsonBody.put("message_id", mess.id)
//        jsonBody.put("user_id", sharedViewModel.userId)
//        jsonBody.put("login", sharedViewModel.login)
//        jsonBody.put("password", sharedViewModel.password)
//
//        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
//        val requestBody = jsonBody.toString().toRequestBody(mediaType)
//
//        val request = Request.Builder()
//            .url("$mainUrl/file_download")
//            .post(requestBody)
//            .build()
//
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                try {
//                    val response = sharedViewModel.httpsClient.newCall(request).execute()
//
//                    if (response.isSuccessful) {
//                        val inputStream: InputStream? = response.body?.byteStream()
//                        createFileFromInputStream(inputStream, appDir, mess.fileName)
//
//
//                    }
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                    // Обработка ошибок ввода/вывода
//                }
//            }
//        }
//    }
//
//    private fun createFileFromInputStream(inputStream: InputStream?, directory: File, fileName: String): File? {
//        val file = File(directory, fileName)
//
//        if (file.exists()) {
//            return null
//        }
//
//        inputStream?.use { input ->
//            FileOutputStream(file).use { output ->
//                input.copyTo(output)
//            }
//        }
//
//        return file
//    }

    fun updateMessage(mess: Message) {
        isEdit.value = true
        editedTextState.value = mess.content
        val jsonBody = JSONObject()

        viewModelScope.launch {
            while (!editedFinished.value) {
                delay(100)
            }


            if (editedTextState.value.isBlank()) {
                editedFinished.value = false
                isEdit.value = false
                editedTextState.value = ""
                return@launch
            }

            jsonBody.put("message_id", mess.id)
            jsonBody.put("new_content", editedTextState.value)
            jsonBody.put("type", "update_message")

            sharedViewModel.webSocket.send(jsonBody.toString())
            editedFinished.value = false
            isEdit.value = false
            editedTextState.value = ""
        }
    }

    fun deleteMessage(mess: Message) {
        val jsonBody = JSONObject()

        jsonBody.put("deleting_message_id", mess.id)
        jsonBody.put("type", "archive_message")

        sharedViewModel.webSocket.send(jsonBody.toString())
    }

    fun messageViewed(messageObject: JSONObject) {
        val messageId = messageObject.getInt("message_id")
        val isViewed = messageObject.getBoolean("is_readed")

        _messages.value = _messages.value.map { currentMessage ->
            if (currentMessage.id == messageId) {
                currentMessage.copy(hasViewed = isViewed)
            } else {
                currentMessage
            }
        }
    }

    fun setMessageViewed(messId: Int) {
        val jsonBody = JSONObject()
        jsonBody.put("message_id", messId)
        jsonBody.put("type", "is_readed_message")

        sharedViewModel.webSocket.send(jsonBody.toString())
    }
}
