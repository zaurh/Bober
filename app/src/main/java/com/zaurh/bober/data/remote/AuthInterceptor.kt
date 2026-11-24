package com.zaurh.bober.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Adds `Authorization: Bearer <jwt>` to every backend request (REST and the chat WebSocket handshake).
 * The backend reads the user id from the token, so endpoints never need it passed explicitly.
 */
class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
    private val sessionEvents: SessionEvents,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenStore.token
        if (token == null || request.header(AUTHORIZATION) != null) {
            return chain.proceed(request)
        }
        val response = chain.proceed(
            request.newBuilder().header(AUTHORIZATION, "Bearer $token").build()
        )
        // Our token was rejected (expired or the server's secret changed): end the session.
        if (response.code == HTTP_UNAUTHORIZED) {
            tokenStore.clear()
            sessionEvents.reportExpired()
        }
        return response
    }

    private companion object {
        const val AUTHORIZATION = "Authorization"
        const val HTTP_UNAUTHORIZED = 401
    }
}
