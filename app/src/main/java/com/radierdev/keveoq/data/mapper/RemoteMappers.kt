package com.radierdev.keveoq.data.mapper

import com.radierdev.keveoq.data.remote.dto.MovieDetailsDto
import com.radierdev.keveoq.data.remote.dto.MovieDto
import com.radierdev.keveoq.data.remote.dto.VideoDto
import com.radierdev.keveoq.domain.model.MoodSearchCriteria
import com.radierdev.keveoq.domain.model.Movie
import com.radierdev.keveoq.domain.model.MovieDetails
import com.radierdev.keveoq.domain.model.Video
import java.time.Instant

// Conversões entre os DTOs do TMDB e os modelos de domínio.
// DTOs nunca saem da camada data: quem está fora só vê os modelos do domain.

const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"

/** Monta a URL completa do pôster, ou `null` se o filme não tiver pôster. */
fun posterUrlOf(posterPath: String?): String? =
    posterPath?.takeIf { it.isNotBlank() }?.let { "$POSTER_BASE_URL$it" }

/** Extrai o ano de uma data "AAAA-MM-DD". Datas vazias ou inválidas viram `null`. */
fun releaseYearOf(releaseDate: String?): Int? =
    releaseDate?.take(4)?.toIntOrNull()

fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title,
    posterUrl = posterUrlOf(posterPath),
    releaseYear = releaseYearOf(releaseDate),
    voteAverage = voteAverage,
    voteCount = voteCount,
)

/**
 * Converte os detalhes, ainda sem trailer: o trailer vem de outra requisição
 * e é escolhido no domain.
 *
 * @param updatedAt momento da busca, usado na validade do cache.
 */
fun MovieDetailsDto.toDomain(updatedAt: Instant): MovieDetails = MovieDetails(
    id = id,
    title = title,
    overview = overview.orEmpty(),
    posterUrl = posterUrlOf(posterPath),
    voteAverage = voteAverage,
    releaseYear = releaseYearOf(releaseDate),
    runtimeMinutes = runtime?.takeIf { it > 0 },
    genres = genres.map { it.name },
    trailerKey = null,
    updatedAt = updatedAt,
)

fun VideoDto.toDomain(): Video = Video(
    key = key,
    site = site,
    type = type,
    official = official,
)

/** Parâmetros de `discover/movie` derivados dos critérios do humor. */
data class DiscoverQuery(
    val withGenres: String,
    val withoutGenres: String?,
    val minVoteCount: Int,
    val releaseDateLte: String?,
)

/**
 * Converte os critérios do domain nos parâmetros da API:
 * gêneros aceitos unidos com `|` (OU) e excluídos com `,`.
 * Listas vazias e datas ausentes viram `null`, para não irem na URL.
 */
fun MoodSearchCriteria.toDiscoverQuery(): DiscoverQuery = DiscoverQuery(
    withGenres = withGenres.joinToString(separator = "|"),
    withoutGenres = withoutGenres.takeIf { it.isNotEmpty() }?.joinToString(separator = ","),
    minVoteCount = minVoteCount,
    releaseDateLte = releasedUntil?.toString(), // LocalDate.toString() já é ISO: "1999-12-31"
)
