package com.zaurh.bober.data.push

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.zaurh.bober.data.remote.apiCall
import com.zaurh.bober.data.requests.FcmTokenRequest
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.user.UserApi
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/** Keeps the backend's copy of this device's FCM token in sync with the signed-in user. */
@Singleton
class PushTokenRegistrar @Inject constructor(
    private val api: UserApi,
) {
    /** Call after sign-in (and on app start while signed in). */
    suspend fun register() {
        val token = runCatching { FirebaseMessaging.getInstance().token.await() }
            .onFailure { Log.w(TAG, "Couldn't get FCM token", it) }
            .getOrNull() ?: return
        upload(token)
    }

    /** Called by FCM when the token rotates. */
    suspend fun upload(token: String) {
        val response = apiCall({ MessageResponse(success = false, message = it) }) {
            api.storeFcmToken(FcmTokenRequest(token))
        }
        if (!response.success) Log.w(TAG, "Storing FCM token failed: ${response.message}")
    }

    /** On sign-out: invalidates the token so this device stops getting the old account's pushes. */
    suspend fun unregister() {
        runCatching { FirebaseMessaging.getInstance().deleteToken().await() }
            .onFailure { Log.w(TAG, "Couldn't delete FCM token", it) }
    }

    private companion object {
        const val TAG = "PushTokenRegistrar"
    }
}
