@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaurh.bober.screen.chat.modal_sheets

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R
import com.zaurh.bober.data.message.EmoteSealed
import com.zaurh.bober.data.user.ChatBackground
import com.zaurh.bober.data.user.ChatData
import com.zaurh.bober.model.MessageData
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.ui.components.Avatar
import com.zaurh.bober.ui.theme.color

/** Long-press sheet for a message: quick reactions, Reply and Copy. */
@Composable
fun Chat_MessageActionsSheet(chatScreenViewModel: ChatScreenViewModel, currentUserId: String) {
    val message = chatScreenViewModel.actionsFor.value ?: return
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val myReaction = message.emoted.firstOrNull { it.senderId == currentUserId }?.emoteSealed

    ModalBottomSheet(
        onDismissRequest = chatScreenViewModel::closeMessageActions,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(Modifier.navigationBarsPadding().padding(bottom = 16.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EmoteSealed.pickable.forEach { emote ->
                    val selected = emote == myReaction
                    Surface(
                        onClick = { chatScreenViewModel.react(message, emote) },
                        shape = CircleShape,
                        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                        border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(emote.displayName, style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
            }
            if (myReaction != null) {
                Text(
                    "Tap your reaction again to remove it",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            SheetAction(icon = R.drawable.reply_ic, title = "Reply") { chatScreenViewModel.startReply(message) }
            if (!message.isGif) {
                SheetAction(icon = R.drawable.copy_ic, title = "Copy text") {
                    clipboard.setText(AnnotatedString(message.text.orEmpty()))
                    chatScreenViewModel.closeMessageActions()
                    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
private fun SheetAction(icon: Int, title: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = { Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(22.dp)) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(onClick = onClick)
    )
}

/** Who reacted to a message. The current user's own reaction is listed first and can be removed. */
@Composable
fun Chat_ReactionsSheet(chatScreenViewModel: ChatScreenViewModel, currentUserId: String) {
    val message: MessageData = chatScreenViewModel.reactionsFor.value ?: return
    val reactions = message.emoted.sortedByDescending { it.senderId == currentUserId }

    ModalBottomSheet(
        onDismissRequest = chatScreenViewModel::closeReactions,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Text(
            "Reactions",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.navigationBarsPadding()
        ) {
            items(reactions, key = { it.senderId }) { reaction ->
                val mine = reaction.senderId == currentUserId
                ListItem(
                    headlineContent = { Text(if (mine) "You" else reaction.senderUsername) },
                    supportingContent = if (mine) {
                        { Text("Tap to remove") }
                    } else null,
                    leadingContent = { Avatar(reaction.senderImage, 44.dp) },
                    trailingContent = { Text(reaction.emoteSealed.displayName, style = MaterialTheme.typography.headlineSmall) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = if (mine) Modifier.clickable { chatScreenViewModel.removeMyReaction(message) } else Modifier
                )
            }
        }
    }
}

/** Per-chat background: a color (or the theme default) and an optional doodle pattern. Changes apply live. */
@Composable
fun Chat_BackgroundSheet(chatScreenViewModel: ChatScreenViewModel, chat: ChatData?) {
    if (!chatScreenViewModel.backgroundSheet.value) return
    val current = chat?.backgroundColor ?: ChatBackground.DEFAULT
    val doodle = chat?.doodleEnabled ?: false
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        listState.scrollToItem((ChatBackground.entries.indexOf(current) - 1).coerceAtLeast(0))
    }

    ModalBottomSheet(
        onDismissRequest = chatScreenViewModel::closeBackgroundSheet,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(Modifier.navigationBarsPadding().padding(bottom = 24.dp)) {
            Text("Chat background", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 24.dp))
            Text(
                if (chat == null) "Send a message first to customize this chat."
                else "Only you see this background.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(16.dp))
            LazyRow(
                state = listState,
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ChatBackground.entries) { background ->
                    Swatch(
                        background = background,
                        selected = background == current,
                        enabled = chat != null,
                    ) {
                        chat?.let { chatScreenViewModel.changeBackground(it.recipientId, background, doodle) }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            ListItem(
                headlineContent = { Text("Doodle pattern") },
                supportingContent = { Text("Draw a playful pattern behind messages") },
                trailingContent = {
                    Switch(
                        checked = doodle,
                        enabled = chat != null,
                        onCheckedChange = { checked ->
                            chat?.let { chatScreenViewModel.changeBackground(it.recipientId, current, checked) }
                        }
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            )
        }
    }
}

@Composable
private fun Swatch(background: ChatBackground, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val fill = background.color ?: MaterialTheme.colorScheme.surface
    val shape = RoundedCornerShape(16.dp)
    Box(
        Modifier
            .width(64.dp)
            .aspectRatio(0.75f)
            .clip(shape)
            .background(fill)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = shape
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (background == ChatBackground.DEFAULT) {
            Text("Default", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (selected) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(2.dp)
                )
            }
        }
    }
}
