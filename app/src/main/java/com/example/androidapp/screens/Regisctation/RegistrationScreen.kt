package com.example.androidapp.screens.Regisctation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidapp.viewModels.RegistrationViewModel.RegistrationViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen (viewModel: RegistrationViewModel) {
    val coroutineScope = rememberCoroutineScope()


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
                fontSize = 50.sp,
                fontWeight = FontWeight.Thin
            )
        )

        Spacer(modifier = Modifier.height(16.dp)) // Промежуток в 16dp

        OutlinedTextField(
            value = viewModel.userName,
            onValueChange = { viewModel.userName = it },
            placeholder = { Text("User name") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp)) // Промежуток в 16dp

        OutlinedTextField(
            value = viewModel.login,
            onValueChange = { viewModel.login = it },
            placeholder = { Text("Login") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp)) // Промежуток в 16dp


        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            singleLine = true,
            placeholder = { Text("Password") },
            visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (viewModel.passwordVisible)
                    Icons.Filled.Lock
                else Icons.Outlined.Info

                IconButton(onClick = {viewModel.passwordVisible = !viewModel.passwordVisible}){
                    Icon(imageVector  = image, null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(viewModel.userMessage)

        Spacer(modifier = Modifier.height(16.dp)) // Промежуток в 16dp

        ElevatedButton(
            onClick = {
                coroutineScope.launch {
                    viewModel.createUser()
                }
            },
            shape = RoundedCornerShape(10.dp), // Закругление углов
        ) {
            Text(
                "Create Account",
                style = TextStyle(
                    color = Color.Black
                )
            )
        }
    }
}

