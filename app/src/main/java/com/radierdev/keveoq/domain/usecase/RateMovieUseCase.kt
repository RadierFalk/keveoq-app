package com.radierdev.keveoq.domain.usecase

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
        val saved = savedMovieRepository.getSavedMovie(movieId)
        if (saved?.status != WatchStatus.WATCHED) {
            return RateMovieResult.NotWatched
        }
        savedMovieRepository.updateRating(movieId, rating)
        return RateMovieResult.Rated
    }
}
