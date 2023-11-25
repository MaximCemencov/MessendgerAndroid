package com.example.androidapp.screens.ChatsScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.androidapp.features.MyColors
import com.example.androidapp.features.generateRandomColor
import com.example.androidapp.features.mainUrl
import com.example.androidapp.viewModels.Profile.ProfileViewModel
import com.example.androidapp.viewModels.createChat.CreateChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChat(
    navController: NavHostController,
    isDarkTheme: Boolean,
    viewModel: CreateChatViewModel,
    profileViewModel: ProfileViewModel
) {
    val coroutineScope = rememberCoroutineScope()


    val searchText by viewModel.searchText.collectAsState()
    val users by viewModel.users.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val errorMessage by viewModel.errorText.collectAsState()

    val count = users.size

    val colors = MyColors
    val backgroundColor =
        if (isDarkTheme) colors.backgroundColorDarkTheme else colors.backgroundColorWhiteTheme
    val buttonColor = if (isDarkTheme) colors.buttonColorDarkTheme else colors.buttonColorWhiteTheme
    val textColor = if (isDarkTheme) colors.textColorDarkTheme else colors.textColorWhiteTheme


    Column(
        modifier = Modifier.background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = textColor)
            }

            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                placeholder = {
                    Text(
                        "Enter user name",
                        fontWeight = FontWeight(200),
                        color = textColor,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = buttonColor,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black
                ),
                shape = RoundedCornerShape(30.dp),
                textStyle = TextStyle(
                    fontWeight = FontWeight(200),
                    color = textColor
                )
            )
        }

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
                    fontSize = 40.sp,
                    fontWeight = FontWeight(200),
                    color = textColor
                )
            }

        if (users.isNotEmpty())
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 23.dp)
            ) {
                Text(
                    "Users found($count)",
                    fontWeight = FontWeight(200),
                    color = textColor
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
                    .padding(horizontal = 10.dp)
            ) {
                items(users) { user ->
                    var loadedAvatar by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(user.userId) {
                        val byte64 = profileViewModel.getAvatarFromServer(user.userId.toInt())
                        loadedAvatar = byte64
                    }

                    Spacer(modifier = Modifier.padding(vertical = 5.dp))

                    Column(
                        Modifier
                            .clickable {
                                coroutineScope.launch {
                                    viewModel.createChat(user, navController)
                                }
                            }
                            .background(buttonColor, shape = RoundedCornerShape(100))
                            .padding(8.dp)
                    )
                    {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row {
                                if (user.hasAvatar) {
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
                            }

                            Text(
                                text = user.userName,
                                color = textColor,
                                fontSize = 22.sp,
                                maxLines = 1,
                                fontWeight = FontWeight(200),
                                modifier = Modifier.padding(start = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }

}