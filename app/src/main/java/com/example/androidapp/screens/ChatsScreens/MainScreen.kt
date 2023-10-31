package com.example.androidapp.screens.ChatsScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.androidapp.features.MyColors
import com.example.androidapp.screens.Items.ChatItem
import com.example.androidapp.viewModels.MessagesViewModel.MessagesViewModel
import com.example.androidapp.viewModels.SharedViewModel
import com.example.androidapp.viewModels.createChat.MainChatScreenViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    isDarkTheme: Boolean,
    viewModel: MainChatScreenViewModel,
    messagesViewModel: MessagesViewModel
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    viewModel.parseChats(sharedViewModel)

    val pullRefreshState = rememberPullRefreshState(isSearching, onRefresh = {
        viewModel.parseChats(sharedViewModel)
    })

    val count = allUsers.size

    val colors = MyColors


    val backgroundColor = if (isDarkTheme) colors.backgroundColorDarkTheme else colors.backgroundColorWhiteTheme
    val buttonColor = if (isDarkTheme) colors.buttonColorDarkTheme else colors.buttonColorWhiteTheme
    val textColor = if (isDarkTheme) colors.textColorDarkTheme else colors.textColorWhiteTheme

    if (isSearching) {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .background(backgroundColor)
                .fillMaxSize()
        ) {

            TopAppBar(
                title = {
                    Text(
                        text = "All chats ($count)",
                        color = textColor
                        )
                },
                backgroundColor = backgroundColor
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allUsers) { chat ->
                        ChatItem(
                            chat,
                            sharedViewModel,
                            navController,
                            isDarkTheme,
                            messagesViewModel
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PullRefreshIndicator(
                        isSearching,
                        pullRefreshState,
                        contentColor = Color.Red,
                        backgroundColor = buttonColor
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("create_chat")
                        },
                        modifier = Modifier.padding(10.dp),
                        containerColor = buttonColor,
                        contentColor = textColor
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}
