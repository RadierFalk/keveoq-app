package com.radierdev.keveoq.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Resposta de `GET discover/movie`.
 * Só os campos usados pelo app são declarados; os demais são ignorados
 * pelo `Json { ignoreUnknownKeys = true }`.
 */
@Serializable
data class DiscoverResponseDto(
    @SerialName("page") val page: Int = 1,
    @SerialName("results") val results: List<MovieDto> = emptyList(),
)

/** Filme resumido, como vem em `discover/movie`. Campos opcionais podem vir nulos ou ausentes. */
@Serializable
data class MovieDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
)
