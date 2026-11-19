package com.radierdev.keveoq.domain.fake

import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.MoodSearchCriteria
import com.radierdev.keveoq.domain.model.MovieDetails
import com.radierdev.keveoq.domain.model.Recommendations
import com.radierdev.keveoq.domain.model.Video
import com.radierdev.keveoq.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Repositório falso em memória. O teste define o que a "API" devolve
 * e confere quantas vezes cada chamada de rede foi feita.
 */
class FakeMovieRepository : MovieRepository {

    private val recommendations = MutableStateFlow<Map<Mood, Recommendations>>(emptyMap())
    private val details = MutableStateFlow<Map<Int, MovieDetails>>(emptyMap())

    // Respostas configuráveis da "API"
    var refreshResult: AppResult<Recommendations> =
        AppResult.Success(Recommendations(emptyList(), null))
    var fetchDetailsResult: AppResult<MovieDetails>? = null
    val videosByLanguage = mutableMapOf<String, AppResult<List<Video>>>()

    /** Quando preenchido, a gravação dos detalhes no "banco" falha com este erro. */
    var saveDetailsFailure: AppError? = null

    // Registro das chamadas
    var refreshCalls = 0
        private set
    var lastCriteria: MoodSearchCriteria? = null
        private set
    var fetchDetailsCalls = 0
        private set
    var saveDetailsCalls = 0
        private set
    val videoLanguagesRequested = mutableListOf<String>()

    fun setRecommendations(mood: Mood, value: Recommendations) {
        recommendations.value = recommendations.value + (mood to value)
    }

    fun setDetails(value: MovieDetails) {
        details.value = details.value + (value.id to value)
    }

    fun storedDetails(movieId: Int): MovieDetails? = details.value[movieId]

    override fun observeRecommendations(mood: Mood): Flow<Recommendations> =
        recommendations.map { it[mood] ?: Recommendations(emptyList(), null) }

    override suspend fun refreshRecommendations(
        mood: Mood,
        criteria: MoodSearchCriteria,
    ): AppResult<Unit> {
        refreshCalls++
        lastCriteria = criteria
        return when (val result = refreshResult) {
            is AppResult.Success -> {
                setRecommendations(mood, result.data)
                AppResult.Success(Unit)
            }
            is AppResult.Failure -> result
        }
    }

    override fun observeMovieDetails(movieId: Int): Flow<MovieDetails?> =
        details.map { it[movieId] }

    override suspend fun fetchMovieDetails(movieId: Int): AppResult<MovieDetails> {
        fetchDetailsCalls++
        return checkNotNull(fetchDetailsResult) { "fetchDetailsResult não configurado no teste" }
    }

    override suspend fun fetchVideos(movieId: Int, language: String): AppResult<List<Video>> {
        videoLanguagesRequested += language
        return videosByLanguage[language] ?: AppResult.Success(emptyList())
    }

    override suspend fun saveMovieDetails(details: MovieDetails): AppResult<Unit> {
        saveDetailsCalls++
        saveDetailsFailure?.let { return AppResult.Failure(it) }
        setDetails(details)
        return AppResult.Success(Unit)
    }
}
