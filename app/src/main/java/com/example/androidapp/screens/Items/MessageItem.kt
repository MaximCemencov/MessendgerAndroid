package com.example.androidapp.screens.Items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidapp.DataClass.MenuItem
import com.example.androidapp.DataClass.Message
import com.example.androidapp.features.MyColors
import com.example.androidapp.viewModels.MessagesViewModel.MessagesViewModel
import okhttp3.WebSocket

@Composable
fun MessageItem(
    mess: Message,
    isDarkTheme: Boolean,
    viewModel: MessagesViewModel,
    webSocket: WebSocket
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
        add(MenuItem(3, "Delete"))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement =
        if (mess.hasYour)
            Arrangement.End
        else
            Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(
                    if (mess.hasYour)
                        Color(0xFF32A0FD)
                    else
                        Color(0xFF00BB00)
                )
                .padding(start = 17.dp, top = 5.dp, end = 17.dp, bottom = 5.dp)
                .clickable(onClick = {
                    expanded = true
                }),
        ) {
            Text(
                mess.content,
                fontSize = 17.sp,
                color = Color(0xFFFFFFFF),
                fontWeight = FontWeight(400)
            )
            Text(
                mess.timeStamp,
                fontSize = 8.sp,
                textAlign = TextAlign.End,
                color = Color(0xFFFFFFFF),
                fontWeight = FontWeight(400),
                modifier = Modifier.padding(end = 10.dp)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(backgroundColor)
            ) {
                items.forEach { item ->
                    Text(
                        text = item.name,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable(onClick = {
                                when (item.id) {
                                    1 -> clipboardManager.setText(AnnotatedString((mess.content)))
                                    2 -> viewModel.updateMessage(mess, webSocket)
                                    3 -> showAlert = true
                                }
                                expanded = false
                            }),
                        color = if (item.id == 3) {
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
                ) },
            text = {
                Text(
                    text = "you really want to delete: ${mess.content}",
                    color = textColor
                ) },
            onDismissRequest = { showAlert = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMessage(mess, webSocket)
                        showAlert = false
                    }
                ) {
                    Text("Delete",
                        color =  Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAlert = false
                }) {
                    Text("Cancel",
                        color = textColor
                    ) }
            }
        )
    }
}
