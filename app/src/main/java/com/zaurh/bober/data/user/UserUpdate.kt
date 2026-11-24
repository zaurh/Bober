package com.zaurh.bober.data.user

import com.zaurh.bober.data.responses.MatchedUserData
import kotlinx.serialization.Serializable

/**
 * Body of `PUT /update_user`. Only non-null fields are sent, and only those change on the server.
 *
 * Likes, blocks, passes, premium, email and like tokens are owned by their dedicated endpoints and are
 * ignored by the backend here, so they are intentionally not part of this request.
 */
@Serializable
data class UserUpdate(
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
    /** Used to clear the "new match" badge. */
    val matchList: List<MatchedUserData>? = null,
    val ageRangeStart: Float? = null,
    val ageRangeEnd: Float? = null,
    val maximumDistance: Float? = null,
    val showFullDistance: Boolean? = null,
    val showMe: ShowMe? = null,
)
