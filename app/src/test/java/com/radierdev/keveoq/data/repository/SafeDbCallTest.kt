package com.radierdev.keveoq.data.repository

import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class SafeDbCallTest {

    @Test
    fun `operacao bem sucedida deve retornar sucesso`() = runTest {
        assertEquals(AppResult.Success("ok"), safeDbCall { "ok" })
    }

    @Test
    fun `falha do banco deve retornar erro desconhecido`() = runTest {
        assertEquals(AppResult.Failure(AppError.Unknown), safeDbCall { throw IllegalStateException("disco cheio") })
    }

    @Test
    fun `cancelamento deve ser relancado`() = runTest {
        try {
            safeDbCall { throw CancellationException("cancelado") }
            fail("A CancellationException deveria ter sido relançada")
        } catch (e: CancellationException) {
            assertEquals("cancelado", e.message)
        }
    }
}
