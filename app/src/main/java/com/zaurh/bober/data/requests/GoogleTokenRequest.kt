package com.zaurh.bober.data.requests

import kotlinx.serialization.Serializable

/** Body of `POST /google_verify`: a Google ID token. The backend signs in (or creates) that Google account. */
@Serializable
data class GoogleTokenRequest(
    val token: String,
)
