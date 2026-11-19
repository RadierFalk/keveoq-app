package com.radierdev.keveoq.domain.model

import java.time.Instant

/**
 * Filme salvo na lista "Quero assistir".
 *
 * @property rating nota pessoal de 1 a 5, ou `null` se ainda não foi avaliado.
 */
data class SavedMovie(
    val movieId: Int,
    val title: String,
    val posterUrl: String?,
    val releaseYear: Int?,
    val status: WatchStatus,
    val rating: Int?,
    val savedAt: Instant,
)
