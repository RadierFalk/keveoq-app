package com.radierdev.keveoq.domain.rule

import java.time.LocalDate

/**
 * Estimativa de "Em cartaz nos cinemas" (RN14).
 *
 * O TMDB não informa salas nem quando o filme sai de cartaz; por isso considera-se
 * em cartaz da data de estreia nos cinemas do Brasil até 42 dias (6 semanas) depois.
 */
object TheatricalRule {

    const val DAYS_IN_THEATERS = 42L

    /**
     * @param releaseDate estreia nos cinemas do Brasil, ou `null` se não houver.
     * @param today data de hoje (vinda do `Clock` injetado; fixa nos testes).
     */
    fun isInTheaters(releaseDate: LocalDate?, today: LocalDate): Boolean {
        if (releaseDate == null) return false
        val lastDay = releaseDate.plusDays(DAYS_IN_THEATERS)
        return !today.isBefore(releaseDate) && !today.isAfter(lastDay)
    }
}
