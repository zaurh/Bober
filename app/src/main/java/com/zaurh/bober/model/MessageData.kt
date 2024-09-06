package com.zaurh.bober.model

import com.zaurh.bober.data.message.MessageStatus

data class MessageData(
    val id: String? = null,
    val text: String? = null,
    val senderUsername: String? = null,
    val senderUserId: String? = null,
    val recipientUserId: String? = null,
    val timestamp: String? = null,
    var status: MessageStatus = MessageStatus.SENT,
    var deliveredTimestamp: Long? = null,

    val recipientUsername: String? = null,
    val recipientOnlineStatus: Boolean? = null,
    val recipientLastSeen: Long? = null

)
