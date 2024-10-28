package com.zaurh.bober.data.user

import com.zaurh.bober.data.responses.MatchedUserData
import com.zaurh.bober.data.responses.WhoLikesData

data class UserUpdate(
    val fcmToken: String? = null,
    val name: String? = null,
    val username: String? = null,
    val imageUrl: List<String>? = null,
    val aboutMe: String? = null,
    val interests: List<Interests>? = null,
    val languages: List<Languages>? = null,
    val jobTitle: String? = null,
    val height: Int? = null,
    val birthDate: String? = null,
    val gender: Gender? = null,
    val matchList: List<MatchedUserData?>? = null,
    val gotLiked: List<WhoLikesData?>? = null,
    val ageRangeStart: Float? = null,
    val ageRangeEnd: Float? = null,
    val maximumDistance: Float? = null,
    val showFullDistance: Boolean? = null,
    val showMe: ShowMe? = null
)

