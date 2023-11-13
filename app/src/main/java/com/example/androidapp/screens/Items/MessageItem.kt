package com.example.androidapp.screens.Items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidapp.DataClass.Message

@Composable
fun MessageItem(
    mess: Message,
) {
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
                        Color(0xFF00BB00))
                .padding(start = 17.dp, top = 5.dp, end = 17.dp, bottom = 5.dp),
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
        }
    }
}
