package com.radierdev.keveoq.presentation.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.Instant

class UpdatedAtFormatterTest {

    private val now = Instant.parse("2026-11-19T12:00:00Z")

    private fun formatAgo(duration: Duration) = UpdatedAtFormatter.format(now.minus(duration), now)

    @Test
    fun `menos de 1 minuto deve ser atualizado agora`() {
        assertEquals(UpdatedAtText.JustNow, formatAgo(Duration.ofSeconds(59)))
    }

    @Test
    fun `exatamente 1 minuto deve ser 1 minuto`() {
        assertEquals(UpdatedAtText.MinutesAgo(1), formatAgo(Duration.ofMinutes(1)))
    }

    @Test
    fun `59 minutos deve ser 59 minutos`() {
        assertEquals(UpdatedAtText.MinutesAgo(59), formatAgo(Duration.ofMinutes(59)))
    }

    @Test
    fun `atualizado ha 3 horas deve informar 3 horas`() {
        assertEquals(UpdatedAtText.HoursAgo(3), formatAgo(Duration.ofHours(3).plusMinutes(40)))
    }

    @Test
    fun `23 horas deve ser 23 horas`() {
        assertEquals(UpdatedAtText.HoursAgo(23), formatAgo(Duration.ofHours(23).plusMinutes(59)))
    }

    @Test
    fun `24 horas deve ser 1 dia`() {
        assertEquals(UpdatedAtText.DaysAgo(1), formatAgo(Duration.ofHours(24)))
    }

    @Test
    fun `3 dias deve ser 3 dias`() {
        assertEquals(UpdatedAtText.DaysAgo(3), formatAgo(Duration.ofDays(3).plusHours(5)))
    }

    @Test
    fun `data no futuro deve ser atualizado agora`() {
        assertEquals(UpdatedAtText.JustNow, UpdatedAtFormatter.format(now.plusSeconds(300), now))
    }
}
