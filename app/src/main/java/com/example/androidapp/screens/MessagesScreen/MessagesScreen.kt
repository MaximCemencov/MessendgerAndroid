package com.example.androidapp.screens.MessagesScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.androidapp.features.MyColors
import com.example.androidapp.features.limit
import com.example.androidapp.screens.Items.MessageItem
import com.example.androidapp.viewModels.MessagesViewModel.MessagesViewModel
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import okhttp3.WebSocket


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    viewModel: MessagesViewModel,
    webSocket: WebSocket,
    isDarkTheme: Boolean,
    coroutineScope: CoroutineScope,
    lazyListState: LazyListState
) {
    val page = remember { mutableStateOf(0) }
    val loading = remember { mutableStateOf(false) }


    LaunchedEffect(key1 = page.value) {
        loading.value = true
        val offset = limit * page.value
        viewModel.getMessages(offset, lazyListState, coroutineScope)
        loading.value = false
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.index }
            .collectLatest { index ->
                if (!loading.value && index == 0) {
                    page.value++
                }
            }
    }

    val colors = MyColors
    val backgroundColor = if (isDarkTheme) colors.backgroundColorDarkTheme else colors.backgroundColorWhiteTheme
    val buttonColor = if (isDarkTheme) colors.buttonColorDarkTheme else colors.buttonColorWhiteTheme
    val buttonTextColor = if (isDarkTheme) colors.buttonTextColorDarkTheme else colors.buttonTextColorWhiteTheme
    val textColor = if (isDarkTheme) colors.textColorDarkTheme else colors.textColorWhiteTheme


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = textColor)
                }
            },
            title = {
                Text(
                    sharedViewModel.user_name2,
                    color = textColor,
                    fontWeight = FontWeight(400),
                )
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = backgroundColor
            )
        )


        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Bottom,
            state = lazyListState,
        ) {
            item {
                if (loading.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }


            items(viewModel.messages.flatten().reversed()) { mess ->
                MessageItem(mess, isDarkTheme, viewModel, webSocket)
            }
        }



        OutlinedTextField(
            value = if (viewModel.isEdit.value) viewModel.editedTextState.value else viewModel.textState.value,
            onValueChange = { newValue ->
                if (viewModel.isEdit.value) {
                    if (newValue.length <= 800) {
                        viewModel.editedTextState.value = newValue
                    } else {
                        viewModel.editedTextState.value = newValue.take(800)
                    }
                } else {
                    if (newValue.length <= 800) {
                        viewModel.textState.value = newValue
                    } else {
                        viewModel.textState.value = newValue.take(800)
                    }
                }

            },
            placeholder = {
                Text(
                    "Enter message",
                    color = buttonTextColor,
                    fontWeight = FontWeight(100)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
            trailingIcon = {
                if (viewModel.isEdit.value) {
                    IconButton(onClick = {
                        viewModel.editedFinished.value = true
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Done,
                            contentDescription = null,
                            tint = textColor
                        )
                    }
                } else {
                    IconButton(onClick = {
                            viewModel.sendMessage(webSocket)
                        }) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = textColor
                        )
                    }
                }
            },
            shape = RoundedCornerShape(15.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = buttonColor,
                textColor = textColor
            ),
            maxLines = 3
        )
    }
}