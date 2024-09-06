package com.zaurh.bober.screen.settings.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleItem(
    title: String
) {
    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
    Spacer(modifier = Modifier.size(12.dp))
}