package com.radierdev.keveoq.data.repository

import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class SafeApiCallTest {

    private fun httpException(code: Int) =
        HttpException(Response.error<Any>(code, "".toResponseBody(null)))

    @Test
    fun `chamada bem sucedida deve retornar sucesso com o valor`() = runTest {
        assertEquals(AppResult.Success(42), safeApiCall { 42 })
    }

    @Test
    fun `sem conexao deve retornar erro de rede`() = runTest {
        assertEquals(AppResult.Failure(AppError.Network), safeApiCall { throw IOException("sem rede") })
    }

    @Test
    fun `timeout deve retornar erro de rede`() = runTest {
        assertEquals(AppResult.Failure(AppError.Network), safeApiCall { throw SocketTimeoutException() })
    }

    @Test
    fun `resposta 401 deve retornar erro http 401`() = runTest {
        assertEquals(AppResult.Failure(AppError.Http(401)), safeApiCall { throw httpException(401) })
    }

    @Test
    fun `resposta 404 deve retornar erro http 404`() = runTest {
        assertEquals(AppResult.Failure(AppError.Http(404)), safeApiCall { throw httpException(404) })
    }

    @Test
    fun `resposta 500 deve retornar erro http 500`() = runTest {
        assertEquals(AppResult.Failure(AppError.Http(500)), safeApiCall { throw httpException(500) })
    }

    @Test
    fun `json invalido deve retornar erro de serializacao`() = runTest {
        assertEquals(
            AppResult.Failure(AppError.Serialization),
            safeApiCall { throw SerializationException("json invalido") },
        )
    }

    @Test
    fun `excecao inesperada deve retornar erro desconhecido`() = runTest {
        assertEquals(AppResult.Failure(AppError.Unknown), safeApiCall { throw IllegalStateException() })
    }

    @Test
    fun `cancelamento deve ser relancado e nao convertido em erro`() = runTest {
        try {
            safeApiCall { throw CancellationException("tela fechada") }
            fail("A CancellationException deveria ter sido relançada")
        } catch (e: CancellationException) {
            assertEquals("tela fechada", e.message)
        }
    }
}
