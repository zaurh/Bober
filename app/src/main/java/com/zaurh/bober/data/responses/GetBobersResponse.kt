package com.zaurh.bober.data.responses

import com.zaurh.bober.data.user.BoberData
import kotlinx.serialization.Serializable

@Serializable
data class GetBobersResponse(
    val success: Boolean,
    val boberDataList: List<BoberData> = listOf(),
    val message: String = "No message.",
)