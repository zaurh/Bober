package com.zaurh.bober.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.R
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.model.MessageData
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun Chat_Content(
    chatScreenViewModel: ChatScreenViewModel,
    currentUser: UserData,
) {
    val profileData = chatScreenViewModel.profileDataState.collectAsState()

    val emptyChatGif = chatScreenViewModel.emptyChatGif.value
    val lazyState = rememberLazyListState()
    val context = LocalContext.current
    val chatState = chatScreenViewModel.chatState.value
    var recipientMessages by remember { mutableStateOf<List<MessageData>>(listOf()) }
    val gifState = chatScreenViewModel.gifState.value
    val gifs = chatScreenViewModel.gifUrls.collectAsState()

    LaunchedEffect(lazyState) {
        snapshotFlow { lazyState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                val totalItems = lazyState.layoutInfo.totalItemsCount

                if (lastVisibleItemIndex != null && lastVisibleItemIndex == totalItems - 1) {
                    if (recipientMessages.size > 15){
                        chatScreenViewModel.getRecipientMessages(profileData.value?.id ?: "")
                    }
                }
            }
    }

    LaunchedEffect(chatState.recipientMessages) {
        chatState.recipientMessages?.let {
            recipientMessages = it
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Image(
            painter = painterResource(id = R.drawable.doodle),
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f),
            contentScale = ContentScale.Crop
        )
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .weight(7f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (chatState.recipientMessagesEmpty && profileData.value != null && emptyChatGif.enabled) {
                    LaunchedEffect(true) {
                        chatScreenViewModel.onEmptyMessageGif()
                    }
                    Text(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(20)
                            )
                            .background(MaterialTheme.colorScheme.background)
                            .padding(4.dp),
                        text = "Tap to send",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            Font(R.font.bober_font)
                        )
                    )
                    AnimatedGif(
                        context = context,
                        gifUrl = emptyChatGif.gifUrl,
                        size = 180,
                        circularSize = 40,
                        clipPercentage = 20
                    ) {
                        chatScreenViewModel.onMessageChange(it)
                        chatScreenViewModel.sendMessage()
                    }

                    IconButton(onClick = {
                        chatScreenViewModel.onEmptyMessageGif()
                    }) {
                        Icon(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .padding(4.dp)
                                .size(30.dp),
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                } else {
                    LazyColumnScrollbar(
                        state = lazyState, settings = ScrollbarSettings.Default.copy(
                            thumbThickness = 2.dp,
                            thumbSelectedColor = colorResource(id = R.color.backgroundBottom),
                            thumbUnselectedColor = Color.Gray
                        )
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                            if (chatState.recipientDataIsLoading || chatState.recipientMessagesLoading ) {
                                CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp),color = MaterialTheme.colorScheme.onSecondary)
                            }
                            LazyColumn(
                                modifier = Modifier.padding(8.dp),
                                state = lazyState,
                                reverseLayout = true
                            ) {
                                items(recipientMessages) {
                                    Chat_MessageItem(
                                        messageData = it,
                                        currentUserId = currentUser.id ?: ""
                                    )
                                }
                            }
                        }

                    }

                }

            }

            AnimatedVisibility(visible = gifState) {
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .weight(3f)
                ) {
                    items(gifs.value) {
                        AnimatedGif(
                            context = context,
                            gifUrl = it,
                            size = 80,
                            circularSize = 40,
                            clipPercentage = 20
                        ) {
                            chatScreenViewModel.onMessageChange(it)
                            chatScreenViewModel.sendMessage()
                        }
                    }
                }
            }


        }
    }

}




