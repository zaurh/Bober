package com.zaurh.bober.screen.chat.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.ui.components.Avatar
import com.zaurh.bober.util.lastSeenDay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat_TopBar(
    image: String,
    username: String,
    onClick: () -> Unit,
    navController: NavController,
    chatScreenViewModel: ChatScreenViewModel
) {
    val profile = chatScreenViewModel.profileDataState.collectAsState().value
    val chatState = chatScreenViewModel.state.value
    val live = chatState.recipientUserId != null && chatState.recipientUserId == profile?.id

    val online = if (live) chatState.recipientOnlineStatus ?: false else profile?.online ?: false
    val typing = live && chatState.recipientIsTyping == true
    val lastSeen = lastSeenDay(if (live) chatState.recipientLastSeen ?: profile?.lastSeen else profile?.lastSeen)

    val status = when {
        typing -> "typing…"
        online -> "online"
        profile?.lastSeen != null && lastSeen.isNotEmpty() -> lastSeen
        else -> ""
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        title = {
            Row(Modifier.clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
                Avatar(url = image, size = 40.dp, online = online)
                Spacer(Modifier.size(12.dp))
                Column {
                    Text(
                        profile?.name?.ifEmpty { null } ?: username,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    AnimatedContent(targetState = status, label = "status") { text ->
                        if (text.isNotEmpty()) {
                            Text(
                                text,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (typing || online) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        actions = {
            if (chatState.isLoading) {
                CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = chatScreenViewModel::openModalSheet) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
        }
    )

    LaunchedEffect(profile) {
        profile?.let {
            chatScreenViewModel.initializeChatState(
                recipientUserId = it.id ?: "",
                recipientOnlineStatus = it.online ?: false,
                recipientLastSeen = it.lastSeen ?: 0L
            )
        }
    }
}
