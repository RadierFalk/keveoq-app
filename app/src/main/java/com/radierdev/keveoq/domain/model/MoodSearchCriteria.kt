package com.radierdev.keveoq.domain.model

import java.time.LocalDate

/**
 * Critérios de busca no TMDB derivados de um [Mood].
 *
 * @property withGenres gêneros aceitos (combinados com OU na consulta).
 * @property withoutGenres gêneros que devem ser excluídos.
 * @property releasedUntil data máxima de lançamento, ou `null` se não houver limite.
 * @property minVoteCount mínimo de votos para evitar notas distorcidas.
 */
data class MoodSearchCriteria(
    val withGenres: Set<Int>,
    val withoutGenres: Set<Int> = emptySet(),
    val releasedUntil: LocalDate? = null,
    val minVoteCount: Int = DEFAULT_MIN_VOTE_COUNT,
) {
    companion object {
        const val DEFAULT_MIN_VOTE_COUNT = 100
    }
}
