package com.zaurh.bober.data.user

import com.zaurh.bober.data.responses.MatchedUserData

data class UserUpdate(
    val isPremium: Boolean? = null,
    val name: String? = null,
    val username: String? = null,
    val encryptedLocation: String? = null,
    val imageUrl: List<String>? = null,
    val aboutMe: String? = null,
    val interests: List<Interests>? = null,
    val languages: List<Languages>? = null,
    val jobTitle: String? = null,
    val height: Int? = null,
    val birthDate: String? = null,
    val gender: Gender? = null,
    val likedUsers: List<String>? = null,
    val gotLiked: List<String>? = null,
    val blockList: List<String>? = null,
    val chatList: List<ChatData>? = null,
    val matchList: List<MatchedUserData?>? = null,
    val ageRangeStart: Float? = null,
    val ageRangeEnd: Float? = null,
    val maximumDistance: Float? = null,
    val showFullDistance: Boolean? = null,
    val showMe: ShowMe? = null,
    val online: Boolean? = null,
    val lastSeen: Long? = null,
    val likeToken: Int? = null
)

