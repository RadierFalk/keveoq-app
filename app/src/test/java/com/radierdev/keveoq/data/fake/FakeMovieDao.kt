package com.radierdev.keveoq.data.fake

import com.radierdev.keveoq.data.local.dao.MovieDao
import com.radierdev.keveoq.data.local.entity.MoodRecommendationEntity
import com.radierdev.keveoq.data.local.entity.MovieDetailsEntity
import com.radierdev.keveoq.data.local.entity.MovieEntity
import com.radierdev.keveoq.data.local.entity.RecommendedMovieRow
import com.radierdev.keveoq.domain.model.Mood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * DAO falso em memória que imita as consultas do Room, incluindo o JOIN
 * entre `mood_recommendations` e `movies` e a ordenação por posição.
 */
class FakeMovieDao : MovieDao() {

    private val movies = MutableStateFlow<Map<Int, MovieEntity>>(emptyMap())
    private val recommendations = MutableStateFlow<List<MoodRecommendationEntity>>(emptyList())
    private val details = MutableStateFlow<Map<Int, MovieDetailsEntity>>(emptyMap())

    /** Quando preenchido, toda escrita lança esta exceção (simula falha do banco). */
    var writeError: Exception? = null

    var replaceCalls = 0
        private set

    override fun observeRecommendations(mood: Mood): Flow<List<RecommendedMovieRow>> =
        combine(movies, recommendations) { movieMap, recs ->
            recs.filter { it.mood == mood }
                .sortedBy { it.position }
                .mapNotNull { rec ->
                    movieMap[rec.movieId]?.let { RecommendedMovieRow(it, rec.position, rec.updatedAt) }
                }
        }

    override suspend fun upsertMovies(movies: List<MovieEntity>) {
        writeError?.let { throw it }
        this.movies.value = this.movies.value + movies.associateBy { it.id }
    }

    override suspend fun deleteRecommendations(mood: Mood) {
        writeError?.let { throw it }
        recommendations.value = recommendations.value.filterNot { it.mood == mood }
    }

    override suspend fun upsertRecommendations(recommendations: List<MoodRecommendationEntity>) {
        writeError?.let { throw it }
        this.recommendations.value = this.recommendations.value + recommendations
    }

    override suspend fun replaceRecommendations(
        mood: Mood,
        movies: List<MovieEntity>,
        recommendations: List<MoodRecommendationEntity>,
    ) {
        replaceCalls++
        super.replaceRecommendations(mood, movies, recommendations)
    }

    override fun observeMovieDetails(movieId: Int): Flow<MovieDetailsEntity?> =
        details.map { it[movieId] }

    override suspend fun upsertMovieDetails(details: MovieDetailsEntity) {
        writeError?.let { throw it }
        this.details.value = this.details.value + (details.id to details)
    }
}
