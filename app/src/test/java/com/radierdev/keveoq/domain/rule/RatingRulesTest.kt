package com.radierdev.keveoq.domain.rule

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RatingRulesTest {

    @Test
    fun `nota 0 deve ser rejeitada`() {
        assertFalse(RatingRules.isValid(0))
    }

    @Test
    fun `nota 1 deve ser aceita`() {
        assertTrue(RatingRules.isValid(1))
    }

    @Test
    fun `nota 5 deve ser aceita`() {
        assertTrue(RatingRules.isValid(5))
    }

    @Test
    fun `nota 6 deve ser rejeitada`() {
        assertFalse(RatingRules.isValid(6))
    }

    @Test
    fun `nota negativa deve ser rejeitada`() {
        assertFalse(RatingRules.isValid(-1))
    }
}
