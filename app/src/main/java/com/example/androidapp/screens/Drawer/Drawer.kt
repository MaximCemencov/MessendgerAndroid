package com.example.androidapp.screens.Drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.androidapp.features.MyColors
import com.example.androidapp.features.generateRandomColor
import com.example.androidapp.viewModels.Profile.ProfileViewModel
import com.example.androidapp.viewModels.SharedViewModel


@Composable
fun DrawerBody(
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    isDarkTheme: Boolean,
    profileViewModel: ProfileViewModel,
) {
    var sliderPosition by remember { mutableFloatStateOf(sharedViewModel.theme.toFloat()) }
    val sliderTexts = listOf("White", "Follow System", "Dark")

    val colors = MyColors
    val buttonColor = if (isDarkTheme) colors.buttonColorDarkTheme else colors.buttonColorWhiteTheme
    val textColor = if (isDarkTheme) colors.textColorDarkTheme else colors.textColorWhiteTheme


    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.End
    ) {
        Row(
           Modifier
               .fillMaxWidth()
               .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (sharedViewModel.avatarBase64 == null) {
                    IconButton(onClick = {
                        navController.navigate("profile_screen")
                    }) {
                        Icon(
                            Icons.Rounded.Face,
                            contentDescription = null,
                            tint = generateRandomColor(),
                            modifier = Modifier.size(50.dp)
                        )
                    }
                } else {
                    Image(
                        bitmap = profileViewModel.base64ToImageBitmap(sharedViewModel.avatarBase64!!)!!
                            .asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .clickable {
                                navController.navigate("profile_screen")
                            }
                    )
                }

                Text(
                    text = sharedViewModel.userName,
                    color = textColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight(200),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = sliderTexts[sliderPosition.toInt()],
                    color = textColor,
                    fontWeight = FontWeight(200),
                )

                Spacer(Modifier.padding(horizontal = 12.5f.dp))

                Slider(
                    modifier = Modifier.width(60.dp),
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    onValueChangeFinished = {
                        sharedViewModel.theme = sliderPosition.toInt()
                        sharedViewModel.saveToSharedPreferences()
                    },
                    steps = 1,
                    valueRange = 0f..2f,
                    colors = SliderDefaults.colors(
                        thumbColor = textColor,
                        inactiveTrackColor = buttonColor,
                        activeTrackColor = buttonColor,
                        inactiveTickColor = Color.Red,
                        activeTickColor = Color.Red
                    )
                )
            }
        }
    }
}