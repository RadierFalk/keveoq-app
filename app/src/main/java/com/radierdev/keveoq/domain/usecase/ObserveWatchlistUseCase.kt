package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.domain.model.WatchStatus
import com.radierdev.keveoq.domain.repository.SavedMovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Observa a lista "Quero assistir", do filme salvo mais recente para o mais antigo (RF09).
 *
 * @param filter status a exibir; `null` significa "todos".
 */
class ObserveWatchlistUseCase(
    private val savedMovieRepository: SavedMovieRepository,
) {
    operator fun invoke(filter: WatchStatus?): Flow<List<SavedMovie>> =
        savedMovieRepository.observeSavedMovies().map { movies ->
            if (filter == null) movies else movies.filter { it.status == filter }
        }
}
