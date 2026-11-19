package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.core.result.AppResult
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
        val saved = when (val result = savedMovieRepository.getSavedMovie(movieId)) {
            is AppResult.Success -> result.data ?: return MarkAsWatchedResult.NotInList
            is AppResult.Failure -> return MarkAsWatchedResult.Failure(result.error)
        }
        if (saved.status == WatchStatus.WATCHED) {
            return MarkAsWatchedResult.AlreadyWatched
        }
        return when (val result = savedMovieRepository.updateStatus(movieId, WatchStatus.WATCHED)) {
            is AppResult.Success -> MarkAsWatchedResult.Marked
            is AppResult.Failure -> MarkAsWatchedResult.Failure(result.error)
        }
    }
}
