package com.zaurh.bober.data.remote

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/** Single owner of the backend JWT. Every authenticated call reads the token from here. */
@Singleton
class TokenStore @Inject constructor(
    private val prefs: SharedPreferences
) {
    val token: String?
        get() = prefs.getString(KEY_JWT, null)?.takeIf { it.isNotBlank() }

    val hasToken: Boolean get() = token != null

    fun save(token: String) {
        prefs.edit().putString(KEY_JWT, token).apply()
    }

    fun clear() {
        prefs.edit().remove(KEY_JWT).apply()
    }

    private companion object {
        // Kept for compatibility with tokens saved by earlier app versions.
        const val KEY_JWT = "jwt"
    }
}
