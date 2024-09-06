package com.zaurh.bober.screen.account.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zaurh.bober.R

@Composable
fun LocationAllowDialog(
    dialogOpen: Boolean,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    if (dialogOpen) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { onDismiss() }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hi!\n\nAre we in the same city?",
                        color = colorResource(id = R.color.darkBlue),
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(
                        text = "Enable location sharing so we can match you with people nearby.\nYour exact location will remain private, encrypted and will not be shared.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                }
                Image(
                    painterResource(id = R.drawable.location_ic),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(200.dp)
                )
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.backgroundBottom)
                ), modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter), onClick = {
                    onClick()
                }) {
                    Text(text = "Allow", color = Color.White)
                }
            }

        }
    }
}