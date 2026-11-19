package com.radierdev.keveoq.data.remote

import com.radierdev.keveoq.data.remote.dto.DiscoverResponseDto
import com.radierdev.keveoq.data.remote.dto.MovieDetailsDto
import com.radierdev.keveoq.data.remote.dto.VideosResponseDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Confere que os DTOs leem respostas no formato real do TMDB, com campos
 * que o app não usa e valores nulos. Usa a mesma configuração de Json do dataModule.
 */
class TmdbJsonParsingTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Test
    fun `deve ler o discover ignorando campos desconhecidos e aceitando poster nulo`() {
        val body = """
            {
              "page": 1,
              "total_pages": 500,
              "results": [
                {"id": 1, "title": "Com pôster", "poster_path": "/a.jpg", "release_date": "2020-01-01",
                 "vote_average": 7.1, "vote_count": 300, "adult": false, "genre_ids": [35]},
                {"id": 2, "title": "Sem pôster", "poster_path": null, "release_date": "",
                 "vote_average": 6.0, "vote_count": 150, "popularity": 12.3}
              ]
            }
        """.trimIndent()

        val response = json.decodeFromString<DiscoverResponseDto>(body)

        assertEquals(2, response.results.size)
        assertEquals("/a.jpg", response.results[0].posterPath)
        assertNull(response.results[1].posterPath)
    }

    @Test
    fun `deve ler detalhes com generos e duracao nula`() {
        val body = """
            {"id": 10, "title": "Filme", "overview": "Sinopse", "runtime": null,
             "genres": [{"id": 18, "name": "Drama"}], "vote_average": 7.5, "budget": 1000}
        """.trimIndent()

        val details = json.decodeFromString<MovieDetailsDto>(body)

        assertEquals(listOf("Drama"), details.genres.map { it.name })
        assertNull(details.runtime)
    }

    @Test
    fun `deve ler a lista de videos`() {
        val body = """
            {"id": 10, "results": [
              {"iso_639_1": "pt", "key": "abc", "site": "YouTube", "type": "Trailer", "official": true,
               "name": "Trailer oficial", "size": 1080}
            ]}
        """.trimIndent()

        val videos = json.decodeFromString<VideosResponseDto>(body)

        assertEquals("abc", videos.results.single().key)
        assertEquals(true, videos.results.single().official)
    }
}
