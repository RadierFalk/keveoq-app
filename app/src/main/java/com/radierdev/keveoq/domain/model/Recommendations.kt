package com.radierdev.keveoq.domain.model

import java.time.Instant

/**
 * Recomendações guardadas para um humor.
 *
 * @property updatedAt momento da última atualização pela API, ou `null`
 * se este humor nunca foi buscado (cache vazio).
 */
data class Recommendations(
    val movies: List<Movie>,
    val updatedAt: Instant?,
)
