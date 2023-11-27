package com.example.androidapp.viewModels.MessagesViewModel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.DataClass.Message
import com.example.androidapp.FileTypes.FileType
import com.example.androidapp.features.getCurrentDateTimeInUTC
import com.example.androidapp.features.limit
import com.example.androidapp.features.mainUrl
import com.example.androidapp.features.parseCustomTime
import com.example.androidapp.viewModels.MessagesViewModel.functions.copyFile
import com.example.androidapp.viewModels.MessagesViewModel.functions.createFileFromInputStream
import com.example.androidapp.viewModels.MessagesViewModel.functions.getFileFromUri
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream

class MessagesViewModel(
    private val sharedViewModel: SharedViewModel,
    private val lazyListState: LazyListState,
    private val coroutineScope: CoroutineScope,
    private val context: Context
) : ViewModel() {
    val messagesViewModelContext = context
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    var selectedFiles = mutableStateListOf<Pair<Uri, FileType>>()

    var textState by mutableStateOf("")

    var currentPage by mutableIntStateOf(0)
    var isLoading by mutableStateOf(false)

    val isEdit = mutableStateOf(false)
    var editedTextState by mutableStateOf("")
    var editedFinished by mutableStateOf(false)

    val bitmapMap = mutableMapOf<String, MutableState<Bitmap?>>()
    val appDir = File(context.filesDir, "messenger_folder")


    fun clearMessages() {
        if (_messages.value.isNotEmpty()) {
            _messages.value = emptyList()
        }
    }

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
                    val fileName = jsonObject.getString("originalfile")
                    val fileType = jsonObject.getString("file_type")

                    val message = Message(
                        messageId,
                        content,
                        senderId,
                        parseCustomTime(timeStamp)!!,
                        senderId == sharedViewModel.userId,
                        hasViewed,
                        hasEdited,
                        fileName,
                        fileType
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
        val fileName = messageObject.getString("file_name")
        val fileType = messageObject.getString("fileType")

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
            fileName,
            fileType
        )

        _messages.value = listOf(newMessage) + _messages.value
        coroutineScope.launch {
            val lastIndex = _messages.value.size - 1
            lazyListState.scrollToItem(lastIndex)
        }
    }

    fun updatedMessage(messageObject: JSONObject) {
        val messageId = messageObject.getInt("message_id")
        val senderId = messageObject.getInt("sender_id")
        val chatId = messageObject.getInt("chat_id")
        val timeStamp = messageObject.getString("time_of_day")
        val newContent = messageObject.getString("new_content")
        val hasEdited = messageObject.getBoolean("is_edited")
        val updatedMessageIndex = _messages.value.indexOfFirst { it.id == messageId }
        val fileName = messageObject.getString("file_name")
        val fileType = messageObject.getString("fileType")

        if (chatId != sharedViewModel.currentChatId || updatedMessageIndex == -1) {
            return
        }

        val mess = Message(
            messageId,
            newContent,
            senderId,
            parseCustomTime(timeStamp)!!,
            sharedViewModel.userId == senderId,
            false,
            hasEdited,
            fileName,
            fileType
        )

        _messages.value = _messages.value.toMutableList().apply {
            set(
                updatedMessageIndex,
                mess
            )
        }

    }

    fun deleteMessage(messageObject: JSONObject) {
        val messageId = messageObject.getInt("message_id")

        _messages.value = _messages.value.filterNot { it.id == messageId }
    }

    fun updateMessage(mess: Message) {
        isEdit.value = true
        editedTextState = mess.content
        val jsonBody = JSONObject()

        viewModelScope.launch {
            while (!editedFinished) {
                delay(100)
            }


            if (editedTextState.isBlank()) {
                editedFinished = false
                isEdit.value = false
                editedTextState = ""
                return@launch
            }

            jsonBody.put("message_id", mess.id)
            jsonBody.put("new_content", editedTextState)
            jsonBody.put("type", "update_message")

            sharedViewModel.webSocket.send(jsonBody.toString())
            editedFinished = false
            isEdit.value = false
            editedTextState = ""
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

    fun sendMessage() {
        val jsonBody = JSONObject()

        if (textState.isNotBlank() && selectedFiles.isEmpty())   {
            jsonBody.put("file_name", "")
            jsonBody.put("service_file", "")
        }


        if (selectedFiles.isNotEmpty()) {
            val fileData = runBlocking { sendFile() }
            Log.d("aboba", fileData.toString())
            if (fileData.first != "" && fileData.second != "") {
                jsonBody.put("file_name", fileData.first)
                jsonBody.put("service_file", fileData.second)
                selectedFiles.removeAt(0)
            }
        }

        jsonBody.put("chat_id", sharedViewModel.currentChatId)
        jsonBody.put("sender_id", sharedViewModel.userId)
        jsonBody.put("recipient_id", sharedViewModel.userId2)
        jsonBody.put("content", textState)
        textState = ""
        jsonBody.put("time_of_day", getCurrentDateTimeInUTC())
        jsonBody.put("type", "new_message")


        Log.d("aboba", jsonBody.toString())
        sharedViewModel.webSocket.send(jsonBody.toString())
    }

    private suspend fun sendFile(): Pair<String, String> = withContext(Dispatchers.IO) {
        var fileName = ""
        var serviceFile = ""

        if (selectedFiles.isNotEmpty()) {
            Log.d("aboba", selectedFiles[0].first.path.toString())
            val file = getFileFromUri(selectedFiles[0].first, context)

            Log.d("aboba", file.toString())

            if (file != null) {

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", sharedViewModel.userId.toString())
                    .addFormDataPart("login", sharedViewModel.login)
                    .addFormDataPart("password", sharedViewModel.password)
                    .addFormDataPart(
                        "file",
                        file.name,
                        file.asRequestBody("multipart/form-data".toMediaType())
                    )
                    .build()

                val request = Request.Builder()
                    .url("$mainUrl/file_upload")
                    .post(requestBody)
                    .build()

                val response = sharedViewModel.httpsClient.newCall(request).execute()

                if (response.isSuccessful) {
                    copyFile(file, file)
                    Log.d("test_file_upload", response.toString())
                    val data = response.body?.string()
                    val jsonArray = JSONObject(data!!)
                    fileName = jsonArray.getString("file_name")
                    serviceFile = jsonArray.getString("service_file")
                }
            } else {
                Log.e("FileNotFound", "The selected file does not exist.")
            }
        } else {
            Log.e("NoFileSelected", "No file selected.")
        }

        return@withContext Pair(fileName, serviceFile)
    }

    fun downloadFileAsync(mess: Message, saveInDownload: Boolean = false): Deferred<File?> =
        viewModelScope.async(Dispatchers.IO) {
            val jsonBody = JSONObject()
            jsonBody.put("message_id", mess.id)
            jsonBody.put("user_id", sharedViewModel.userId)
            jsonBody.put("login", sharedViewModel.login)
            jsonBody.put("password", sharedViewModel.password)

            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$mainUrl/file_download")
                .post(requestBody)
                .build()


            var file: File? = null

            try {
                val response = sharedViewModel.httpsClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val inputStream: InputStream? = response.body?.byteStream()
                    val targetDirectory =
                        if (saveInDownload)
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        else
                            appDir
                    file = createFileFromInputStream(inputStream, targetDirectory, mess.fileName)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            file
        }
}
