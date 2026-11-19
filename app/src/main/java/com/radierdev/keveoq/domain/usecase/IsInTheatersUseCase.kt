package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.domain.rule.TheatricalRule
import java.time.Clock
import java.time.LocalDate

/**
 * Diz se o filme está "Em cartaz nos cinemas" hoje (RF28, RN14).
 *
 * Existe para a presentation acessar a regra por um caso de uso, como as demais,
 * e para a data de hoje vir do [Clock] injetado (fixo nos testes).
 */
class IsInTheatersUseCase(
    private val clock: Clock,
) {
    operator fun invoke(theatricalReleaseBr: LocalDate?): Boolean =
        TheatricalRule.isInTheaters(theatricalReleaseBr, LocalDate.now(clock))
}
