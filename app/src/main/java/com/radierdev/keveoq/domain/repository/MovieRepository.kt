package com.radierdev.keveoq.domain.repository

import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.MoodSearchCriteria
import com.radierdev.keveoq.domain.model.MovieDetails
import com.radierdev.keveoq.domain.model.Recommendations
import com.radierdev.keveoq.domain.model.Video
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso aos filmes (recomendações e detalhes).
 *
 * O domain declara o que precisa; a camada data implementa com Room e Retrofit.
 * O banco local é a única fonte de verdade: os métodos `observe` leem do banco,
 * e os de rede apenas atualizam o banco ou devolvem dados para serem gravados.
 * Nenhum método lança exceção: as falhas voltam como [AppResult.Failure].
 */
interface MovieRepository {

    /**
     * Recomendações guardadas de um humor, na ordem de popularidade, sem filtros
     * de regra de negócio. Sem cache, emite lista vazia com `updatedAt` nulo.
     */
    fun observeRecommendations(mood: Mood): Flow<Recommendations>

    /** Busca na API as recomendações do humor e substitui o cache dele. */
    suspend fun refreshRecommendations(mood: Mood, criteria: MoodSearchCriteria): AppResult<Unit>

    /** Detalhes guardados de um filme, ou `null` se ainda não foram buscados. */
    fun observeMovieDetails(movieId: Int): Flow<MovieDetails?>

    /**
     * Busca os detalhes na API (em pt-BR), sem gravar.
     * O resultado vem sem trailer (`trailerKey` nulo) e com `updatedAt` no momento da busca.
     */
    suspend fun fetchMovieDetails(movieId: Int): AppResult<MovieDetails>

    /** Busca na API os vídeos do filme no idioma informado (por exemplo, "pt-BR"). */
    suspend fun fetchVideos(movieId: Int, language: String): AppResult<List<Video>>

    /** Grava (ou substitui) os detalhes de um filme no banco. */
    suspend fun saveMovieDetails(details: MovieDetails)
}
