package com.radierdev.keveoq.data.mapper

import com.radierdev.keveoq.data.local.entity.MovieEntity
import com.radierdev.keveoq.data.local.entity.RecommendedMovieRow
import com.radierdev.keveoq.data.remote.dto.MovieDto
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.MovieDetails
import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.domain.model.WatchStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class LocalMappersTest {

    private val instant = Instant.parse("2026-11-19T12:00:00Z")

    private fun movieEntity(id: Int) = MovieEntity(
        id = id,
        title = "Filme $id",
        posterUrl = null,
        releaseYear = 2020,
        voteAverage = 7.0,
        voteCount = 200,
    )

    @Test
    fun `filme da api deve virar entidade com url do poster e ano`() {
        val dto = MovieDto(id = 1, title = "A", posterPath = "/a.jpg", releaseDate = "2001-09-01", voteCount = 150)

        val entity = dto.toEntity()

        assertEquals("https://image.tmdb.org/t/p/w500/a.jpg", entity.posterUrl)
        assertEquals(2001, entity.releaseYear)
        assertEquals(150, entity.voteCount)
    }

    @Test
    fun `deve criar uma recomendacao por filme com posicao e data`() {
        val entities = recommendationEntitiesOf(Mood.SAD, listOf(30, 10, 20), instant)

        assertEquals(listOf(30, 10, 20), entities.map { it.movieId })
        assertEquals(listOf(0, 1, 2), entities.map { it.position })
        assertTrue(entities.all { it.mood == Mood.SAD && it.updatedAt == instant.toEpochMilli() })
    }

    @Test
    fun `linhas do banco devem virar recomendacoes ordenadas pela posicao`() {
        val rows = listOf(
            RecommendedMovieRow(movieEntity(2), position = 1, updatedAt = instant.toEpochMilli()),
            RecommendedMovieRow(movieEntity(1), position = 0, updatedAt = instant.toEpochMilli()),
        )

        val recommendations = rows.toRecommendations()

        assertEquals(listOf(1, 2), recommendations.movies.map { it.id })
        assertEquals(instant, recommendations.updatedAt)
    }

    @Test
    fun `sem linhas no banco as recomendacoes devem estar vazias e sem data`() {
        val recommendations = emptyList<RecommendedMovieRow>().toRecommendations()

        assertTrue(recommendations.movies.isEmpty())
        assertNull(recommendations.updatedAt)
    }

    @Test
    fun `detalhes devem ir para a entidade e voltar sem perda`() {
        val details = MovieDetails(
            id = 10,
            title = "Filme",
            overview = "Sinopse",
            posterUrl = "https://image.tmdb.org/t/p/w500/p.jpg",
            voteAverage = 8.1,
            releaseYear = 1999,
            runtimeMinutes = 136,
            genres = listOf("Ação", "Ficção científica"),
            trailerKey = "abc123",
            updatedAt = instant,
        )

        assertEquals(details, details.toEntity().toDomain())
    }

    @Test
    fun `filme salvo deve ir para a entidade e voltar sem perda`() {
        val saved = SavedMovie(
            movieId = 10,
            title = "Filme",
            posterUrl = null,
            releaseYear = null,
            status = WatchStatus.WATCHED,
            rating = 4,
            savedAt = instant,
        )

        assertEquals(saved, saved.toEntity().toDomain())
    }
}
