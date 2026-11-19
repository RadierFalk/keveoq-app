package com.radierdev.keveoq.domain.rule

import java.time.Duration
import java.time.Instant

/** Validade do cache de recomendações de cada humor (RN05). */
object CachePolicy {

    val VALIDITY: Duration = Duration.ofHours(24)

    /**
     * Indica se o cache precisa ser atualizado.
     *
     * - `updatedAt` nulo (nunca buscado): expirado.
     * - `updatedAt` no futuro (relógio do aparelho foi atrasado): expirado,
     *   para que uma nova busca corrija a data gravada.
     * - Idade de 24 h ou mais: expirado.
     *
     * @param now momento atual, vindo do `Clock` injetado (fixo nos testes).
     */
    fun isExpired(updatedAt: Instant?, now: Instant): Boolean {
        if (updatedAt == null) return true
        if (updatedAt.isAfter(now)) return true
        val age = Duration.between(updatedAt, now)
        return age >= VALIDITY
    }
}
