package com.radierdev.keveoq.domain.rule

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

class CachePolicyTest {

    // Relógio fixo: "agora" é sempre o mesmo instante, então os testes são exatos.
    private val clock = Clock.fixed(Instant.parse("2026-11-19T12:00:00Z"), ZoneOffset.UTC)
    private val now = clock.instant()

    private fun updatedAgo(duration: Duration): Instant = now.minus(duration)

    @Test
    fun `cache sem data de atualizacao deve estar expirado`() {
        assertTrue(CachePolicy.isExpired(updatedAt = null, now = now))
    }

    @Test
    fun `cache atualizado agora deve estar valido`() {
        assertFalse(CachePolicy.isExpired(updatedAt = now, now = now))
    }

    @Test
    fun `cache atualizado ha 23 horas e 59 minutos deve estar valido`() {
        val updatedAt = updatedAgo(Duration.ofHours(23).plusMinutes(59))

        assertFalse(CachePolicy.isExpired(updatedAt, now))
    }

    @Test
    fun `cache atualizado ha exatamente 24 horas deve estar expirado`() {
        assertTrue(CachePolicy.isExpired(updatedAgo(Duration.ofHours(24)), now))
    }

    @Test
    fun `cache atualizado ha 25 horas deve estar expirado`() {
        assertTrue(CachePolicy.isExpired(updatedAgo(Duration.ofHours(25)), now))
    }

    @Test
    fun `cache com data de atualizacao no futuro deve estar expirado`() {
        val updatedAt = now.plus(Duration.ofHours(2))

        assertTrue(CachePolicy.isExpired(updatedAt, now))
    }
}
