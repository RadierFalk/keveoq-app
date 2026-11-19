package com.radierdev.keveoq.domain.rule

import com.radierdev.keveoq.domain.model.Movie
import com.radierdev.keveoq.domain.model.MoodSearchCriteria

/**
 * Filtra a lista de recomendações antes de exibi-la (RN02, RN03 e RN04).
 *
 * Todas as operações preservam a ordem recebida (popularidade da API).
 */
object RecommendationRules {

    /**
     * @param movies filmes na ordem em que devem aparecer.
     * @param watchedIds IDs dos filmes marcados como assistidos.
     * @param minVoteCount mínimo de votos para o filme ser recomendado.
     */
    fun apply(
        movies: List<Movie>,
        watchedIds: Set<Int>,
        minVoteCount: Int = MoodSearchCriteria.DEFAULT_MIN_VOTE_COUNT,
    ): List<Movie> =
        movies
            .filter { it.voteCount >= minVoteCount } // RN02: mínimo de votos
            .filter { it.id !in watchedIds }         // RN03: sem assistidos
            .distinctBy { it.id }                    // RN04: sem duplicados (fica a 1ª ocorrência)
}
