package com.zaurh.bober.data.requests

import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.data.user.Interests
import com.zaurh.bober.data.user.ShowMe


data class SignUpRequest(
    val username: String,
    val gender: Gender,
    val image: String,
    val dateOfBirth: String,
    val showMe: ShowMe,
    val interests: List<Interests>,
    val password: String,
)