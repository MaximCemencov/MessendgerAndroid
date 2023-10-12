package com.example.androidapp.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController

class SharedViewModel: ViewModel() {
    var hasLogIn by mutableStateOf(false)
    var login by mutableStateOf("")
    var userName by mutableStateOf("")
    var userId by mutableStateOf(0)
}