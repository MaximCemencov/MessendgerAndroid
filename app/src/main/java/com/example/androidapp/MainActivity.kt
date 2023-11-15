package com.example.androidapp

import android.Manifest
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.features.SharedPreferencesManager
import com.example.androidapp.screens.ChatsScreens.CreateChat
import com.example.androidapp.screens.ChatsScreens.MainScreen
import com.example.androidapp.screens.MessagesScreen.MessagesScreen
import com.example.androidapp.screens.Regisctation.MainRegistrationScreen
import com.example.androidapp.screens.Settings.SettingsScreen
import com.example.androidapp.viewModels.MessagesViewModel.MessagesViewModel
import com.example.androidapp.viewModels.RegistrationViewModel.LoginViewModel
import com.example.androidapp.viewModels.RegistrationViewModel.RegistrationViewModel
import com.example.androidapp.viewModels.SharedViewModel
import com.example.androidapp.viewModels.createChat.CreateChatViewModel
import com.example.androidapp.viewModels.createChat.MainChatScreenViewModel
import com.example.androidapp.websocket.MyWebSocketListener
import com.google.firebase.FirebaseApp
import createRequest
import okhttp3.WebSocket

class MainActivity : ComponentActivity() {
    private lateinit var webSocket: WebSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            val sharedViewModel = SharedViewModel(this)
            val messagesViewModel = MessagesViewModel(sharedViewModel)
            val registrationViewModel = RegistrationViewModel(sharedViewModel)
            val loginViewModel = LoginViewModel(sharedViewModel)
            val createChatViewModel = CreateChatViewModel(sharedViewModel)
            val mainChatScreenViewModel = MainChatScreenViewModel(sharedViewModel)

            val navController = rememberNavController()
            val coroutineScope = rememberCoroutineScope()
            val lazyListState = rememberLazyListState()

            val webSocketListener = MyWebSocketListener(
                messagesViewModel,
                sharedViewModel,
                coroutineScope,
                lazyListState
            )



//            LaunchedEffect(true) {
//                while (true) {
//
//                    delay(60000L)
//                }
//            }

            webSocket = sharedViewModel.client.newWebSocket(
                createRequest(sharedViewModel),
                webSocketListener
            )

            val isDarkTheme = sharedViewModel.getTheme()

            Surface(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = if (sharedViewModel.hasLogIn) "main_screen" else "registration"
                ) {
                    composable("registration") {
                        MainRegistrationScreen(
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
                            messagesViewModel,
                            coroutineScope,
                            mainChatScreenViewModel
                        )
                    }
                    composable("create_chat") {
                        CreateChat(navController, isDarkTheme, createChatViewModel)
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
                    composable("settings_screen") {
                        SettingsScreen()
                    }
                }
            }
        }
    }


    override fun onStop() {
        val sharedPreferencesManager = SharedPreferencesManager(this)
        sharedPreferencesManager.saveBoolean("isInApp", false)
        super.onStop()
    }
}

