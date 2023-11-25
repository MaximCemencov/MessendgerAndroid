package com.example.androidapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.features.SharedPreferencesManager
import com.example.androidapp.screens.ChatsScreens.CreateChat
import com.example.androidapp.screens.ChatsScreens.MainScreen
import com.example.androidapp.screens.Drawer.Profile
import com.example.androidapp.screens.MessagesScreen.MessagesScreen
import com.example.androidapp.screens.Regisctation.MainRegistrationScreen
import com.example.androidapp.screens.Settings.SettingsScreen
import com.example.androidapp.viewModels.MessagesViewModel.MessagesViewModel
import com.example.androidapp.viewModels.Profile.ProfileViewModel
import com.example.androidapp.viewModels.RegistrationViewModel.LoginViewModel
import com.example.androidapp.viewModels.RegistrationViewModel.RegistrationViewModel
import com.example.androidapp.viewModels.SharedViewModel
import com.example.androidapp.viewModels.createChat.CreateChatViewModel
import com.example.androidapp.viewModels.createChat.MainChatScreenViewModel
import com.example.androidapp.websocket.MyWebSocketListener
import com.example.androidapp.websocket.WorkWithWebsocket
import com.google.firebase.FirebaseApp
import createRequest
import kotlinx.coroutines.delay
import org.json.JSONObject


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

//        val appDir = File(filesDir, "messenger_folder")
//        if (!appDir.exists()) {
//            appDir.mkdir()
//        }

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

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this as Activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                123
            )
        }


        setContent {
            val navController = rememberNavController()
            val coroutineScope = rememberCoroutineScope()
            val lazyListState = rememberLazyListState()

            val sharedViewModel = SharedViewModel(this)
            val registrationViewModel = RegistrationViewModel(sharedViewModel)
            val createChatViewModel = CreateChatViewModel(sharedViewModel)
            val messagesViewModel =
                MessagesViewModel(sharedViewModel, lazyListState, coroutineScope)
            val mainChatScreenViewModel = MainChatScreenViewModel(sharedViewModel)
            val profileViewModel = ProfileViewModel(this, sharedViewModel)


            val workWithWebsocket = WorkWithWebsocket(messagesViewModel, mainChatScreenViewModel)
            val webSocketListener = MyWebSocketListener(workWithWebsocket, sharedViewModel)

            val loginViewModel = LoginViewModel(sharedViewModel, webSocketListener)

            if (sharedViewModel.hasLogIn) {
                sharedViewModel.webSocket = sharedViewModel.client.newWebSocket(
                    createRequest(sharedViewModel),
                    webSocketListener
                )

                val jsonBody = JSONObject()
                jsonBody.put("type", "ping pong")

                LaunchedEffect(true) {
                    while (true) {
                        Log.d("Test123412341", "ping pong")
                        sharedViewModel.webSocket.send(jsonBody.toString())
                        delay(10000L)
                    }
                }
            }

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
                            mainChatScreenViewModel,
                            profileViewModel
                        )
                    }
                    composable("create_chat") {
                        CreateChat(navController, isDarkTheme, createChatViewModel, profileViewModel)
                    }
                    composable("messages_screen") {
                        MessagesScreen(
                            sharedViewModel,
                            navController,
                            messagesViewModel,
                            isDarkTheme,
                            coroutineScope,
                            lazyListState
                        )
                    }
                    composable("settings_screen") {
                        SettingsScreen()
                    }
                    composable("profile_screen") {
                        Profile(
                            sharedViewModel,
                            isDarkTheme,
                            navController,
                            profileViewModel
                        )
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
