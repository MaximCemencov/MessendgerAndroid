package com.example.androidapp.screens.ChatsScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidapp.viewModels.SharedViewModel
import com.example.androidapp.viewModels.createChat.CreateChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChat(sharedViewModel: SharedViewModel) {
    val viewModel = viewModel<CreateChatViewModel>()

    val searchText by viewModel.searchText.collectAsState()
    val users by viewModel.users.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val errorMessage by viewModel.errorText.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = viewModel::onSearchTextChange,
            placeholder = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (errorMessage.isNotEmpty())
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    errorMessage,
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Thin
                    )
                )
            }
        if (isSearching) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .padding(vertical = 20.dp)
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(users) { user ->
                    Button(
                        onClick = {

                        },
                        modifier = Modifier
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(size = 15.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    )
                    {
                        Text(
                            text = user.userName,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.CenterVertically)

                        )
                    }
                }
            }
        }
    }

}