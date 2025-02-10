package com.zaurh.bober.data.message

import kotlinx.serialization.Serializable

@Serializable
enum class EmoteSealed(val displayName: String) {
    NOTHING(""),
    THUMB("\uD83D\uDC4D"),
    HEART("❤\uFE0F"),
    LAUGH("\uD83D\uDE02"),
    CRY("\uD83D\uDE22"),
    ANGRY("😡")
}