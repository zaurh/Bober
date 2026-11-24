package com.zaurh.bober.data.user

import com.zaurh.bober.data.responses.MatchedUserData
import com.zaurh.bober.data.responses.WhoLikesData
import kotlinx.serialization.Serializable

/** The user as returned by the backend (`/get_user_data`, `/sign_in`, `/update_user`, `/users/...`). */
@Serializable
data class UserData(
    val id: String? = "",
    /** Set for accounts created with Google sign-in (they have no password of their own). */
    val googleId: String? = "",
    val premiumCards: List<PremiumCardData>? = listOf(),
    val name: String? = "",
    val email: String? = "",
    val emailIsVerified: Boolean? = false,
    val gender: Gender? = Gender.PREFER_NOT_TO_SAY,
    val username: String? = "",
    val encryptedLocation: String? = "",
    val aboutMe: String? = "",
    val interests: List<Interests>? = listOf(),
    val languages: List<Languages>? = listOf(),
    val jobTitle: String? = "",
    val height: Int? = 0,
    val birthDate: String? = "",
    val imageUrl: List<String>? = listOf(),
    val likedUsers: List<String>? = listOf(),
    val passedUsers: List<PassedUserData>? = listOf(),
    val gotLiked: List<WhoLikesData>? = listOf(),
    val blockList: List<String>? = listOf(),
    val chatList: List<ChatData>? = listOf(),
    val matchList: List<MatchedUserData>? = listOf(),
    val ageRangeStart: Float? = 18f,
    val ageRangeEnd: Float? = 100f,
    val maximumDistance: Float? = 200f,
    val showFullDistance: Boolean? = true,
    val showMe: ShowMe? = ShowMe.EVERYONE,
    val online: Boolean? = false,
    val lastSeen: Long? = 0,
    val likeToken: Int? = 0
) {
    /** The backend has no `isPremium` flag; a user is premium while any activated card hasn't expired. */
    val isPremium: Boolean
        get() = premiumCards.orEmpty().any { it.isActive() }
}
