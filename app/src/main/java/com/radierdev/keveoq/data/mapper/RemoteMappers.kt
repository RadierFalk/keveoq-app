package com.radierdev.keveoq.data.mapper

import com.radierdev.keveoq.data.remote.dto.MovieDetailsDto
import com.radierdev.keveoq.data.remote.dto.MovieDto
import com.radierdev.keveoq.data.remote.dto.ProviderDto
import com.radierdev.keveoq.data.remote.dto.ReleaseDatesDto
import com.radierdev.keveoq.data.remote.dto.VideoDto
import com.radierdev.keveoq.data.remote.dto.WatchProvidersResponseDto
import com.radierdev.keveoq.domain.model.MoodSearchCriteria
import com.radierdev.keveoq.domain.model.Movie
import com.radierdev.keveoq.domain.model.MovieDetails
import com.radierdev.keveoq.domain.model.Video
import com.radierdev.keveoq.domain.model.WatchProvider
import com.radierdev.keveoq.domain.model.WatchProviders
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeParseException

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
    watchProviders = watchProviders.toDomainForBrazil(),
    theatricalReleaseBr = releaseDates.theatricalReleaseInBrazil(),
)

// ---------- Onde assistir (RN13) e estreia nos cinemas (RN14) ----------

const val BRAZIL = "BR"
const val PROVIDER_LOGO_BASE_URL = "https://image.tmdb.org/t/p/w92"

/** Tipos de lançamento do TMDB que contam como cinema: limitado (2) e amplo (3). */
private val THEATRICAL_RELEASE_TYPES = setOf(2, 3)

/** Serviços do Brasil, por grupo, na ordem de prioridade e sem repetidos. */
fun WatchProvidersResponseDto?.toDomainForBrazil(): WatchProviders {
    val brazil = this?.results?.get(BRAZIL) ?: return WatchProviders.EMPTY
    return WatchProviders(
        subscription = brazil.flatrate.toDomainProviders(),
        rent = brazil.rent.toDomainProviders(),
        buy = brazil.buy.toDomainProviders(),
        link = brazil.link?.takeIf { it.isNotBlank() },
    )
}

private fun List<ProviderDto>.toDomainProviders(): List<WatchProvider> =
    sortedBy { it.displayPriority } // sortedBy é estável: empates mantêm a ordem da API
        .distinctBy { it.providerId }
        .map { dto ->
            WatchProvider(
                id = dto.providerId,
                name = dto.providerName,
                logoUrl = dto.logoPath?.takeIf { it.isNotBlank() }?.let { "$PROVIDER_LOGO_BASE_URL$it" },
            )
        }

/** Primeira estreia nos cinemas do Brasil (tipos 2 ou 3), ou `null`. */
fun ReleaseDatesDto?.theatricalReleaseInBrazil(): LocalDate? =
    this?.results
        ?.firstOrNull { it.country == BRAZIL }
        ?.releaseDates
        ?.filter { it.type in THEATRICAL_RELEASE_TYPES }
        ?.mapNotNull { parseReleaseDate(it.releaseDate) }
        ?.minOrNull()

/** A API usa "2024-06-20T00:00:00.000Z"; só a parte da data interessa. */
private fun parseReleaseDate(value: String): LocalDate? =
    try {
        LocalDate.parse(value.take(10))
    } catch (e: DateTimeParseException) {
        null
    }

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
