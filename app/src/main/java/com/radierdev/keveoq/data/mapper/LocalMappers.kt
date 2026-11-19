package com.radierdev.keveoq.data.mapper

import com.radierdev.keveoq.data.local.entity.MoodRecommendationEntity
import com.radierdev.keveoq.data.local.entity.MovieDetailsEntity
import com.radierdev.keveoq.data.local.entity.MovieEntity
import com.radierdev.keveoq.data.local.entity.RecommendedMovieRow
import com.radierdev.keveoq.data.local.entity.SavedMovieEntity
import com.radierdev.keveoq.data.remote.dto.MovieDto
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.Movie
import com.radierdev.keveoq.domain.model.MovieDetails
import com.radierdev.keveoq.domain.model.Recommendations
import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.data.local.entity.StoredProvider
import com.radierdev.keveoq.data.local.entity.StoredWatchProviders
import com.radierdev.keveoq.domain.model.WatchProvider
import com.radierdev.keveoq.domain.model.WatchProviders
import java.time.Instant
import java.time.LocalDate

// Conversões entre as entidades do Room e os modelos de domínio.
// Datas são gravadas como epoch em milissegundos (Long) e viram Instant no domínio.

fun MovieDto.toEntity(): MovieEntity = MovieEntity(
    id = id,
    title = title,
    posterUrl = posterUrlOf(posterPath),
    releaseYear = releaseYearOf(releaseDate),
    voteAverage = voteAverage,
    voteCount = voteCount,
)

fun MovieEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    posterUrl = posterUrl,
    releaseYear = releaseYear,
    voteAverage = voteAverage,
    voteCount = voteCount,
)

/** Uma linha por filme recomendado, com a posição na lista. */
fun recommendationEntitiesOf(
    mood: Mood,
    movieIds: List<Int>,
    updatedAt: Instant,
): List<MoodRecommendationEntity> =
    movieIds.mapIndexed { index, movieId ->
        MoodRecommendationEntity(
            mood = mood,
            movieId = movieId,
            position = index,
            updatedAt = updatedAt.toEpochMilli(),
        )
    }

/**
 * Converte as linhas do banco nas recomendações do domínio.
 * A data do cache é o `updatedAt` mais recente; sem linhas, é `null` (cache vazio).
 */
fun List<RecommendedMovieRow>.toRecommendations(): Recommendations = Recommendations(
    movies = sortedBy { it.position }.map { it.movie.toDomain() },
    updatedAt = maxOfOrNull { it.updatedAt }?.let(Instant::ofEpochMilli),
)

fun MovieDetailsEntity.toDomain(): MovieDetails = MovieDetails(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    voteAverage = voteAverage,
    releaseYear = releaseYear,
    runtimeMinutes = runtimeMinutes,
    genres = genres,
    trailerKey = trailerKey,
    updatedAt = Instant.ofEpochMilli(updatedAt),
    watchProviders = watchProviders.toDomain(),
    theatricalReleaseBr = theatricalReleaseBr?.let(LocalDate::ofEpochDay),
)

fun MovieDetails.toEntity(): MovieDetailsEntity = MovieDetailsEntity(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    voteAverage = voteAverage,
    releaseYear = releaseYear,
    runtimeMinutes = runtimeMinutes,
    genres = genres,
    trailerKey = trailerKey,
    updatedAt = updatedAt.toEpochMilli(),
    watchProviders = watchProviders.toStored(),
    theatricalReleaseBr = theatricalReleaseBr?.toEpochDay(),
)

fun StoredWatchProviders.toDomain(): WatchProviders = WatchProviders(
    subscription = subscription.map { it.toDomain() },
    rent = rent.map { it.toDomain() },
    buy = buy.map { it.toDomain() },
    link = link,
)

fun WatchProviders.toStored(): StoredWatchProviders = StoredWatchProviders(
    subscription = subscription.map { it.toStored() },
    rent = rent.map { it.toStored() },
    buy = buy.map { it.toStored() },
    link = link,
)

private fun StoredProvider.toDomain() = WatchProvider(id = id, name = name, logoUrl = logoUrl)

private fun WatchProvider.toStored() = StoredProvider(id = id, name = name, logoUrl = logoUrl)

fun SavedMovieEntity.toDomain(): SavedMovie = SavedMovie(
    movieId = movieId,
    title = title,
    posterUrl = posterUrl,
    releaseYear = releaseYear,
    status = status,
    rating = rating,
    savedAt = Instant.ofEpochMilli(savedAt),
)

fun SavedMovie.toEntity(): SavedMovieEntity = SavedMovieEntity(
    movieId = movieId,
    title = title,
    posterUrl = posterUrl,
    releaseYear = releaseYear,
    status = status,
    rating = rating,
    savedAt = savedAt.toEpochMilli(),
)
