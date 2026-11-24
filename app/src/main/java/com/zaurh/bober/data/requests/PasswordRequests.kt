package com.zaurh.bober.data.requests

import kotlinx.serialization.Serializable

/** Body of `POST /reset-password`: emails a reset link to the account with this (verified) address. */
@Serializable
data class SendEmailRequest(
    val email: String,
)

/** Body of `POST /check_current_password`. */
@Serializable
data class CheckCurrentPasswordRequest(
    val password: String,
)

/**
 * Body of `POST /update_password`. Allowed for the signed-in user changing their own password,
 * or for anyone holding a valid [token] from a reset email.
 */
@Serializable
data class UpdatePasswordRequest(
    val userId: String,
    val password: String,
    val confirmPassword: String,
    val token: String? = null,
)
