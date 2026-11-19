package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.domain.repository.SavedMovieRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observa se um filme está na lista e com qual status e nota (RF08).
 * A tela de detalhes usa isso para decidir a ação: "Quero assistir",
 * "Já assisti" ou as estrelas. Emite `null` se o filme não está na lista.
 */
class ObserveSavedMovieUseCase(
    private val savedMovieRepository: SavedMovieRepository,
) {
    operator fun invoke(movieId: Int): Flow<SavedMovie?> =
        savedMovieRepository.observeSavedMovie(movieId)
}
