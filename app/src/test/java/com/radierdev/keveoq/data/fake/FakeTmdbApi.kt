package com.radierdev.keveoq.data.fake

import com.radierdev.keveoq.data.remote.TmdbApi
import com.radierdev.keveoq.data.remote.dto.DiscoverResponseDto
import com.radierdev.keveoq.data.remote.dto.MovieDetailsDto
import com.radierdev.keveoq.data.remote.dto.MovieDto
import com.radierdev.keveoq.data.remote.dto.VideosResponseDto

/**
 * API do TMDB falsa. O teste define as respostas (ou exceções) de cada página
 * e confere quais requisições foram feitas.
 */
class FakeTmdbApi : TmdbApi {

    val discoverPages = mutableMapOf<Int, List<MovieDto>>()
    val discoverErrors = mutableMapOf<Int, Exception>()
    var detailsResponse: MovieDetailsDto? = null
    var detailsError: Exception? = null
    val videosByLanguage = mutableMapOf<String, VideosResponseDto>()

    data class DiscoverCall(
        val withGenres: String,
        val withoutGenres: String?,
        val minVoteCount: Int,
        val releaseDateLte: String?,
        val page: Int,
        val sortBy: String,
        val language: String,
    )

    val discoverCalls = mutableListOf<DiscoverCall>()
    var detailsCalls = 0
        private set

    override suspend fun discoverMovies(
        withGenres: String,
        withoutGenres: String?,
        minVoteCount: Int,
        releaseDateLte: String?,
        page: Int,
        sortBy: String,
        language: String,
    ): DiscoverResponseDto {
        discoverCalls += DiscoverCall(withGenres, withoutGenres, minVoteCount, releaseDateLte, page, sortBy, language)
        discoverErrors[page]?.let { throw it }
        return DiscoverResponseDto(page = page, results = discoverPages[page].orEmpty())
    }

    override suspend fun getMovieDetails(movieId: Int, language: String): MovieDetailsDto {
        detailsCalls++
        detailsError?.let { throw it }
        return checkNotNull(detailsResponse) { "detailsResponse não configurado no teste" }
    }

    override suspend fun getMovieVideos(movieId: Int, language: String): VideosResponseDto =
        videosByLanguage[language] ?: VideosResponseDto()
}
