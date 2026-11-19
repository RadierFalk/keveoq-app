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
    fun `deve ler detalhes com release_dates e watch providers do append_to_response`() {
        val body = """
            {"id": 10, "title": "Filme",
             "release_dates": {"results": [
               {"iso_3166_1": "BR", "release_dates": [
                 {"type": 3, "release_date": "2024-06-20T00:00:00.000Z", "certification": "L", "note": ""}
               ]}
             ]},
             "watch/providers": {"results": {
               "BR": {"link": "https://www.themoviedb.org/movie/10/watch?locale=BR",
                      "flatrate": [{"provider_id": 337, "provider_name": "Disney Plus",
                                    "logo_path": "/disney.jpg", "display_priority": 1}]},
               "US": {"rent": [{"provider_id": 2, "provider_name": "Apple TV", "logo_path": "/a.jpg"}]}
             }}}
        """.trimIndent()

        val details = json.decodeFromString<MovieDetailsDto>(body)

        assertEquals(3, details.releaseDates?.results?.single()?.releaseDates?.single()?.type)
        assertEquals("Disney Plus", details.watchProviders?.results?.get("BR")?.flatrate?.single()?.providerName)
        assertEquals(2, details.watchProviders?.results?.size)
    }

    @Test
    fun `detalhes sem append_to_response devem ser lidos sem onde assistir`() {
        val details = json.decodeFromString<MovieDetailsDto>("""{"id": 10, "title": "Filme"}""")

        assertNull(details.releaseDates)
        assertNull(details.watchProviders)
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
