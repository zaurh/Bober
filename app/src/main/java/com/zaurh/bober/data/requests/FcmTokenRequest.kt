package com.zaurh.bober.data.requests

import kotlinx.serialization.Serializable

/** Body of `PUT /store_fcm_token`: this device's Firebase Cloud Messaging token. */
@Serializable
data class FcmTokenRequest(
    val fcmToken: String
)
