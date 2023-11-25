package com.example.androidapp.screens.MessagesScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.androidapp.features.MyColors
import com.example.androidapp.features.limit
import com.example.androidapp.screens.Items.MessageItem.MessageItem
import com.example.androidapp.viewModels.MessagesViewModel.MessagesViewModel
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    viewModel: MessagesViewModel,
    isDarkTheme: Boolean,
    coroutineScope: CoroutineScope,
    lazyListState: LazyListState
) {
//    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//            if (uri != null) {
//                val fileType = viewModel.getFileType(uri.toString())
//
//                if (viewModel.selectedFiles.isNotEmpty()) {
//                    // Если список не пустой, перезапишите первый элемент
//                    viewModel.selectedFiles[0] = Pair(uri, fileType)
//                } else {
//                    // Если список пустой, добавьте новый элемент
//                    viewModel.selectedFiles.add(0, Pair(uri, fileType))
//                }
//            }
//        }


    val messages by viewModel.messages.collectAsState()

    LaunchedEffect(key1 = viewModel.currentPage) {
        viewModel.isLoading.value = true
        val offset = limit * viewModel.currentPage
        viewModel.getMessages(offset, lazyListState, coroutineScope)
        viewModel.isLoading.value = false
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.index }
            .collectLatest { index ->
                if (!viewModel.isLoading.value && index == 0) {
                    viewModel.currentPage++
                }
            }
    }

//    var expanded by remember { mutableStateOf(false) }
//    val animatedHeight by animateDpAsState(
//        targetValue = if (expanded) 80.dp else 0.dp,
//        animationSpec = spring(),
//        label = "aboba"
//    )

    val colors = MyColors
    val backgroundColor =
        if (isDarkTheme) colors.backgroundColorDarkTheme else colors.backgroundColorWhiteTheme
    val buttonColor = if (isDarkTheme) colors.buttonColorDarkTheme else colors.buttonColorWhiteTheme
    val buttonTextColor =
        if (isDarkTheme) colors.buttonTextColorDarkTheme else colors.buttonTextColorWhiteTheme
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
                    sharedViewModel.userName2,
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
                if (viewModel.isLoading.value) {
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

            itemsIndexed(
                messages.reversed(),
                key = { _, it -> it.id }
            ) { key, mess ->
                MessageItem(mess, isDarkTheme, viewModel, lazyListState, key)
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
                    Row {
                        IconButton(onClick = {
                            viewModel.editedTextState.value = ""
                            viewModel.editedFinished.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = textColor
                            )
                        }
                        IconButton(onClick = {
                            viewModel.editedFinished.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Done,
                                contentDescription = null,
                                tint = textColor
                            )
                        }
                    }
                } else {
                    Row {
//                        IconButton(onClick = {
//                            expanded = !expanded
//                        }) {
//                            Icon(
//                                painter = painterResource(R.drawable.add_file),
//                                contentDescription = null,
//                                tint = textColor
//                            )
//                        }

                        IconButton(onClick = {
                            viewModel.sendMessage()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = textColor
                            )
                        }
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

//        Row (
//            Modifier
//                .height(animatedHeight)
//                .background(buttonColor),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center,
//                modifier = Modifier
//                    .size(animatedHeight)
//                    .clickable {
//                        getContent.launch("*/*")
//                    }
//                    .padding(5.dp),
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.icon_file),
//                    contentDescription = null,
//                    tint = textColor
//                )
//                Text(
//                    "pick file",
//                    color = textColor
//                )
//            }
//            Divider(
//                Modifier
//                    .fillMaxHeight()
//                    .padding(vertical = 10.dp)
//                    .width(2.dp), // Ширина разделителя
//                color = buttonTextColor // Цвет разделителя
//            )
//
//            LazyRow(
//                Modifier.fillMaxSize(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                items(viewModel.selectedFiles) { uri ->
//                    val icon = when (uri.second.name) {
//                        "IMAGE" -> painterResource(id = R.drawable.icon_image)
//                        "VIDEO" -> painterResource(id = R.drawable.icon_video)
//                        "AUDIO" -> painterResource(id = R.drawable.icon_music)
//                        else -> painterResource(id = R.drawable.icon_file)
//                    }
//
//                    val fileName = viewModel.getFileName(uri.first)
//
//                    Column(
//                        modifier = Modifier
//                            .height(80.dp)
//                            .padding(6.dp)
//                            .clickable {}
//                            .border(
//                                BorderStroke(1.dp, textColor),
//                                RoundedCornerShape(10)),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        Icon(
//                            painter = icon,
//                            contentDescription = null,
//                            tint = textColor,
//                            modifier = Modifier.size(45.dp)
//                        )
//                        Text(
//                            text = fileName,
//                            color = textColor,
//                            maxLines = 1,
//                            modifier = Modifier.padding(horizontal = 5.dp)
//                        )
//
//                    }
//                }
//            }
//        }
    }
}