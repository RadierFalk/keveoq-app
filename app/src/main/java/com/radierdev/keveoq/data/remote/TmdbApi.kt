package com.radierdev.keveoq.data.remote

import com.radierdev.keveoq.data.remote.dto.DiscoverResponseDto
import com.radierdev.keveoq.data.remote.dto.MovieDetailsDto
import com.radierdev.keveoq.data.remote.dto.VideosResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Endpoints do TMDB usados pelo app. O Retrofit gera a implementação.
 * A autenticação é feita pelo [AuthInterceptor], não aqui.
 * Parâmetros `null` não são enviados na URL.
 */
interface TmdbApi {

    /**
     * @param withGenres IDs unidos com `|` (OU), por exemplo "35|16|10751".
     * @param withoutGenres IDs unidos com `,`, ou `null` se não houver exclusão.
     * @param releaseDateLte data máxima no formato ISO ("1999-12-31"), ou `null`.
     */
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_genres") withGenres: String,
        @Query("without_genres") withoutGenres: String?,
        @Query("vote_count.gte") minVoteCount: Int,
        @Query("primary_release_date.lte") releaseDateLte: String?,
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String = SORT_BY_POPULARITY,
        @Query("language") language: String = DEFAULT_LANGUAGE,
    ): DiscoverResponseDto

    /** Detalhes + datas de lançamento + onde assistir, numa única requisição. */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = DEFAULT_LANGUAGE,
        @Query("append_to_response") appendToResponse: String = DETAILS_APPEND,
    ): MovieDetailsDto

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String,
    ): VideosResponseDto

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val DEFAULT_LANGUAGE = "pt-BR"
        const val SORT_BY_POPULARITY = "popularity.desc"
        const val DETAILS_APPEND = "release_dates,watch/providers"
    }
}
