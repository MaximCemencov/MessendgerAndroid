package com.example.androidapp.screens.Drawer

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
import androidx.compose.material.DrawerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.androidapp.R
import com.example.androidapp.features.MyColors
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerBody(
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    isDarkTheme: Boolean,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
) {
    var sliderPosition by remember { mutableFloatStateOf(sharedViewModel.theme.intValue.toFloat()) }
    val sliderTexts = listOf("White", "Follow System", "Dark")

    val colors = MyColors
    val buttonColor = if (isDarkTheme) colors.buttonColorDarkTheme else colors.buttonColorWhiteTheme
    val textColor = if (isDarkTheme) colors.textColorDarkTheme else colors.textColorWhiteTheme

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sharedViewModel.userName,
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight(200)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = sliderTexts[sliderPosition.toInt()],
                    color = textColor,
                    fontWeight = FontWeight(200),
                )

                Spacer(modifier = Modifier.padding(horizontal = 12.5f.dp))

                Slider(
                    modifier = Modifier.width(60.dp),
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    onValueChangeFinished = {
                        sharedViewModel.theme.intValue = sliderPosition.toInt()
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

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .padding(20.dp)
                .clickable {
                    sharedViewModel.logout()
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate("registration")
                },
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Log out",
                fontWeight = FontWeight(200),
                color = textColor,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
            )
            Icon(
                painter = painterResource(R.drawable.logout),
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}