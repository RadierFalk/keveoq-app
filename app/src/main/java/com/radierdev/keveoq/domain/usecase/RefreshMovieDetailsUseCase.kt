package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.core.network.NetworkMonitor
import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.repository.MovieRepository
import com.radierdev.keveoq.domain.rule.CachePolicy
import com.radierdev.keveoq.domain.rule.TrailerSelector
import kotlinx.coroutines.flow.first
import java.time.Clock

/**
 * Busca na API os detalhes e o trailer de um filme e grava no banco (RF07, RN09).
 *
 * - Detalhes guardados há menos de 24 h: não faz nada.
 * - Sem conexão: devolve `Failure(Network)` sem chamar a API.
 * - Trailer: procura primeiro nos vídeos em pt-BR; sem trailer válido, repete em en-US.
 * - Tudo ou nada: se qualquer requisição falhar, nada é gravado e o erro é devolvido,
 *   para o banco nunca guardar detalhes "pela metade" (por exemplo, sem trailer por falha de rede).
 */
class RefreshMovieDetailsUseCase(
    private val movieRepository: MovieRepository,
    private val networkMonitor: NetworkMonitor,
    private val clock: Clock,
) {
    suspend operator fun invoke(movieId: Int): AppResult<Unit> {
        val cached = movieRepository.observeMovieDetails(movieId).first()
        if (!CachePolicy.isExpired(cached?.updatedAt, clock.instant())) {
            return AppResult.Success(Unit)
        }
        if (!networkMonitor.isOnline.first()) {
            return AppResult.Failure(AppError.Network)
        }

        val details = when (val result = movieRepository.fetchMovieDetails(movieId)) {
            is AppResult.Success -> result.data
            is AppResult.Failure -> return result
        }
        val trailerKey = when (val result = findTrailerKey(movieId)) {
            is AppResult.Success -> result.data
            is AppResult.Failure -> return result
        }

        return movieRepository.saveMovieDetails(details.copy(trailerKey = trailerKey))
    }

    /** Tenta cada idioma em ordem e para no primeiro que tiver trailer válido. */
    private suspend fun findTrailerKey(movieId: Int): AppResult<String?> {
        for (language in TRAILER_LANGUAGES) {
            when (val result = movieRepository.fetchVideos(movieId, language)) {
                is AppResult.Success -> {
                    val key = TrailerSelector.selectKey(result.data)
                    if (key != null) return AppResult.Success(key)
                }
                is AppResult.Failure -> return result
            }
        }
        return AppResult.Success(null)
    }

    companion object {
        /** Idiomas em ordem de preferência para o trailer. */
        val TRAILER_LANGUAGES = listOf("pt-BR", "en-US")
    }
}
