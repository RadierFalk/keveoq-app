package com.radierdev.keveoq.data.remote

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthInterceptorTest {

    @Test
    fun `deve acrescentar o token no cabecalho authorization`() {
        val sentRequest = slot<Request>()
        val chain = mockk<Interceptor.Chain> {
            every { request() } returns Request.Builder().url("https://api.themoviedb.org/3/movie/1").build()
            every { proceed(capture(sentRequest)) } returns mockk<Response>()
        }

        AuthInterceptor(token = "token-de-teste").intercept(chain)

        assertEquals("Bearer token-de-teste", sentRequest.captured.header("Authorization"))
    }

    @Test
    fun `token nao deve aparecer na url`() {
        val sentRequest = slot<Request>()
        val chain = mockk<Interceptor.Chain> {
            every { request() } returns Request.Builder().url("https://api.themoviedb.org/3/movie/1").build()
            every { proceed(capture(sentRequest)) } returns mockk<Response>()
        }

        AuthInterceptor(token = "token-de-teste").intercept(chain)

        assertEquals(false, sentRequest.captured.url.toString().contains("token-de-teste"))
    }
}
