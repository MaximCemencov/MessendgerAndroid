package com.example.androidapp.screens.Regisctation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.androidapp.features.MyColors
import com.example.androidapp.viewModels.RegistrationViewModel.LoginViewModel
import com.example.androidapp.viewModels.RegistrationViewModel.RegistrationViewModel
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.launch

@Composable
fun MainRegistrationScreen(
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    isDarkTheme: Boolean,
    registrationViewModel: RegistrationViewModel,
    loginViewModel: LoginViewModel
) {

    val coroutineScope = rememberCoroutineScope()

    var selectedOption by remember { mutableStateOf(0) }

    val colors = MyColors
    val backgroundColor = if (isDarkTheme) colors.backgroundColorDarkTheme else colors.backgroundColorWhiteTheme
    val buttonColor = if (isDarkTheme) colors.buttonColorDarkTheme else colors.buttonColorWhiteTheme
    val buttonTextColor = if (isDarkTheme) colors.buttonTextColorDarkTheme else colors.buttonTextColorWhiteTheme
    val buttonDisabled = if (isDarkTheme) colors.buttonDisableColorDarkTheme else colors.buttonDisableColorWhiteTheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        verticalArrangement = Arrangement.Center
    ) {
        // Отображаем содержимое в зависимости от выбранной опции
        when (selectedOption) {
            0 -> LoginScreen(sharedViewModel, navController, loginViewModel, isDarkTheme)
            1 -> RegistrationScreen(registrationViewModel, isDarkTheme) {
                val userNameValid = registrationViewModel.userName.isNotBlank()
                val passwordValid = registrationViewModel.password.isNotBlank()
                val loginValid = registrationViewModel.login.isNotBlank()

                if (userNameValid && passwordValid && loginValid)
                    coroutineScope.launch {
                        selectedOption = registrationViewModel.createUser(sharedViewModel)
                    }
                else if (!userNameValid) registrationViewModel.userMessage = "please fill you're username!"
                else if (!loginValid) registrationViewModel.userMessage = "please fill you're login!"
                else if (!passwordValid) registrationViewModel.userMessage = "please fill you're password!"

            }
        }

        Spacer(modifier = Modifier.height(35.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ElevatedButton(
                onClick = { selectedOption = 0 },
                enabled = selectedOption != 0,
                shape = RoundedCornerShape(30.dp), // Закругление углов
                modifier = Modifier
                    .width(125.dp)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    disabledContainerColor = buttonDisabled
                )
            ) {
                Text(
                    "Log in",
                    fontWeight = FontWeight(200),
                    color = buttonTextColor,
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            ElevatedButton(
                onClick = { selectedOption = 1 },
                enabled = selectedOption != 1,
                shape = RoundedCornerShape(30.dp), // Закругление углов
                modifier = Modifier
                    .width(125.dp)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    disabledContainerColor = buttonDisabled
                )
            ) {
                Text(
                    "Registration",
                    style = TextStyle(
                        fontWeight = FontWeight(200),
                        color = buttonTextColor,
                    )
                )
            }
        }
    }
}