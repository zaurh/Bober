package com.zaurh.bober.screen.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.R
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.model.MessageData

@Composable
fun Chat_MessageItem(
    messageData: MessageData,
    currentUserId: String
) {
    val senderIsCurrent = messageData.senderUserId == currentUserId
    val messageText = messageData.text ?: ""
    val messageIsGif = messageText.endsWith(".gif")
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.Transparent),
        horizontalAlignment = if (senderIsCurrent) Alignment.End else Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(if (messageIsGif) 5 else 20))
                .background(
                    if (senderIsCurrent)
                        colorResource(id = R.color.darkGreen) else Color.DarkGray
                )
                .padding(if (messageIsGif) 0.dp else 8.dp)
            ,
            horizontalAlignment = if (senderIsCurrent) Alignment.End else Alignment.Start
        ) {
            if (messageIsGif){
                AnimatedGif(context = context, gifUrl = messageText, size = 100, circularSize = 60, clipPercentage = 5) {

                }
            }else{
                Text(
                    text = messageText,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.size(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = messageData.timestamp ?: "",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
                if (senderIsCurrent){
                    val tickIcon = when (messageData.status) {
                        MessageStatus.SENT -> R.drawable.sent_ic
                        MessageStatus.DELIVERED -> R.drawable.delivered_ic
                        MessageStatus.READ -> R.drawable.read_ic
                        MessageStatus.FAILED -> R.drawable.sent_ic
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    Image(
                        modifier = Modifier.size(14.dp),
                        painter = painterResource(id = tickIcon),
                        contentDescription = ""
                    )
                }

            }
        }


    }
}