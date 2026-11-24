package com.zaurh.bober.data.remote

import com.zaurh.bober.data.responses.MessageResponse
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

/**
 * Runs a backend call and turns every failure into a value built by [onError], so repositories never throw.
 *
 * The backend answers many errors with a non-2xx status *and* a JSON body such as
 * `{"success":false,"message":"You don't have enough like token."}`. Retrofit throws for those, so the
 * server's message is read from the error body instead of showing "HTTP 400".
 */
suspend inline fun <T> apiCall(onError: (message: String) -> T, block: () -> T): T {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        onError(e.serverMessage())
    } catch (e: IOException) {
        onError("No connection to the server. Please check your internet and try again.")
    } catch (e: Exception) {
        onError(e.message ?: "Something went wrong.")
    }
}

fun HttpException.serverMessage(): String {
    val body = runCatching { response()?.errorBody()?.string() }.getOrNull()
    val parsed = body?.let { runCatching { NetworkJson.decodeFromString<MessageResponse>(it) }.getOrNull() }
    return parsed?.message?.takeIf { it.isNotBlank() } ?: "Server error (${code()})."
}
