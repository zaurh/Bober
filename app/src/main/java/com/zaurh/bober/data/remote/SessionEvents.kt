package com.zaurh.bober.data.remote

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tells the auth layer that the backend rejected our token. Lives apart from AuthRepo so the HTTP
 * client can report it without a dependency cycle (AuthRepo → UserApi → OkHttp → interceptor).
 */
@Singleton
class SessionEvents @Inject constructor() {
    private val _expired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val expired: SharedFlow<Unit> = _expired

    fun reportExpired() {
        _expired.tryEmit(Unit)
    }
}
