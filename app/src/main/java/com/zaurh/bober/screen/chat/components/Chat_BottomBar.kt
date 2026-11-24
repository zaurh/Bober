package com.zaurh.bober.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.zaurh.bober.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.zaurh.bober.screen.chat.ChatScreenViewModel

@Composable
fun ChatBottomBar(
    chatScreenViewModel: ChatScreenViewModel,
    modifier: Modifier = Modifier,
) {
    val gifMode = chatScreenViewModel.gifState.value
    val value = if (gifMode) chatScreenViewModel.tenorQuery.value else chatScreenViewModel.messageText.value

    val replyingTo = chatScreenViewModel.replyingTo.value

    Surface(color = MaterialTheme.colorScheme.surface, modifier = modifier) {
      Column(
          Modifier
              .fillMaxWidth()
              .navigationBarsPadding()
              .imePadding()
      ) {
        val currentUserId = chatScreenViewModel.userDataState.collectAsState().value?.id
        val profile = chatScreenViewModel.profileDataState.collectAsState().value
        AnimatedVisibility(visible = replyingTo != null && !gifMode) {
            if (replyingTo != null) {
                val author = if (replyingTo.senderUserId == currentUserId) "yourself"
                else profile?.name?.ifEmpty { null } ?: replyingTo.senderUsername.orEmpty()
                ReplyBar(author = author, text = replyingTo.preview, onClose = chatScreenViewModel::cancelReply)
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            TextField(
                value = value,
                onValueChange = if (gifMode) chatScreenViewModel::onTenorQueryChange else chatScreenViewModel::onMessageChange,
                placeholder = { Text(if (gifMode) "Search GIFs on Tenor" else "Message") },
                maxLines = 5,
                shape = MaterialTheme.shapes.extraLarge,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                leadingIcon = {
                    TextButton(onClick = chatScreenViewModel::onGifStateChange) {
                        if (gifMode) Icon(Icons.Default.Close, contentDescription = "Close GIFs")
                        else Text("GIF", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 52.dp)
            )
            if (!gifMode) {
                Spacer(Modifier.size(8.dp))
                FilledIconButton(
                    onClick = chatScreenViewModel::sendMessage,
                    enabled = value.isNotBlank(),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
      }
    }
}

@Composable
private fun ReplyBar(author: String, text: String, onClose: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp, top = 8.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(R.drawable.reply_ic),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(12.dp))
        Box(
            Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
        )
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            Text("Replying to $author", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, maxLines = 1)
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onClose) {
            Icon(Icons.Default.Close, contentDescription = "Cancel reply")
        }
    }
}
