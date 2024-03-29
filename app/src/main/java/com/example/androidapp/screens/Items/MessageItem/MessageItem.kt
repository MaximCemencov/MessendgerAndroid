package com.example.androidapp.screens.Items.MessageItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.androidapp.DataClass.MenuItem
import com.example.androidapp.DataClass.Message
import com.example.androidapp.R
import com.example.androidapp.features.MyColors
import com.example.androidapp.viewModels.MessagesViewModel.MessagesViewModel
import com.example.androidapp.viewModels.MessagesViewModel.functions.decodeFileToBitmap
import com.example.androidapp.viewModels.MessagesViewModel.functions.decodeImage
import com.example.androidapp.viewModels.MessagesViewModel.functions.getBitmapState
import com.example.androidapp.viewModels.MessagesViewModel.functions.isFileExistsInFolder
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun MessageItem(
    mess: Message,
    isDarkTheme: Boolean,
    viewModel: MessagesViewModel,
    lazyListState: LazyListState,
    key: Int,
    sharedViewModel: SharedViewModel,
    navController: NavHostController
) {
    var expanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    var showAlert by remember { mutableStateOf(false) }

    val colors = MyColors
    val textColor = if (isDarkTheme) colors.textColorDarkTheme else colors.textColorWhiteTheme
    val backgroundColor = if (isDarkTheme) colors.backgroundColorDarkTheme else colors.backgroundColorWhiteTheme
    val buttonColor = if (isDarkTheme) colors.buttonColorDarkTheme else colors.buttonColorWhiteTheme

    val items = buildList {
        add(MenuItem(1, "Copy"))
        if (mess.hasYour) {
            add(MenuItem(2, "Edit"))
        }
        if (mess.fileName != "null") {
            add(MenuItem(3, "Save To Download"))
        }
        add(MenuItem(4, "Delete"))
    }
    val isVisible by remember(lazyListState, key) {
        derivedStateOf {
            isItemVisible(lazyListState, key)
        }
    }


    SideEffect {
        if (isVisible && !mess.hasViewed && !mess.hasYour) {
            viewModel.setMessageViewed(mess.id)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalAlignment = if (mess.hasYour) Alignment.End else Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(22.dp))
                .background(
                    if (mess.hasYour)
                        Color(0xFF32A0FD)
                    else
                        Color(0xFF00BB00)
                )
                .padding(horizontal = 5.dp, vertical = 5.dp)
                .clickable { expanded = true }
                .width(IntrinsicSize.Max)
        ) {
            if (mess.fileName != "null" && mess.fileType != "null") {
                var fileDownloaded by remember { mutableStateOf(isFileExistsInFolder(mess.fileName, viewModel.appDir)) }

                when (mess.fileType) {
                    "image" -> {
                        LaunchedEffect(true) {
                            if (fileDownloaded && getBitmapState(mess.fileName, viewModel.bitmapMap).value == null) {
                                withContext(Dispatchers.IO) {
                                    val decodedBitmap = decodeImage(mess.fileName, viewModel.appDir)
                                    getBitmapState(mess.fileName, viewModel.bitmapMap).value = decodedBitmap
                                }
                            }
                        }
                        val bitmapState = getBitmapState(mess.fileName, viewModel.bitmapMap)

                        if (fileDownloaded) {
                            bitmapState.value?.let { decodedBitmap ->
                                Column(Modifier.padding(bottom = 5.dp)) {
                                    Image(
                                        bitmap = decodedBitmap.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.clip(RoundedCornerShape(22.dp))
                                            .clickable {
                                                sharedViewModel.selectedBitmap = Pair(decodedBitmap, mess.fileName)
                                                navController.navigate("ImageViewed")
                                            }
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .size(175.dp)
                                    .background(buttonColor)
                                    .clip(RoundedCornerShape(22.dp)),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                TextButton(onClick = {
                                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                                        val file = viewModel.downloadFileAsync(mess).await()
                                        val decodedBitmap = decodeFileToBitmap(file)
                                        getBitmapState(mess.fileName, viewModel.bitmapMap).value =
                                            decodedBitmap
                                        fileDownloaded = true
                                    }
                                }) {
                                    Column (
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.download),
                                            contentDescription = null,
                                            modifier = Modifier.size(42.dp),
                                            tint = textColor
                                        )
                                        Text(
                                            text = mess.fileName,
                                            color = textColor,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "other" -> {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(buttonColor)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.download),
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { viewModel.downloadFileAsync(mess, true) }
                            )
                            Column {
                                Text(text = mess.fileName, color = textColor)
                            }
                        }
                    }
                }
            }



            Column(Modifier.padding(horizontal = 5.dp)) {
                if (mess.content != "")
                    Text(
                        mess.content,
                        fontSize = 17.sp,
                        color = Color(0xFFFFFFFF),
                        fontWeight = FontWeight(400)
                    )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        mess.timeStamp,
                        fontSize = 8.sp,
                        textAlign = TextAlign.End,
                        color = Color(0xFFFFFFFF),
                        fontWeight = FontWeight(400),
                        modifier = Modifier.padding(end = 10.dp)
                    )

                    Row {
                        if (mess.hasEdited) {
                            Text(
                                "edited",
                                fontSize = 8.sp,
                                textAlign = TextAlign.End,
                                color = Color(0xFFFFFFFF),
                                fontWeight = FontWeight(200),
                                fontStyle = FontStyle(1),
                                modifier = Modifier.padding(end = 10.dp)
                            )
                        }

                        if (mess.hasYour) {
                            Icon(
                                painter = painterResource(
                                    if (mess.hasViewed) R.drawable.viewed_message
                                    else R.drawable.send_message
                                ),
                                contentDescription = null,
                                Modifier.size(12.dp),
                                tint = Color(0xFFFFFFFF)
                            )
                        }
                    }
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(backgroundColor),
            ) {
                items.forEach { item ->
                    Text(
                        text = item.name,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                when (item.id) {
                                    1 -> clipboardManager.setText(AnnotatedString((mess.content)))
                                    2 -> viewModel.updateMessage(mess)
                                    3 -> viewModel.downloadFileAsync(mess, true)
                                    4 -> {
                                        showAlert = true
                                    }
                                }
                                expanded = false
                            },
                        color = if (item.id == 4) {
                            Color.Red
                        } else {
                            textColor
                        }
                    )
                }
            }
        }
    }


    if (showAlert) {
        AlertDialog(
            containerColor = buttonColor,
            title = {
                Text(
                    text = "Confirm delete",
                    color = textColor
                )
            },
            text = {
                Text(
                    text = "you really want to delete: ${mess.content}",
                    color = textColor
                )
            },
            onDismissRequest = { showAlert = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMessage(mess)
                        showAlert = false
                    }
                ) {
                    Text(
                        "Delete",
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAlert = false
                }) {
                    Text(
                        "Cancel",
                        color = textColor
                    )
                }
            }
        )
    }
}
