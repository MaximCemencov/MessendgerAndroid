package com.example.androidapp.screens.Items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.androidapp.DataClass.Chat
import com.example.androidapp.DataClass.Message
import com.example.androidapp.features.MyColors
import com.example.androidapp.viewModels.MessagesViewModel.MessagesViewModel
import com.example.androidapp.viewModels.SharedViewModel

@Composable
fun ChatItem (
    chat: Chat,
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    isDarkTheme: Boolean,
    viewModel: MessagesViewModel
) {
    val colors = MyColors

    val buttonColor = if (isDarkTheme) colors.buttonColorDarkTheme else colors.buttonColorWhiteTheme
    val textColor = if (isDarkTheme) colors.textColorDarkTheme else colors.textColorWhiteTheme


    Button(
        onClick = {
            sharedViewModel.user_id2 = chat.user_id2.toInt()
            sharedViewModel.current_chat_id = chat.chat_id.toInt()
            sharedViewModel.user_name2 = chat.user_name2
            sharedViewModel.saveToSharedPreferences()
            viewModel.clearMessages()
            navController.navigate("messages_screen")
        },
        modifier = Modifier
            .padding(vertical = 7.dp),
        shape = RoundedCornerShape(size = 30.dp),
        colors = ButtonDefaults.buttonColors(buttonColor)
    )
    {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = chat.user_name2,
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
}