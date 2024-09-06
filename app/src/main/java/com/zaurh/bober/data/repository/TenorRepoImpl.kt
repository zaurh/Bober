package com.zaurh.bober.data.repository

import com.zaurh.bober.data.tenor.TenorApi
import com.zaurh.bober.domain.repository.TenorRepo
import javax.inject.Inject

class TenorRepoImpl @Inject constructor(
    private val api: TenorApi
): TenorRepo{

    override suspend fun searchGifs(query: String): List<String> {
        return try {
            val response = api.searchGifs(query)
            response.results?.flatMap { result ->
                result.media?.mapNotNull { mediaFormat ->
                    mediaFormat.gif?.url
                } ?: emptyList()
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun trendingGifs(): List<String> {
        return try {
            val response = api.trendingGifs()
            response.results?.flatMap { result ->
                result.media?.mapNotNull { mediaFormat ->
                    mediaFormat.gif?.url
                } ?: emptyList()
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }


}