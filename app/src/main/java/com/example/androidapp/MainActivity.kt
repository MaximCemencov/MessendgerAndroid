package com.example.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.screens.ChatsScreens.CreateChat
import com.example.androidapp.screens.Regisctation.MainRegistrationScreen
import com.example.androidapp.screens.ChatsScreens.MainScreen
import com.example.androidapp.viewModels.SharedViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val sharedViewModel = SharedViewModel()

            Surface (
                modifier = Modifier.fillMaxSize()
            ) {
                NavHost(navController = navController, startDestination = "create_chat") {
                    composable("registration") {
                        MainRegistrationScreen(sharedViewModel, navController)
                    }
                    composable("main_screen") {
                        MainScreen(sharedViewModel, navController)
                    }
                    composable("create_chat") {
                        CreateChat(sharedViewModel)
                    }
                }
            }
        }
    }
}

