package com.radierdev.keveoq.domain.model

import java.time.Instant
import java.time.LocalDate

/**
 * Detalhes completos de um filme.
 *
 * @property trailerKey chave do vídeo no YouTube, ou `null` se o filme não tem trailer válido.
 * @property updatedAt momento em que os detalhes foram buscados na API.
 */
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val voteAverage: Double,
    val releaseYear: Int?,
    val runtimeMinutes: Int?,
    val genres: List<String>,
    val trailerKey: String?,
    val updatedAt: Instant,
    /** Onde assistir no Brasil (RN13). */
    val watchProviders: WatchProviders = WatchProviders.EMPTY,
    /** Estreia nos cinemas do Brasil, para o selo "Em cartaz" (RN14); `null` se não houver. */
    val theatricalReleaseBr: LocalDate? = null,
)
