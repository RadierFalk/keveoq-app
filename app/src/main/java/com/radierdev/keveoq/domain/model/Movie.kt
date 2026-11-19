package com.radierdev.keveoq.domain.model

/** Filme resumido, como aparece na lista de recomendações. */
data class Movie(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val releaseYear: Int?,
    val voteAverage: Double,
    val voteCount: Int,
)
