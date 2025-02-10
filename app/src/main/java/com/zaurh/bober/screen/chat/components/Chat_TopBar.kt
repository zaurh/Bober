package com.zaurh.bober.screen.chat.components

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.zaurh.bober.R
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.profile.ProfileViewModel
import com.zaurh.bober.ui.theme.outfitFamily
import com.zaurh.bober.util.lastSeenDay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat_TopBar(
    image: String,
    username: String,
    onClick: () -> Unit,
    navController: NavController,
    chatScreenViewModel: ChatScreenViewModel,
    profileViewModel: ProfileViewModel,
    ) {
    val profileData = chatScreenViewModel.profileDataState.collectAsState()
    val chatState = chatScreenViewModel.chatState.value
    val selectedMessages = chatScreenViewModel.selectedMessages.value

    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current


    val recipientOnline =
        if (chatState.recipientUserId == profileData.value?.id) chatState.recipientOnlineStatus
            ?: false else profileData.value?.online
            ?: false

    val recipientLastSeen = if (chatState.recipientUserId == profileData.value?.id) lastSeenDay(
        chatState.recipientLastSeen ?: profileData.value?.lastSeen
    ) else lastSeenDay(profileData.value?.lastSeen)

    val recipientIsTyping =
        if (chatState.recipientUserId == profileData.value?.id) chatState.recipientIsTyping
            ?: false else false



    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        title = {
            Column() {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selectedMessages.isNotEmpty()){
                        Text("${selectedMessages.size}", fontFamily = outfitFamily)
                    }else{
                        AsyncImage(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onClick()
                                },
                            model = image,
                            contentDescription = "",
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Column() {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = username, color = MaterialTheme.colorScheme.primary, fontFamily = outfitFamily)
                                if (recipientOnline) {
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Icon(
                                        modifier = Modifier.size(10.dp),
                                        painter = painterResource(id = R.drawable.online_ic),
                                        contentDescription = "",
                                        tint = colorResource(id = R.color.darkGreen)
                                    )
                                }
                            }
                            AnimatedVisibility(!recipientOnline && !recipientIsTyping && profileData.value?.lastSeen != null) {
                                Text(text = recipientLastSeen, fontSize = 12.sp, color = Color.Gray, fontFamily = outfitFamily)
                            }
                            AnimatedVisibility(recipientIsTyping) {
                                Text(text = "typing...", fontSize = 12.sp, color = Color.Gray, fontFamily = outfitFamily)
                            }
                        }
                    }


                }

            }

        },
        navigationIcon = {
                IconButton(onClick = {
                    if (selectedMessages.isNotEmpty()){
                        chatScreenViewModel.clearSelectMessages()
                    }else{
                        navController.popBackStack()
                    }
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                }

        },
        actions = {
            if (selectedMessages.isNotEmpty()){
                if (selectedMessages.size < 2){
                    val selectedMessage = selectedMessages.lastOrNull()
                    IconButton(onClick = {
                        chatScreenViewModel.enableReply(
                            username = selectedMessage?.senderUsername ?: "",
                            messageId = selectedMessage?.id ?: "",
                            messageText = selectedMessage?.text ?: ""
                        )
                    }) {
                        Icon(modifier = Modifier.size(20.dp),painter = painterResource(R.drawable.reply_ic), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = {
                        chatScreenViewModel.clearSelectMessages()
                        clipboardManager.setText(AnnotatedString(selectedMessages.last().text ?: ""))
                        Toast.makeText(context, "Message copied", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(modifier = Modifier.size(20.dp),painter = painterResource(R.drawable.copy_ic), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                if (selectedMessages.any { it.senderUsername == username }){

                    IconButton(onClick = {
                        profileViewModel.onReportStateChange()
                    }) {
                        Icon(modifier = Modifier.size(20.dp),painter = painterResource(R.drawable.report_ic), contentDescription = "", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }else{
                IconButton(onClick = {
                    chatScreenViewModel.tempGetRoomMessages()
                    chatScreenViewModel.openModalSheet()
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (chatState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.size(8.dp))
                        }
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "")
                    }
                }
            }

        }
    )
    LaunchedEffect(profileData.value) {
        profileData.value?.let {
            chatScreenViewModel.initializeChatState(
                recipientUserId = profileData.value?.id ?: "",
                recipientOnlineStatus = profileData.value?.online ?: false,
                recipientLastSeen = profileData.value?.lastSeen ?: 0L
            )

        }
    }
    LaunchedEffect(chatState) {
        Log.d("dfjkslfkjdfl", "${chatState.recipientUserId}")
        Log.d("dfjkslfkjdfl", "${profileData.value?.id}")
        Log.d("dfjkslfkjdfl", "${profileData.value?.online}")
        Log.d("dfjkslfkjdfl", "${chatState.recipientOnlineStatus}")
    }
}