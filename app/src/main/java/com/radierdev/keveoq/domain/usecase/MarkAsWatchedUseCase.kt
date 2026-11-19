package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.domain.model.MarkAsWatchedResult
import com.radierdev.keveoq.domain.model.WatchStatus
import com.radierdev.keveoq.domain.repository.SavedMovieRepository

/**
 * Marca um filme salvo como assistido: `WANT_TO_WATCH` → `WATCHED` (RF10, RN07).
 * O status só avança; não existe caminho de volta.
 */
class MarkAsWatchedUseCase(
    private val savedMovieRepository: SavedMovieRepository,
) {
    suspend operator fun invoke(movieId: Int): MarkAsWatchedResult {
        val saved = savedMovieRepository.getSavedMovie(movieId)
            ?: return MarkAsWatchedResult.NotInList
        if (saved.status == WatchStatus.WATCHED) {
            return MarkAsWatchedResult.AlreadyWatched
        }
        savedMovieRepository.updateStatus(movieId, WatchStatus.WATCHED)
        return MarkAsWatchedResult.Marked
    }
}
