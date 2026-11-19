package com.radierdev.keveoq.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Resposta de `GET movie/{movie_id}/videos`. */
@Serializable
data class VideosResponseDto(
    @SerialName("results") val results: List<VideoDto> = emptyList(),
)

@Serializable
data class VideoDto(
    @SerialName("key") val key: String = "",
    @SerialName("site") val site: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("official") val official: Boolean = false,
)
