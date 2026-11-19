package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.domain.model.MovieDetails
import com.radierdev.keveoq.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observa os detalhes guardados de um filme (RF06, RF07).
 * Emite `null` enquanto os detalhes ainda não foram buscados.
 */
class ObserveMovieDetailsUseCase(
    private val movieRepository: MovieRepository,
) {
    operator fun invoke(movieId: Int): Flow<MovieDetails?> =
        movieRepository.observeMovieDetails(movieId)
}
