package com.radierdev.keveoq.data.repository

import com.radierdev.keveoq.core.dispatcher.AppDispatchers
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.data.local.dao.MovieDao
import com.radierdev.keveoq.data.mapper.recommendationEntitiesOf
import com.radierdev.keveoq.data.mapper.toDiscoverQuery
import com.radierdev.keveoq.data.mapper.toDomain
import com.radierdev.keveoq.data.mapper.toEntity
import com.radierdev.keveoq.data.mapper.toRecommendations
import com.radierdev.keveoq.data.remote.TmdbApi
import com.radierdev.keveoq.data.remote.dto.MovieDto
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.MoodSearchCriteria
import com.radierdev.keveoq.domain.model.MovieDetails
import com.radierdev.keveoq.domain.model.Recommendations
import com.radierdev.keveoq.domain.model.Video
import com.radierdev.keveoq.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Clock

/**
 * Implementação do [MovieRepository] com Room (fonte de verdade) e a API do TMDB.
 *
 * A tela nunca recebe dados direto da API: a rede só grava no banco, e o Flow
 * observado pela tela emite os dados novos sozinho (Offline First, seção 6).
 */
class MovieRepositoryImpl(
    private val api: TmdbApi,
    private val movieDao: MovieDao,
    private val clock: Clock,
    private val dispatchers: AppDispatchers,
) : MovieRepository {

    override fun observeRecommendations(mood: Mood): Flow<Recommendations> =
        movieDao.observeRecommendations(mood).map { rows -> rows.toRecommendations() }

    /**
     * Busca as páginas 1 e 2 (RN11) e substitui o cache do humor numa transação.
     *
     * Tudo ou nada: se qualquer página falhar, nada é gravado e o cache anterior
     * continua intacto (CA09). Filmes repetidos entre as páginas são gravados uma
     * só vez, na posição em que apareceram primeiro.
     */
    override suspend fun refreshRecommendations(
        mood: Mood,
        criteria: MoodSearchCriteria,
    ): AppResult<Unit> = withContext(dispatchers.io) {
        val query = criteria.toDiscoverQuery()
        val movies = mutableListOf<MovieDto>()

        for (page in PAGES) {
            val result = safeApiCall {
                api.discoverMovies(
                    withGenres = query.withGenres,
                    withoutGenres = query.withoutGenres,
                    minVoteCount = query.minVoteCount,
                    releaseDateLte = query.releaseDateLte,
                    page = page,
                )
            }
            when (result) {
                is AppResult.Success -> movies += result.data.results
                is AppResult.Failure -> return@withContext result
            }
        }

        val uniqueMovies = movies.distinctBy { it.id }
        safeDbCall {
            movieDao.replaceRecommendations(
                mood = mood,
                movies = uniqueMovies.map { it.toEntity() },
                recommendations = recommendationEntitiesOf(mood, uniqueMovies.map { it.id }, clock.instant()),
            )
        }
    }

    override fun observeMovieDetails(movieId: Int): Flow<MovieDetails?> =
        movieDao.observeMovieDetails(movieId).map { it?.toDomain() }

    override suspend fun fetchMovieDetails(movieId: Int): AppResult<MovieDetails> =
        withContext(dispatchers.io) {
            when (val result = safeApiCall { api.getMovieDetails(movieId) }) {
                is AppResult.Success -> AppResult.Success(result.data.toDomain(updatedAt = clock.instant()))
                is AppResult.Failure -> result
            }
        }

    override suspend fun fetchVideos(movieId: Int, language: String): AppResult<List<Video>> =
        withContext(dispatchers.io) {
            when (val result = safeApiCall { api.getMovieVideos(movieId, language) }) {
                is AppResult.Success -> AppResult.Success(result.data.results.map { it.toDomain() })
                is AppResult.Failure -> result
            }
        }

    override suspend fun saveMovieDetails(details: MovieDetails): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeDbCall { movieDao.upsertMovieDetails(details.toEntity()) }
        }

    private companion object {
        /** Páginas buscadas a cada atualização (até 40 filmes, RN11). */
        val PAGES = listOf(1, 2)
    }
}
