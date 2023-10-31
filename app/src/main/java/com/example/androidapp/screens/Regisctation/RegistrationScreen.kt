package com.example.androidapp.screens.Regisctation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidapp.R
import com.example.androidapp.features.MyColors
import com.example.androidapp.viewModels.RegistrationViewModel.RegistrationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    isDarkTheme: Boolean,
    onButtonClicked: () -> Unit
) {

    val colors = MyColors

    val textColor = if (isDarkTheme) colors.textColorDarkTheme else colors.textColorWhiteTheme
    val buttonColor = if (isDarkTheme) colors.buttonColorDarkTheme else colors.buttonColorWhiteTheme
    val buttonTextColor = if (isDarkTheme) colors.buttonTextColorDarkTheme else colors.buttonTextColorWhiteTheme


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registration",
            style = TextStyle(
                fontSize = 64.sp,
                fontWeight = FontWeight(200),
                color = textColor,
            )
        )

        Spacer(modifier = Modifier.height(16.dp)) // Промежуток в 16dp

        OutlinedTextField(
            value = viewModel.userName,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = buttonColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(
                fontWeight = FontWeight(100),
                color = textColor
            ),
            onValueChange = {
                if (viewModel.userName.length <= 50) {
                    viewModel.userName = it
                } else {
                    viewModel.userName = it.take(50)
                }
            },
            placeholder = {
                Text(
                    "User name",
                    style = TextStyle(
                        fontWeight = FontWeight(100),
                        color = textColor
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp)) // Промежуток в 16dp

        OutlinedTextField(
            value = viewModel.login,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = buttonColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(
                fontWeight = FontWeight(100),
                color = textColor
            ),
            onValueChange = {
                if (viewModel.login.length <= 50) {
                    viewModel.login = it
                } else {
                    viewModel.login = it.take(50)
                }
            },
            placeholder = {
                Text(
                    "Login",
                    style = TextStyle(
                        fontWeight = FontWeight(100),
                        color = textColor
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp)) // Промежуток в 16dp

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = {
                if (viewModel.password.length <= 100) {
                    viewModel.password = it
                } else {
                    viewModel.password = it.take(100)
                }
            },
            singleLine = true,
            placeholder = {
                Text(
                    "Password",
                    style = TextStyle(
                        fontWeight = FontWeight(100),
                        color = textColor
                    )
                )
            },
            visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (viewModel.passwordVisible)
                    painterResource(R.drawable.lock_open)
                else painterResource(R.drawable.lock)

                IconButton(
                    onClick = {
                        viewModel.passwordVisible = !viewModel.passwordVisible
                    }) {
                    Icon(painter = image, null, tint = Color.Black)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = buttonColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(
                fontWeight = FontWeight(100),
                color = textColor
            )
        )

        Text(
            viewModel.userMessage,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp)) // Промежуток в 16dp

        ElevatedButton(
            onClick = onButtonClicked,
            shape = RoundedCornerShape(30.dp), // Закругление углов
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            )
        ) {
            Text(
                "Create Account",
                fontWeight = FontWeight(200),
                color = buttonTextColor,
            )
        }
    }
}

