package com.radierdev.keveoq.data.local

import com.radierdev.keveoq.data.local.entity.StoredProvider
import com.radierdev.keveoq.data.local.entity.StoredWatchProviders
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
    fun `onde assistir deve ir e voltar sem perda`() {
        val providers = StoredWatchProviders(
            subscription = listOf(StoredProvider(8, "Netflix", "https://image.tmdb.org/t/p/w92/n.jpg")),
            rent = listOf(StoredProvider(2, "Apple TV")),
            link = "https://www.themoviedb.org/movie/1/watch?locale=BR",
        )

        assertEquals(providers, converters.toWatchProviders(converters.fromWatchProviders(providers)))
    }

    @Test
    fun `valor padrao da migracao deve ser lido como nenhum servico`() {
        // A migração 1 → 2 cria a coluna com DEFAULT '{}'.
        assertEquals(StoredWatchProviders(), converters.toWatchProviders("{}"))
    }

    @Test
    fun `generos com acentos e aspas devem ir e voltar sem perda`() {
        val genres = listOf("Ficção científica", "Ação", "Filme \"cult\"")

        assertEquals(genres, converters.toStringList(converters.fromStringList(genres)))
    }
}
