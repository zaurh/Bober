package com.zaurh.bober.domain.repository

interface TenorRepo {

    suspend fun searchGifs(query: String): List<String>
    suspend fun emptyChatGif(): String
    suspend fun trendingGifs(): List<String>
}