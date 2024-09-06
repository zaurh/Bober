package com.zaurh.bober.screen.profile.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R

@Composable
fun ProfileScreenButtonItem(
    title: String,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Button(colors = ButtonDefaults.buttonColors(
        disabledContainerColor = Color.White,
        containerColor = Color.White
    ),enabled = !isLoading,modifier = Modifier.fillMaxWidth(), onClick = { onClick() }) {
        Text(text = title, color = colorResource(id = R.color.darkBlue))
        Spacer(modifier = Modifier.size(8.dp))
        if (isLoading){
            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp,color = colorResource(id = R.color.darkBlue))
        }
    }
}