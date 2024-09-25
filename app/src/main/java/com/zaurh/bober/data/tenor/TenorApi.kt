package com.zaurh.bober.data.tenor

import com.zaurh.bober.util.Constants.TENOR_API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface TenorApi {
    @GET("search")
    suspend fun searchGifs(
        @Query("q") query: String,
        @Query("key") apiKey: String = TENOR_API_KEY,
        @Query("limit") limit: Int = 20
    ): TenorResponse

    @GET("trending")
    suspend fun trendingGifs(
        @Query("key") apiKey: String = TENOR_API_KEY
    ): TenorResponse
}
