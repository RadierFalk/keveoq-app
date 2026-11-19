package com.radierdev.keveoq.domain.rule

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TheatricalRuleTest {

    private val premiere = LocalDate.of(2026, 11, 1)

    @Test
    fun `no dia da estreia o filme deve estar em cartaz`() {
        assertTrue(TheatricalRule.isInTheaters(premiere, today = premiere))
    }

    @Test
    fun `10 dias apos a estreia o filme deve estar em cartaz`() {
        assertTrue(TheatricalRule.isInTheaters(premiere, today = premiere.plusDays(10)))
    }

    @Test
    fun `42 dias apos a estreia o filme ainda deve estar em cartaz`() {
        assertTrue(TheatricalRule.isInTheaters(premiere, today = premiere.plusDays(42)))
    }

    @Test
    fun `43 dias apos a estreia o filme nao deve estar em cartaz`() {
        assertFalse(TheatricalRule.isInTheaters(premiere, today = premiere.plusDays(43)))
    }

    @Test
    fun `60 dias apos a estreia o filme nao deve estar em cartaz`() {
        assertFalse(TheatricalRule.isInTheaters(premiere, today = premiere.plusDays(60)))
    }

    @Test
    fun `antes da estreia o filme nao deve estar em cartaz`() {
        assertFalse(TheatricalRule.isInTheaters(premiere, today = premiere.minusDays(1)))
    }

    @Test
    fun `sem data de estreia no brasil o filme nao deve estar em cartaz`() {
        assertFalse(TheatricalRule.isInTheaters(releaseDate = null, today = premiere))
    }
}
