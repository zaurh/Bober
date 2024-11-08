package com.zaurh.bober.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Chat_SendMessageField(
    placeholder: String,
    value: String,
    gifState: Boolean,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onGif: () -> Unit,
) {

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp)
            .background(Color.Transparent)
            .clip(RoundedCornerShape(20))
            .padding(8.dp)
            .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(20))
            .imePadding(),
        placeholder = { Text(text = if (enabled) placeholder else "No internet connection") },
        enabled = enabled,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        value = value,
        onValueChange = onValueChange,
        trailingIcon = {
            IconButton(onClick = {
                onSend()
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "")
            }
        },
        leadingIcon = {
            IconButton(onClick = {
                onGif()
            }) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (gifState) "X" else "GIF", color = MaterialTheme.colorScheme.surface)
                }
            }
        }
    )

}