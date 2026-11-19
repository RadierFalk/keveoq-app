package com.radierdev.keveoq.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `deve gravar a lista de generos como texto json`() {
        assertEquals("""["Drama","Romance"]""", converters.fromStringList(listOf("Drama", "Romance")))
    }

    @Test
    fun `deve ler a lista de generos do texto json`() {
        assertEquals(listOf("Drama", "Romance"), converters.toStringList("""["Drama","Romance"]"""))
    }

    @Test
    fun `lista vazia deve ir e voltar sem perda`() {
        assertEquals(emptyList<String>(), converters.toStringList(converters.fromStringList(emptyList())))
    }

    @Test
    fun `generos com acentos e aspas devem ir e voltar sem perda`() {
        val genres = listOf("Ficção científica", "Ação", "Filme \"cult\"")

        assertEquals(genres, converters.toStringList(converters.fromStringList(genres)))
    }
}
