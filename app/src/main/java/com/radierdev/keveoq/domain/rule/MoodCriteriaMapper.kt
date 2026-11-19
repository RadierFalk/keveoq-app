package com.radierdev.keveoq.domain.rule

import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.MoodSearchCriteria
import java.time.LocalDate

/**
 * Traduz um [Mood] nos critérios de busca do TMDB (RN01 e RN02).
 *
 * O `when` não tem `else`: se um novo humor for criado, o código só volta
 * a compilar quando ele ganhar seus critérios aqui.
 */
object MoodCriteriaMapper {

    /** Último dia aceito para filmes do humor Nostálgico. */
    val NOSTALGIC_RELEASED_UNTIL: LocalDate = LocalDate.of(1999, 12, 31)

    fun map(mood: Mood): MoodSearchCriteria = when (mood) {
        Mood.HAPPY -> MoodSearchCriteria(
            withGenres = setOf(TmdbGenre.COMEDY, TmdbGenre.ANIMATION, TmdbGenre.FAMILY),
        )

        Mood.SAD -> MoodSearchCriteria(
            withGenres = setOf(TmdbGenre.DRAMA, TmdbGenre.ROMANCE),
        )

        Mood.EXCITED -> MoodSearchCriteria(
            withGenres = setOf(TmdbGenre.ACTION, TmdbGenre.ADVENTURE),
        )

        Mood.ANXIOUS -> MoodSearchCriteria(
            withGenres = setOf(TmdbGenre.COMEDY, TmdbGenre.ANIMATION, TmdbGenre.FAMILY),
            withoutGenres = setOf(TmdbGenre.HORROR, TmdbGenre.THRILLER),
        )

        Mood.BORED -> MoodSearchCriteria(
            withGenres = setOf(TmdbGenre.MYSTERY, TmdbGenre.SCIENCE_FICTION, TmdbGenre.THRILLER),
        )

        Mood.ROMANTIC -> MoodSearchCriteria(
            withGenres = setOf(TmdbGenre.ROMANCE),
        )

        Mood.NOSTALGIC -> MoodSearchCriteria(
            withGenres = setOf(TmdbGenre.ADVENTURE, TmdbGenre.FAMILY),
            releasedUntil = NOSTALGIC_RELEASED_UNTIL,
        )

        Mood.BRAVE -> MoodSearchCriteria(
            withGenres = setOf(TmdbGenre.HORROR, TmdbGenre.THRILLER),
        )
    }
}
