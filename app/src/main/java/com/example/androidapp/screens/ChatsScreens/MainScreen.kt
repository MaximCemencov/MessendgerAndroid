package com.example.androidapp.screens.ChatsScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalDrawer
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.androidapp.features.MyColors
import com.example.androidapp.screens.Drawer.DrawerBody
import com.example.androidapp.screens.Items.ChatItem
import com.example.androidapp.viewModels.MessagesViewModel.MessagesViewModel
import com.example.androidapp.viewModels.Profile.ProfileViewModel
import com.example.androidapp.viewModels.SharedViewModel
import com.example.androidapp.viewModels.createChat.MainChatScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    isDarkTheme: Boolean,
    messagesViewModel: MessagesViewModel,
    coroutineScope: CoroutineScope,
    viewModel: MainChatScreenViewModel,
    profileViewModel: ProfileViewModel,
) {
    val loading by viewModel.isLoading.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val colors = MyColors
    val backgroundColor = if (isDarkTheme) colors.backgroundColorDarkTheme else colors.backgroundColorWhiteTheme
    val buttonColor = if (isDarkTheme) colors.buttonColorDarkTheme else colors.buttonColorWhiteTheme
    val textColor = if (isDarkTheme) colors.textColorDarkTheme else colors.textColorWhiteTheme

    LaunchedEffect(true) {
        viewModel.parseChats()
    }

    val pullRefreshState = rememberPullRefreshState(loading, onRefresh = { viewModel.parseChats() })


    if (loading) {
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
        val count = viewModel.allChats.size

        ModalDrawer(
            drawerContent = {
                DrawerBody(sharedViewModel, navController, isDarkTheme, profileViewModel)
            },
            drawerState = drawerState,
            drawerBackgroundColor = backgroundColor
        ) {
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
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Menu, contentDescription = "Menu", tint = textColor
                            )
                        }
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
                        itemsIndexed(
                            items=viewModel.allChats,
                            key={_, item->
                                item.hashCode()
                            }
                        ) {_, item->
                            val state = rememberDismissState(
                                confirmStateChange = {
                                    if (it == DismissValue.DismissedToStart) {
                                        viewModel.deleteChat(item)
                                    }
                                    true
                                }
                            )

                            Spacer(modifier = Modifier.padding(vertical = 5.dp))


                            SwipeToDismiss(
                                state = state,
                                background = {
                                    val color = when (state.dismissDirection) {
                                        DismissDirection.StartToEnd -> Color.Transparent
                                        DismissDirection.EndToStart -> Color.Red
                                        null -> Color.Transparent
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.align(Alignment.CenterEnd)
                                        )
                                    }
                                },
                                directions = setOf(DismissDirection.EndToStart),
                                dismissContent = {
                                    ChatItem(
                                        item,
                                        sharedViewModel,
                                        navController,
                                        isDarkTheme,
                                        messagesViewModel,
                                        profileViewModel
                                    )
                                }
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PullRefreshIndicator(
                            loading,
                            pullRefreshState,
                            Modifier.align(Alignment.CenterHorizontally)
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
}
