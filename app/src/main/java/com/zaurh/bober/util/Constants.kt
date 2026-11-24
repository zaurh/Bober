package com.zaurh.bober.util

object Constants {

    /** The Ktor backend (BoberBackend, github.com/zaurh/com.zaurh.boberbackend). Use https so the JWT is never sent in clear text. */
    const val BASE_URL = "https://bober.zaurhasanov.com"
//    const val BASE_URL = "http://10.0.2.2:8080" // Local backend from the Android emulator

    /** OkHttp maps https:// to wss:// for WebSockets. */
    const val WEBSOCKET_URL = "$BASE_URL/chat-socket"

    /**
     * Web OAuth client of the Firebase/Google Cloud project. Google sign-in asks for an ID token for this
     * client, and the backend (`/google_verify`) only accepts tokens issued for it (its `AUDIENCE`).
     */
    const val GOOGLE_WEB_CLIENT_ID = "107296471290-669ebbhg158lsqapptts39j60prtdreb.apps.googleusercontent.com"

    //Tenor GIF
    const val TENOR_BASE_URL = "https://api.tenor.com/v1/"
    const val TENOR_API_KEY = "LIVDSRZULELA"
}
