package com.example.androidapp.screens.Items

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.androidapp.DataClass.Chat
import com.example.androidapp.features.MyColors
import com.example.androidapp.features.generateRandomColor
import com.example.androidapp.viewModels.MessagesViewModel.MessagesViewModel
import com.example.androidapp.viewModels.Profile.ProfileViewModel
import com.example.androidapp.viewModels.SharedViewModel

@Composable
fun ChatItem(
    chat: Chat,
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    isDarkTheme: Boolean,
    viewModel: MessagesViewModel,
    profileViewModel: ProfileViewModel
) {
    val colors = MyColors
    val buttonColor = if (isDarkTheme) colors.buttonColorDarkTheme else colors.buttonColorWhiteTheme
    val textColor = if (isDarkTheme) colors.textColorDarkTheme else colors.textColorWhiteTheme

    var loadedAvatar by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(chat.userId2) {
        val byte64 = profileViewModel.getAvatarFromServer(chat.userId2.toInt())
        loadedAvatar = byte64
    }

    Column(
        Modifier
            .clickable {
                viewModel.clearMessages()
                sharedViewModel.userId2 = chat.userId2.toInt()
                sharedViewModel.currentChatId = chat.chatId.toInt()
                sharedViewModel.userName2 = chat.userName2
                sharedViewModel.saveToSharedPreferences()
                navController.navigate("messages_screen")
            }
            .background(buttonColor, shape = RoundedCornerShape(100))
            .padding(8.dp)
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                if (chat.hasAvatar) {
                    if (loadedAvatar != null) {
                        Image(
                            bitmap = profileViewModel.base64ToImageBitmap(loadedAvatar!!)!!.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        CircularProgressIndicator(
                            Modifier.size(40.dp)
                        )
                    }
                } else {
                    Icon(
                        Icons.Rounded.Face,
                        contentDescription = null,
                        tint = generateRandomColor(),
                        modifier = Modifier.size(50.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    Text(
                        text = chat.userName2,
                        color = textColor,
                        fontSize = 22.sp,
                        maxLines = 1,
                        fontWeight = FontWeight(200)
                    )
                    Text(
                        chat.lastMessage,
                        color = textColor,
                        fontSize = 15.sp,
                        maxLines = 1,
                        fontWeight = FontWeight(200)
                    )
                }
            }
            if (chat.noViewedMessages != 0) {
                Box(
                    modifier = Modifier
                        .border(
                            color = textColor,
                            width = 0.5f.dp,
                            shape = RoundedCornerShape(100)
                        )
                        .size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (chat.noViewedMessages < 99) chat.noViewedMessages.toString() else "99+",
                        color = textColor,
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }
}