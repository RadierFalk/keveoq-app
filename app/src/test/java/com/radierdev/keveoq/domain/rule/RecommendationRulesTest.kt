package com.radierdev.keveoq.domain.rule

import com.radierdev.keveoq.domain.model.Movie
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendationRulesTest {

    private fun movie(id: Int, voteCount: Int = 500, title: String = "Filme $id") = Movie(
        id = id,
        title = title,
        posterUrl = null,
        releaseYear = 2020,
        voteAverage = 7.0,
        voteCount = voteCount,
    )

    @Test
    fun `deve remover filmes com menos de 100 votos`() {
        val movies = listOf(movie(id = 1, voteCount = 99), movie(id = 2, voteCount = 100), movie(id = 3, voteCount = 5000))

        val result = RecommendationRules.apply(movies, watchedIds = emptySet())

        assertEquals(listOf(2, 3), result.map { it.id })
    }

    @Test
    fun `deve remover filmes assistidos das recomendacoes`() {
        val movies = listOf(movie(1), movie(2), movie(3))

        val result = RecommendationRules.apply(movies, watchedIds = setOf(2))

        assertEquals(listOf(1, 3), result.map { it.id })
    }

    @Test
    fun `deve remover filmes duplicados mantendo a primeira ocorrencia`() {
        val movies = listOf(movie(1, title = "Primeira"), movie(2), movie(1, title = "Repetida"))

        val result = RecommendationRules.apply(movies, watchedIds = emptySet())

        assertEquals(listOf(1, 2), result.map { it.id })
        assertEquals("Primeira", result.first().title)
    }

    @Test
    fun `deve manter a ordem original dos filmes`() {
        val movies = listOf(movie(30), movie(10), movie(20), movie(40))

        val result = RecommendationRules.apply(movies, watchedIds = setOf(20))

        assertEquals(listOf(30, 10, 40), result.map { it.id })
    }

    @Test
    fun `deve retornar lista vazia quando todos os filmes forem filtrados`() {
        val movies = listOf(movie(1), movie(2, voteCount = 10))

        val result = RecommendationRules.apply(movies, watchedIds = setOf(1))

        assertTrue(result.isEmpty())
    }

    @Test
    fun `deve retornar lista vazia quando nao houver filmes`() {
        val result = RecommendationRules.apply(emptyList(), watchedIds = setOf(1))

        assertTrue(result.isEmpty())
    }
}
