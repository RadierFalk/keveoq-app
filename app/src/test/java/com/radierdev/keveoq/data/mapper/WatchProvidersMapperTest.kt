package com.radierdev.keveoq.data.mapper

import com.radierdev.keveoq.data.remote.dto.CountryReleaseDatesDto
import com.radierdev.keveoq.data.remote.dto.CountryWatchProvidersDto
import com.radierdev.keveoq.data.remote.dto.MovieDetailsDto
import com.radierdev.keveoq.data.remote.dto.ProviderDto
import com.radierdev.keveoq.data.remote.dto.ReleaseDateDto
import com.radierdev.keveoq.data.remote.dto.ReleaseDatesDto
import com.radierdev.keveoq.data.remote.dto.WatchProvidersResponseDto
import com.radierdev.keveoq.domain.model.WatchProviders
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class WatchProvidersMapperTest {

    private fun provider(id: Int, name: String, priority: Int = 1, logo: String? = "/logo$id.jpg") =
        ProviderDto(providerId = id, providerName = name, logoPath = logo, displayPriority = priority)

    private fun response(country: String, providers: CountryWatchProvidersDto) =
        WatchProvidersResponseDto(results = mapOf(country to providers))

    // Onde assistir (RN13)

    @Test
    fun `deve separar os servicos do brasil em assinatura aluguel e compra`() {
        val dto = response(
            "BR",
            CountryWatchProvidersDto(
                link = "https://www.themoviedb.org/movie/1/watch?locale=BR",
                flatrate = listOf(provider(8, "Netflix")),
                rent = listOf(provider(2, "Apple TV")),
            ),
        )

        val result = dto.toDomainForBrazil()

        assertEquals(listOf("Netflix"), result.subscription.map { it.name })
        assertEquals(listOf("Apple TV"), result.rent.map { it.name })
        assertTrue(result.buy.isEmpty())
        assertEquals("https://www.themoviedb.org/movie/1/watch?locale=BR", result.link)
    }

    @Test
    fun `deve ignorar servicos de outros paises`() {
        val dto = response("US", CountryWatchProvidersDto(flatrate = listOf(provider(15, "Hulu"))))

        assertEquals(WatchProviders.EMPTY, dto.toDomainForBrazil())
    }

    @Test
    fun `resposta sem dados de onde assistir deve ficar vazia`() {
        val nothing: WatchProvidersResponseDto? = null

        assertTrue(nothing.toDomainForBrazil().isEmpty)
    }

    @Test
    fun `deve ordenar pela prioridade da api e remover repetidos`() {
        val dto = response(
            "BR",
            CountryWatchProvidersDto(
                flatrate = listOf(
                    provider(119, "Prime Video", priority = 3),
                    provider(8, "Netflix", priority = 1),
                    provider(8, "Netflix", priority = 1),
                ),
            ),
        )

        assertEquals(listOf("Netflix", "Prime Video"), dto.toDomainForBrazil().subscription.map { it.name })
    }

    @Test
    fun `deve montar a url do logo e aceitar servico sem logo`() {
        val dto = response(
            "BR",
            CountryWatchProvidersDto(buy = listOf(provider(3, "Google Play"), provider(9, "Loja", logo = null))),
        )

        val buy = dto.toDomainForBrazil().buy
        assertEquals("https://image.tmdb.org/t/p/w92/logo3.jpg", buy[0].logoUrl)
        assertNull(buy[1].logoUrl)
    }

    // Estreia nos cinemas (RN14)

    private fun releases(country: String, vararg dates: Pair<Int, String>) = ReleaseDatesDto(
        results = listOf(
            CountryReleaseDatesDto(
                country = country,
                releaseDates = dates.map { (type, date) -> ReleaseDateDto(type = type, releaseDate = date) },
            ),
        ),
    )

    @Test
    fun `deve usar a estreia nos cinemas do brasil`() {
        val dto = releases("BR", 3 to "2024-06-20T00:00:00.000Z")

        assertEquals(LocalDate.of(2024, 6, 20), dto.theatricalReleaseInBrazil())
    }

    @Test
    fun `lancamento limitado tambem conta como cinema e vale a data mais antiga`() {
        val dto = releases("BR", 3 to "2024-06-20T00:00:00.000Z", 2 to "2024-06-13T00:00:00.000Z")

        assertEquals(LocalDate.of(2024, 6, 13), dto.theatricalReleaseInBrazil())
    }

    @Test
    fun `lancamento digital nao conta como cinema`() {
        assertNull(releases("BR", 4 to "2024-09-01T00:00:00.000Z").theatricalReleaseInBrazil())
    }

    @Test
    fun `estreia em outro pais nao conta`() {
        assertNull(releases("US", 3 to "2024-06-14T00:00:00.000Z").theatricalReleaseInBrazil())
    }

    @Test
    fun `data invalida deve ser ignorada`() {
        assertNull(releases("BR", 3 to "").theatricalReleaseInBrazil())
    }

    @Test
    fun `detalhes devem trazer onde assistir e a estreia no brasil`() {
        val dto = MovieDetailsDto(
            id = 1,
            title = "Filme",
            releaseDates = releases("BR", 3 to "2024-06-20T00:00:00.000Z"),
            watchProviders = response("BR", CountryWatchProvidersDto(flatrate = listOf(provider(8, "Netflix")))),
        )

        val details = dto.toDomain(Instant.EPOCH)

        assertEquals(LocalDate.of(2024, 6, 20), details.theatricalReleaseBr)
        assertEquals(listOf("Netflix"), details.watchProviders.subscription.map { it.name })
    }
}
