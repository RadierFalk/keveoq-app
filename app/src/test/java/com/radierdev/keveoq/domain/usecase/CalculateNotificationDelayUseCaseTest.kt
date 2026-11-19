package com.radierdev.keveoq.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class CalculateNotificationDelayUseCaseTest {

    private val saoPaulo = ZoneId.of("America/Sao_Paulo")

    /** Relógio fixo no horário local informado, no fuso de São Paulo. */
    private fun clockAt(localDateTime: LocalDateTime): Clock =
        Clock.fixed(localDateTime.atZone(saoPaulo).toInstant(), saoPaulo)

    @Test
    fun `agendamento as 14h deve esperar 5 horas`() {
        val calculateDelay = CalculateNotificationDelayUseCase(clockAt(LocalDateTime.of(2026, 11, 19, 14, 0)))

        assertEquals(Duration.ofHours(5), calculateDelay())
    }

    @Test
    fun `agendamento as 20h deve ser imediato`() {
        val calculateDelay = CalculateNotificationDelayUseCase(clockAt(LocalDateTime.of(2026, 11, 19, 20, 0)))

        assertEquals(Duration.ZERO, calculateDelay())
    }

    @Test
    fun `agendamento as 22h deve esperar ate as 19h do dia seguinte`() {
        val calculateDelay = CalculateNotificationDelayUseCase(clockAt(LocalDateTime.of(2026, 11, 19, 22, 0)))

        assertEquals(Duration.ofHours(21), calculateDelay())
    }

    @Test
    fun `deve usar o horario local do fuso do relogio e nao o UTC`() {
        // O instante é 17h em UTC, que são 14h em São Paulo (UTC-3).
        // O atraso deve ser até as 19h locais (5 h), e não até as 19h UTC (2 h).
        val clock = Clock.fixed(Instant.parse("2026-11-19T17:00:00Z"), saoPaulo)
        val calculateDelay = CalculateNotificationDelayUseCase(clock)

        assertEquals(Duration.ofHours(5), calculateDelay())
    }
}
