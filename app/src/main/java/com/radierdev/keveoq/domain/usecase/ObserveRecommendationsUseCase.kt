package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.Recommendations
import com.radierdev.keveoq.domain.repository.MovieRepository
import com.radierdev.keveoq.domain.repository.SavedMovieRepository
import com.radierdev.keveoq.domain.rule.MoodCriteriaMapper
import com.radierdev.keveoq.domain.rule.RecommendationRules
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Observa as recomendações de um humor já filtradas pelas regras de negócio (RF03).
 *
 * Combina dois Flows: o cache de recomendações e os IDs assistidos. Quando um
 * filme é marcado como assistido, o segundo Flow emite e o filme some da lista
 * na hora, sem nova consulta à API (RN03).
 */
class ObserveRecommendationsUseCase(
    private val movieRepository: MovieRepository,
    private val savedMovieRepository: SavedMovieRepository,
) {
    operator fun invoke(mood: Mood): Flow<Recommendations> {
        val minVoteCount = MoodCriteriaMapper.map(mood).minVoteCount
        return combine(
            movieRepository.observeRecommendations(mood),
            savedMovieRepository.observeWatchedIds(),
        ) { recommendations, watchedIds ->
            recommendations.copy(
                movies = RecommendationRules.apply(recommendations.movies, watchedIds, minVoteCount),
            )
        }
    }
}
