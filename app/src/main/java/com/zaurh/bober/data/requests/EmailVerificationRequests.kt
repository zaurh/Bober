package com.zaurh.bober.data.requests

import kotlinx.serialization.Serializable

/** Body of `POST /send_email_verification`: the backend emails a link with a token to this address. */
@Serializable
data class VerifyEmailRequest(
    val email: String
)

/** Body of `POST /validate_email_token`: the token from the emailed link. */
@Serializable
data class ValidateTokenRequest(
    val token: String
)
