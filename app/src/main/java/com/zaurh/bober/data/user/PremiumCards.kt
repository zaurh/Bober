package com.zaurh.bober.data.user

import kotlinx.serialization.Serializable

@Serializable
enum class PremiumCards(val displayName: String){
    NONE(displayName = "None"),
    BOBERIUM(displayName = "Boberium"),
    TITANIUM(displayName = "Titanium")
}
