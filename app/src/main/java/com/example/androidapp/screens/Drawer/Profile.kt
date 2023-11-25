package com.example.androidapp.screens.Drawer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidapp.features.MyColors
import com.example.androidapp.features.generateRandomColor
import com.example.androidapp.viewModels.Profile.ProfileViewModel
import com.example.androidapp.viewModels.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    sharedViewModel: SharedViewModel,
    isDarkTheme: Boolean,
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val colors = MyColors
    val backgroundColor = if (isDarkTheme) colors.backgroundColorDarkTheme else colors.backgroundColorWhiteTheme
    val buttonColor = if (isDarkTheme) colors.buttonColorDarkTheme else colors.buttonColorWhiteTheme
    val textColor = if (isDarkTheme) colors.textColorDarkTheme else colors.textColorWhiteTheme

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.sendNewAvatarToServer(uri)
            }
        }
    )


    Column {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = textColor
                    )
                }
            },
            actions = {
                TextButton(onClick = {
                    sharedViewModel.logout()
                    navController.navigate("registration")
                    sharedViewModel.webSocket.close(1000, "Log out")
                }) { Text(
                    text = "Log Out",
                    color = textColor
                )}
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = backgroundColor)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (sharedViewModel.avatarBase64 != null) {
                Image(
                    bitmap = viewModel.base64ToImageBitmap(sharedViewModel.avatarBase64!!)!!.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                )
            } else {
                var color by remember { mutableStateOf(generateRandomColor()) }

                Icon(
                    Icons.Rounded.Face,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .size(200.dp)
                        .clickable { color = generateRandomColor() }
                        .clip(CircleShape)
                )
            }

            Text(
                text = sharedViewModel.userName,
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            Row {
                Button(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor
                    )
                ) {
                    Text(
                        if (sharedViewModel.avatarBase64 != null) "Change Avatar"
                        else "set Avatar Picture",
                        color = textColor
                    )
                }
                if (sharedViewModel.avatarBase64 != null) {
                    Button(
                        onClick = { viewModel.removeAvatar() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor
                        ),
                    ) {
                        Text(
                            "Delete Avatar",
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}
