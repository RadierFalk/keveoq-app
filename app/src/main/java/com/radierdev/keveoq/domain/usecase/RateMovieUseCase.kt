package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.model.RateMovieResult
import com.radierdev.keveoq.domain.model.WatchStatus
import com.radierdev.keveoq.domain.repository.SavedMovieRepository
import com.radierdev.keveoq.domain.rule.RatingRules

/**
 * Grava a avaliação pessoal de um filme (RF11, RN08). Também serve para trocar a nota.
 *
 * - Nota fora de 1 a 5: `InvalidRating`.
 * - Filme fora da lista ou ainda não assistido: `NotWatched`.
 */
class RateMovieUseCase(
    private val savedMovieRepository: SavedMovieRepository,
) {
    suspend operator fun invoke(movieId: Int, rating: Int): RateMovieResult {
        if (!RatingRules.isValid(rating)) {
            return RateMovieResult.InvalidRating
        }
        val saved = when (val result = savedMovieRepository.getSavedMovie(movieId)) {
            is AppResult.Success -> result.data
            is AppResult.Failure -> return RateMovieResult.Failure(result.error)
        }
        if (saved?.status != WatchStatus.WATCHED) {
            return RateMovieResult.NotWatched
        }
        return when (val result = savedMovieRepository.updateRating(movieId, rating)) {
            is AppResult.Success -> RateMovieResult.Rated
            is AppResult.Failure -> RateMovieResult.Failure(result.error)
        }
    }
}
