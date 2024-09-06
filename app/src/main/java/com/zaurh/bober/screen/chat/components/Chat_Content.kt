package com.zaurh.bober.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun Chat_Content(
    chatScreenViewModel: ChatScreenViewModel,
    userData: UserData,
    currentUser: UserData,
    paddingValues: PaddingValues
) {
    val messageList = chatScreenViewModel.state.value.messages
    val filteredList = messageList.filter {
        it.senderUserId == currentUser.id && it.recipientUserId == userData.id
                || it.senderUserId == userData.id && it.recipientUserId == currentUser.id
    }

    var limit by remember { mutableIntStateOf(20) }
    val limitedList = filteredList.take(limit)

    val lazyState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(lazyState) {
        snapshotFlow { lazyState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                val totalItems = lazyState.layoutInfo.totalItemsCount

                if (lastVisibleItemIndex != null && lastVisibleItemIndex == totalItems - 1) {
                    limit += 20
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color.Transparent)
    ) {
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .weight(7f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumnScrollbar(
                    state = lazyState, settings = ScrollbarSettings.Default.copy(
                        thumbThickness = 2.dp,
                        thumbSelectedColor = colorResource(id = R.color.backgroundBottom),
                        thumbUnselectedColor = Color.Gray
                    )
                ) {


                    LazyColumn(
                        modifier = Modifier.padding(8.dp),
                        state = lazyState,
                        reverseLayout = true
                    ) {

                        items(limitedList) {
                            Chat_MessageItem(
                                messageData = it,
                                currentUserId = currentUser.id ?: ""
                            )
                        }
                    }
                }

            }
            val gifState = chatScreenViewModel.gifState.value
            val gifs = chatScreenViewModel.gifUrls.collectAsState()
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

