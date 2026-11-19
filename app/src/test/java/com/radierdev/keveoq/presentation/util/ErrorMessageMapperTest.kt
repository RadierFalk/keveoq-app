package com.radierdev.keveoq.presentation.util

import com.radierdev.keveoq.R
import com.radierdev.keveoq.core.result.AppError
import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorMessageMapperTest {

    @Test
    fun `sem conexao deve pedir para verificar a conexao`() {
        assertEquals(R.string.error_network, ErrorMessageMapper.messageFor(AppError.Network))
    }

    @Test
    fun `erro 401 deve informar que nao foi possivel acessar o catalogo`() {
        assertEquals(R.string.error_unauthorized, ErrorMessageMapper.messageFor(AppError.Http(401)))
    }

    @Test
    fun `erros 500 e 503 devem informar que o catalogo esta fora do ar`() {
        assertEquals(R.string.error_server, ErrorMessageMapper.messageFor(AppError.Http(500)))
        assertEquals(R.string.error_server, ErrorMessageMapper.messageFor(AppError.Http(503)))
    }

    @Test
    fun `outros erros http devem usar a mensagem generica`() {
        assertEquals(R.string.error_generic, ErrorMessageMapper.messageFor(AppError.Http(404)))
        assertEquals(R.string.error_generic, ErrorMessageMapper.messageFor(AppError.Http(429)))
    }

    @Test
    fun `erro de leitura e erro desconhecido devem usar a mensagem generica`() {
        assertEquals(R.string.error_generic, ErrorMessageMapper.messageFor(AppError.Serialization))
        assertEquals(R.string.error_generic, ErrorMessageMapper.messageFor(AppError.Unknown))
    }
}
