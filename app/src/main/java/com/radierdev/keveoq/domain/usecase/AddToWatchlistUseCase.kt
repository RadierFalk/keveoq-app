package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.domain.model.AddToWatchlistResult
import com.radierdev.keveoq.domain.model.MovieDetails
import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.domain.model.WatchStatus
import com.radierdev.keveoq.domain.repository.SavedMovieRepository
import java.time.Clock

/**
 * Adiciona um filme à lista "Quero assistir" com status `WANT_TO_WATCH` (RF08).
 *
 * Recusa o filme se ele já estiver na lista, com qualquer status (RN06).
 * Título, pôster e ano são copiados para a lista funcionar sozinha, mesmo
 * que o cache de recomendações seja substituído.
 */
class AddToWatchlistUseCase(
    private val savedMovieRepository: SavedMovieRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(details: MovieDetails): AddToWatchlistResult {
        if (savedMovieRepository.getSavedMovie(details.id) != null) {
            return AddToWatchlistResult.AlreadyInList
        }
        savedMovieRepository.save(
            SavedMovie(
                movieId = details.id,
                title = details.title,
                posterUrl = details.posterUrl,
                releaseYear = details.releaseYear,
                status = WatchStatus.WANT_TO_WATCH,
                rating = null,
                savedAt = clock.instant(),
            ),
        )
        return AddToWatchlistResult.Added
    }
}
