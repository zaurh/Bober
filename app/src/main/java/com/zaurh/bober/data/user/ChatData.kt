package com.zaurh.bober.data.user

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class ChatData(
    val recipientId: String,
    val recipientImage: String,
    val recipientUsername: String,
    val backgroundColor: BackgroundColor,
    val doodleEnabled: Boolean,
    val lastUpdated: Long,
    val extendPage: Int = 0,
    val extendedFull: Boolean = false,

    )

fun ChatData.toUserData() = UserData(
    id = recipientId,
    imageUrl = listOf(recipientImage),
    username = recipientUsername
)

enum class BackgroundColor(val color: Color) {
    // Defaults
    DEFAULT(color = Color.Transparent),

    // Purples
    PURPLE(Color(0xFF800080)),
    PURPLE_LIGHT(Color(0xFFDA70D6)),
    PURPLE_DARK(Color(0xFF4B0082)),
    LAVENDER(Color(0xFFE6E6FA)),
    VIOLET(Color(0xFFEE82EE)),

    // Reds
    RED(Color(0xFFFF0000)),
    RED_LIGHT(Color(0xFFFFA07A)),
    RED_DARK(Color(0xFF8B0000)),
    PINK(Color(0xFFFFC0CB)),
    CRIMSON(Color(0xFFDC143C)),

    // Yellows
    YELLOW(Color(0xFFFFFF00)),
    YELLOW_LIGHT(Color(0xFFFFFFE0)),
    YELLOW_DARK(Color(0xFFCCCC00)),
    GOLD(Color(0xFFFFD700)),
    ORANGE(Color(0xFFFFA500)),

    // Blues
    BLUE(Color(0xFF0000FF)),
    BLUE_LIGHT(Color(0xFFADD8E6)),
    BLUE_DARK(Color(0xFF00008B)),
    SKY_BLUE(Color(0xFF87CEEB)),
    NAVY(Color(0xFF000080)),

    // Greens
    GREEN(Color(0xFF008000)),
    GREEN_LIGHT(Color(0xFF90EE90)),
    GREEN_DARK(Color(0xFF006400)),
    MINT(Color(0xFF98FF98)),
    LIME(Color(0xFF00FF00)),

    // Grays
    GRAY(Color(0xFF808080)),
    GRAY_LIGHT(Color(0xFFD3D3D3)),
    GRAY_DARK(Color(0xFFA9A9A9)),
    SILVER(Color(0xFFC0C0C0)),
    CHARCOAL(Color(0xFF36454F)),

    // Browns
    BROWN(Color(0xFFA52A2A)),
    BROWN_LIGHT(Color(0xFFDEB887)),
    BROWN_DARK(Color(0xFF654321)),
    TAN(Color(0xFFD2B48C)),
    BEIGE(Color(0xFFF5F5DC)),

    // Whites & Blacks
    WHITE(Color(0xFFFFFFFF)),
    BLACK(Color(0xFF000000)),
    IVORY(Color(0xFFFFFFF0)),
    SNOW(Color(0xFFFFFAFA)),
    COAL(Color(0xFF2C2C2C))
}
