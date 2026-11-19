package com.radierdev.keveoq.domain.fake

import com.radierdev.keveoq.domain.model.Movie
import com.radierdev.keveoq.domain.model.MovieDetails
import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.domain.model.WatchStatus
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

/** Dados e relógio fixos compartilhados pelos testes do domain. */
object TestData {

    val NOW: Instant = Instant.parse("2026-11-19T12:00:00Z")
    val clock: Clock = Clock.fixed(NOW, ZoneOffset.UTC)

    fun movie(id: Int, voteCount: Int = 500) = Movie(
        id = id,
        title = "Filme $id",
        posterUrl = null,
        releaseYear = 2020,
        voteAverage = 7.5,
        voteCount = voteCount,
    )

    fun details(id: Int, updatedAt: Instant = NOW, trailerKey: String? = null) = MovieDetails(
        id = id,
        title = "Filme $id",
        overview = "Sinopse do filme $id",
        posterUrl = "https://image.tmdb.org/t/p/w500/poster$id.jpg",
        voteAverage = 7.5,
        releaseYear = 2020,
        runtimeMinutes = 120,
        genres = listOf("Drama"),
        trailerKey = trailerKey,
        updatedAt = updatedAt,
    )

    fun saved(
        id: Int,
        status: WatchStatus = WatchStatus.WANT_TO_WATCH,
        rating: Int? = null,
        savedAt: Instant = NOW,
    ) = SavedMovie(
        movieId = id,
        title = "Filme $id",
        posterUrl = null,
        releaseYear = 2020,
        status = status,
        rating = rating,
        savedAt = savedAt,
    )
}
