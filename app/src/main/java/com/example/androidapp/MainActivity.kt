package com.example.androidapp

import android.Manifest
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.features.SharedPreferencesManager
import com.example.androidapp.screens.ChatsScreens.CreateChat
import com.example.androidapp.screens.ChatsScreens.MainScreen
import com.example.androidapp.screens.MessagesScreen.MessagesScreen
import com.example.androidapp.screens.Regisctation.MainRegistrationScreen
import com.example.androidapp.viewModels.MessagesViewModel.MessagesViewModel
import com.example.androidapp.viewModels.RegistrationViewModel.LoginViewModel
import com.example.androidapp.viewModels.RegistrationViewModel.RegistrationViewModel
import com.example.androidapp.viewModels.SharedViewModel
import com.example.androidapp.viewModels.createChat.CreateChatViewModel
import com.example.androidapp.viewModels.createChat.MainChatScreenViewModel
import com.example.androidapp.websocket.MyWebSocketListener
import com.google.firebase.FirebaseApp
import createRequest
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class MainActivity : ComponentActivity() {
    private val okHttpClient = OkHttpClient()
    private lateinit var webSocketListener: WebSocketListener
    private lateinit var webSocket: WebSocket
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var lazyListState: LazyListState
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var messagesViewModel: MessagesViewModel


    @RequiresApi(VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(this)

        val sharedPreferencesManager = SharedPreferencesManager(this)
        sharedPreferencesManager.saveBoolean("isInApp", true)

        if (Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ), 0
            )
        }


        setContent {
            val navController = rememberNavController()
            sharedViewModel = SharedViewModel(this)
            messagesViewModel = viewModel<MessagesViewModel>()
            val registrationViewModel = viewModel<RegistrationViewModel>()
            val loginViewModel = viewModel<LoginViewModel>()
            val mainChatScreenViewModel = MainChatScreenViewModel(this.application, sharedViewModel)
            val createChatViewModel = viewModel<CreateChatViewModel>()
            coroutineScope = rememberCoroutineScope()
            lazyListState = rememberLazyListState()

            webSocketListener = MyWebSocketListener(
                messagesViewModel,
                sharedViewModel,
                coroutineScope,
                lazyListState
            )
            webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)

            val isDarkTheme = isSystemInDarkTheme()

            Surface(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = if (sharedViewModel.hasLogIn) "main_screen" else "registration"
                ) {
                    composable("registration") {
                        MainRegistrationScreen(
                            sharedViewModel,
                            navController,
                            isDarkTheme,
                            registrationViewModel,
                            loginViewModel
                        )
                    }
                    composable("main_screen") {
                        MainScreen(
                            sharedViewModel,
                            navController,
                            isDarkTheme,
                            mainChatScreenViewModel,
                            messagesViewModel
                        )
                    }
                    composable("create_chat") {
                        CreateChat(sharedViewModel, navController, isDarkTheme, createChatViewModel)
                    }
                    composable("messages_screen") {
                        MessagesScreen(
                            sharedViewModel,
                            navController,
                            messagesViewModel,
                            webSocket,
                            isDarkTheme,
                            coroutineScope,
                            lazyListState
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        webSocket.close(1000, "aboba")
        val sharedPreferencesManager = SharedPreferencesManager(this)
        sharedPreferencesManager.saveBoolean("isInApp", false)
        super.onStop()
    }
}

