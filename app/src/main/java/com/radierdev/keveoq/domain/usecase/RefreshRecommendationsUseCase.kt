package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.core.network.NetworkMonitor
import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.repository.MovieRepository
import com.radierdev.keveoq.domain.rule.CachePolicy
import com.radierdev.keveoq.domain.rule.MoodCriteriaMapper
import kotlinx.coroutines.flow.first
import java.time.Clock

/**
 * Decide se as recomendações de um humor precisam ser atualizadas e, se sim,
 * pede ao repositório que busque na API (RF04, RF05, RN05).
 *
 * - Cache válido e sem `force`: não faz nada (sucesso sem requisição).
 * - Sem conexão: devolve `Failure(Network)` sem chamar a API.
 * - Caso contrário: busca e grava; o Flow observado pela tela emite os dados novos.
 *
 * @param force `true` no pull-to-refresh: ignora a validade do cache.
 */
class RefreshRecommendationsUseCase(
    private val movieRepository: MovieRepository,
    private val networkMonitor: NetworkMonitor,
    private val clock: Clock,
) {
    suspend operator fun invoke(mood: Mood, force: Boolean = false): AppResult<Unit> {
        if (!force) {
            val cached = movieRepository.observeRecommendations(mood).first()
            if (!CachePolicy.isExpired(cached.updatedAt, clock.instant())) {
                return AppResult.Success(Unit)
            }
        }
        if (!networkMonitor.isOnline.first()) {
            return AppResult.Failure(AppError.Network)
        }
        return movieRepository.refreshRecommendations(mood, MoodCriteriaMapper.map(mood))
    }
}
