package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.RandomMovieResult
import kotlinx.coroutines.flow.first
import kotlin.random.Random

/**
 * Sorteia um filme para quem não quer escolher um humor (RF25, RN12).
 *
 * 1. Embaralha os humores.
 * 2. Para cada humor, pede a atualização das recomendações exatamente como a tela
 *    de recomendações faz: com cache válido não chama a API; offline sem cache,
 *    apenas falha (sem chamar a API).
 * 3. Lê as recomendações já filtradas (sem assistidos, repetidos ou poucos votos).
 * 4. No primeiro humor com filmes, sorteia um deles.
 *
 * O [Random] é injetado: no app é `Random.Default`; nos testes, um gerador com
 * semente fixa, o que torna o sorteio reproduzível.
 */
class PickRandomMovieUseCase(
    private val refreshRecommendations: RefreshRecommendationsUseCase,
    private val observeRecommendations: ObserveRecommendationsUseCase,
    private val random: Random,
) {
    suspend operator fun invoke(): RandomMovieResult {
        for (mood in Mood.entries.shuffled(random)) {
            // Uma falha aqui não interrompe o sorteio: o humor pode ter cache,
            // e, se não tiver, o próximo humor é tentado.
            refreshRecommendations(mood)
            val movies = observeRecommendations(mood).first().movies
            if (movies.isNotEmpty()) {
                return RandomMovieResult.Found(movieId = movies.random(random).id, mood = mood)
            }
        }
        return RandomMovieResult.NoMovieAvailable
    }
}
