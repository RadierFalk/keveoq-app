package com.radierdev.keveoq.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Resposta de `GET movie/{movie_id}?append_to_response=release_dates,watch/providers`.
 * O `append_to_response` faz a mesma resposta trazer as datas de lançamento por país
 * e os serviços de streaming, sem requisições extras.
 */
@Serializable
data class MovieDetailsDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String = "",
    @SerialName("overview") val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("runtime") val runtime: Int? = null,
    @SerialName("genres") val genres: List<GenreDto> = emptyList(),
    @SerialName("release_dates") val releaseDates: ReleaseDatesDto? = null,
    @SerialName("watch/providers") val watchProviders: WatchProvidersResponseDto? = null,
)

@Serializable
data class GenreDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
)

// ---------- release_dates ----------

@Serializable
data class ReleaseDatesDto(
    @SerialName("results") val results: List<CountryReleaseDatesDto> = emptyList(),
)

@Serializable
data class CountryReleaseDatesDto(
    @SerialName("iso_3166_1") val country: String,
    @SerialName("release_dates") val releaseDates: List<ReleaseDateDto> = emptyList(),
)

/** `type`: 1 Premiere, 2 Theatrical (limited), 3 Theatrical, 4 Digital, 5 Physical, 6 TV. */
@Serializable
data class ReleaseDateDto(
    @SerialName("type") val type: Int,
    @SerialName("release_date") val releaseDate: String = "",
)

// ---------- watch/providers (dados do JustWatch) ----------

@Serializable
data class WatchProvidersResponseDto(
    /** Chave: código do país ("BR", "US"...). */
    @SerialName("results") val results: Map<String, CountryWatchProvidersDto> = emptyMap(),
)

@Serializable
data class CountryWatchProvidersDto(
    @SerialName("link") val link: String? = null,
    @SerialName("flatrate") val flatrate: List<ProviderDto> = emptyList(),
    @SerialName("rent") val rent: List<ProviderDto> = emptyList(),
    @SerialName("buy") val buy: List<ProviderDto> = emptyList(),
)

@Serializable
data class ProviderDto(
    @SerialName("provider_id") val providerId: Int,
    @SerialName("provider_name") val providerName: String = "",
    @SerialName("logo_path") val logoPath: String? = null,
    @SerialName("display_priority") val displayPriority: Int = Int.MAX_VALUE,
)
