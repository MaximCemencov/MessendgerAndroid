package com.example.androidapp.screens.Regisctation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.androidapp.viewModels.RegistrationViewModel.LoginViewModel
import com.example.androidapp.viewModels.RegistrationViewModel.RegistrationViewModel
import com.example.androidapp.viewModels.SharedViewModel

@Composable
fun MainRegistrationScreen(
    sharedViewModel: SharedViewModel,
    navController: NavHostController
) {
    val viewModel = RegistrationViewModel() // Создайте ViewModel вне функции
    val loginViewModel = LoginViewModel()
    var selectedOption by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        // Отображаем содержимое в зависимости от выбранной опции
        when (selectedOption) {
            0 -> LoginScreen(sharedViewModel, navController, loginViewModel)
            1 -> RegistrationScreen(viewModel)
        }

        Spacer(modifier = Modifier.height(35.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ElevatedButton(
                onClick = { selectedOption = 0 },
                enabled = selectedOption != 0,
                shape = RoundedCornerShape(10.dp), // Закругление углов
                modifier = Modifier
                    .width(125.dp)
                    .height(40.dp)
            ) {
                Text(
                    "Log in",
                    style = TextStyle(
                        color = Color.Black
                    )
                )
            }
            
            Spacer(modifier = Modifier.width(5.dp))
            
            ElevatedButton(
                onClick = { selectedOption = 1 },
                enabled = selectedOption != 1,
                shape = RoundedCornerShape(10.dp), // Закругление углов
                modifier = Modifier
                    .width(125.dp)
                    .height(40.dp)
            ) {
                Text(
                    "Registration",
                    style = TextStyle(
                        color = Color.Black
                    )
                )
            }
        }
    }
}