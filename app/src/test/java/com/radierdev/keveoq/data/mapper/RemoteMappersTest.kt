package com.radierdev.keveoq.data.mapper

import com.radierdev.keveoq.data.remote.dto.GenreDto
import com.radierdev.keveoq.data.remote.dto.MovieDetailsDto
import com.radierdev.keveoq.data.remote.dto.MovieDto
import com.radierdev.keveoq.data.remote.dto.VideoDto
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.Video
import com.radierdev.keveoq.domain.rule.MoodCriteriaMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class RemoteMappersTest {

    // Pôster

    @Test
    fun `deve montar a url completa do poster`() {
        assertEquals("https://image.tmdb.org/t/p/w500/abc.jpg", posterUrlOf("/abc.jpg"))
    }

    @Test
    fun `filme sem poster deve ter url nula`() {
        assertNull(posterUrlOf(null))
        assertNull(posterUrlOf(""))
    }

    // Ano

    @Test
    fun `deve extrair o ano da data de lancamento`() {
        assertEquals(1999, releaseYearOf("1999-05-21"))
    }

    @Test
    fun `data vazia ou nula deve resultar em ano nulo`() {
        assertNull(releaseYearOf(""))
        assertNull(releaseYearOf(null))
    }

    // Filme, detalhes e vídeo

    @Test
    fun `deve converter filme do discover para o dominio`() {
        val dto = MovieDto(
            id = 603,
            title = "Matrix",
            posterPath = "/matrix.jpg",
            releaseDate = "1999-03-31",
            voteAverage = 8.2,
            voteCount = 25000,
        )

        val movie = dto.toDomain()

        assertEquals(603, movie.id)
        assertEquals("Matrix", movie.title)
        assertEquals("https://image.tmdb.org/t/p/w500/matrix.jpg", movie.posterUrl)
        assertEquals(1999, movie.releaseYear)
        assertEquals(8.2, movie.voteAverage, 0.0)
        assertEquals(25000, movie.voteCount)
    }

    @Test
    fun `deve converter detalhes com nomes dos generos e sem trailer`() {
        val updatedAt = Instant.parse("2026-11-19T12:00:00Z")
        val dto = MovieDetailsDto(
            id = 603,
            title = "Matrix",
            overview = "Um hacker descobre a verdade.",
            posterPath = "/matrix.jpg",
            releaseDate = "1999-03-31",
            voteAverage = 8.2,
            runtime = 136,
            genres = listOf(GenreDto(28, "Ação"), GenreDto(878, "Ficção científica")),
        )

        val details = dto.toDomain(updatedAt)

        assertEquals("Um hacker descobre a verdade.", details.overview)
        assertEquals(136, details.runtimeMinutes)
        assertEquals(listOf("Ação", "Ficção científica"), details.genres)
        assertNull(details.trailerKey)
        assertEquals(updatedAt, details.updatedAt)
    }

    @Test
    fun `detalhes sem sinopse e com duracao zero devem ter sinopse vazia e duracao nula`() {
        val dto = MovieDetailsDto(id = 1, title = "Sem dados", overview = null, runtime = 0)

        val details = dto.toDomain(Instant.EPOCH)

        assertEquals("", details.overview)
        assertNull(details.runtimeMinutes)
    }

    @Test
    fun `deve converter video para o dominio`() {
        val dto = VideoDto(key = "abc123", site = "YouTube", type = "Trailer", official = true)

        assertEquals(Video("abc123", "YouTube", "Trailer", true), dto.toDomain())
    }

    // Parâmetros do discover

    @Test
    fun `generos aceitos devem ser unidos com barra vertical`() {
        val query = MoodCriteriaMapper.map(Mood.HAPPY).toDiscoverQuery()

        assertEquals(setOf("35", "16", "10751"), query.withGenres.split("|").toSet())
        assertEquals(2, query.withGenres.count { it == '|' })
    }

    @Test
    fun `generos excluidos devem ser unidos com virgula`() {
        val query = MoodCriteriaMapper.map(Mood.ANXIOUS).toDiscoverQuery()

        assertEquals(setOf("27", "53"), query.withoutGenres?.split(",")?.toSet())
    }

    @Test
    fun `sem exclusoes o parametro deve ser nulo`() {
        assertNull(MoodCriteriaMapper.map(Mood.SAD).toDiscoverQuery().withoutGenres)
    }

    @Test
    fun `humor nostalgico deve enviar a data limite no formato iso`() {
        assertEquals("1999-12-31", MoodCriteriaMapper.map(Mood.NOSTALGIC).toDiscoverQuery().releaseDateLte)
    }

    @Test
    fun `humor sem data limite deve enviar data nula`() {
        assertNull(MoodCriteriaMapper.map(Mood.HAPPY).toDiscoverQuery().releaseDateLte)
    }

    @Test
    fun `deve enviar o minimo de 100 votos`() {
        assertEquals(100, MoodCriteriaMapper.map(Mood.BRAVE).toDiscoverQuery().minVoteCount)
    }
}
