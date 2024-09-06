package com.zaurh.bober.data.tenor

data class TenorResponse(
    val results: List<GifResult>?
)

data class GifResult(
    val media: List<MediaFormat>?
)

data class MediaFormat(
    val gif: GifFormat?
)

data class GifFormat(
    val url: String?
)