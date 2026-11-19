package com.radierdev.keveoq.data.repository

import com.radierdev.keveoq.core.dispatcher.AppDispatchers
import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.data.fake.FakeMovieDao
import com.radierdev.keveoq.data.fake.FakeTmdbApi
import com.radierdev.keveoq.data.remote.dto.GenreDto
import com.radierdev.keveoq.data.remote.dto.MovieDetailsDto
import com.radierdev.keveoq.data.remote.dto.MovieDto
import com.radierdev.keveoq.data.remote.dto.VideoDto
import com.radierdev.keveoq.data.remote.dto.VideosResponseDto
import com.radierdev.keveoq.domain.fake.FakeNetworkMonitor
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.rule.MoodCriteriaMapper
import com.radierdev.keveoq.domain.usecase.RefreshRecommendationsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

class MovieRepositoryImplTest {

    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)
    private val dispatchers = AppDispatchers(io = dispatcher, default = dispatcher, main = dispatcher)

    private val now = Instant.parse("2026-11-19T12:00:00Z")
    private var clock: Clock = Clock.fixed(now, ZoneOffset.UTC)

    private val api = FakeTmdbApi()
    private val dao = FakeMovieDao()

    private fun repository() = MovieRepositoryImpl(api, dao, clock, dispatchers)

    private fun movieDto(id: Int) = MovieDto(id = id, title = "Filme $id", posterPath = "/p$id.jpg", voteCount = 500)

    private val happyCriteria = MoodCriteriaMapper.map(Mood.HAPPY)

    private fun runRepoTest(block: suspend TestScope.() -> Unit) = testScope.runTest { block() }

    // Atualização das recomendações

    @Test
    fun `deve buscar as paginas 1 e 2 e gravar os filmes na ordem`() = runRepoTest {
        api.discoverPages[1] = listOf(movieDto(1), movieDto(2))
        api.discoverPages[2] = listOf(movieDto(3))

        val result = repository().refreshRecommendations(Mood.HAPPY, happyCriteria)

        assertEquals(AppResult.Success(Unit), result)
        assertEquals(listOf(1, 2), api.discoverCalls.map { it.page })
        val cached = repository().observeRecommendations(Mood.HAPPY).first()
        assertEquals(listOf(1, 2, 3), cached.movies.map { it.id })
        assertEquals(now, cached.updatedAt)
    }

    @Test
    fun `deve enviar os criterios do humor para a api`() = runRepoTest {
        repository().refreshRecommendations(Mood.ANXIOUS, MoodCriteriaMapper.map(Mood.ANXIOUS))

        val call = api.discoverCalls.first()
        assertEquals(setOf("35", "16", "10751"), call.withGenres.split("|").toSet())
        assertEquals(setOf("27", "53"), call.withoutGenres?.split(",")?.toSet())
        assertEquals(100, call.minVoteCount)
        assertEquals("popularity.desc", call.sortBy)
        assertEquals("pt-BR", call.language)
    }

    @Test
    fun `filme repetido entre as paginas deve ser gravado uma vez na primeira posicao`() = runRepoTest {
        api.discoverPages[1] = listOf(movieDto(1), movieDto(2))
        api.discoverPages[2] = listOf(movieDto(2), movieDto(3))

        repository().refreshRecommendations(Mood.HAPPY, happyCriteria)

        assertEquals(listOf(1, 2, 3), repository().observeRecommendations(Mood.HAPPY).first().movies.map { it.id })
    }

    @Test
    fun `nova atualizacao deve substituir as recomendacoes do humor`() = runRepoTest {
        api.discoverPages[1] = listOf(movieDto(1), movieDto(2))
        repository().refreshRecommendations(Mood.HAPPY, happyCriteria)

        api.discoverPages[1] = listOf(movieDto(5))
        repository().refreshRecommendations(Mood.HAPPY, happyCriteria)

        assertEquals(listOf(5), repository().observeRecommendations(Mood.HAPPY).first().movies.map { it.id })
    }

    @Test
    fun `atualizar um humor nao deve apagar as recomendacoes de outro`() = runRepoTest {
        api.discoverPages[1] = listOf(movieDto(1))
        repository().refreshRecommendations(Mood.HAPPY, happyCriteria)

        api.discoverPages[1] = listOf(movieDto(1), movieDto(9))
        repository().refreshRecommendations(Mood.SAD, MoodCriteriaMapper.map(Mood.SAD))

        assertEquals(listOf(1), repository().observeRecommendations(Mood.HAPPY).first().movies.map { it.id })
        assertEquals(listOf(1, 9), repository().observeRecommendations(Mood.SAD).first().movies.map { it.id })
    }

    @Test
    fun `falha de rede com cache deve preservar o cache`() = runRepoTest {
        api.discoverPages[1] = listOf(movieDto(1))
        repository().refreshRecommendations(Mood.HAPPY, happyCriteria)

        api.discoverErrors[1] = IOException("sem rede")
        val result = repository().refreshRecommendations(Mood.HAPPY, happyCriteria)

        assertEquals(AppResult.Failure(AppError.Network), result)
        val cached = repository().observeRecommendations(Mood.HAPPY).first()
        assertEquals(listOf(1), cached.movies.map { it.id })
        assertEquals(now, cached.updatedAt)
    }

    @Test
    fun `falha sem cache deve retornar erro e manter o cache vazio`() = runRepoTest {
        api.discoverErrors[1] = IOException("sem rede")

        val result = repository().refreshRecommendations(Mood.HAPPY, happyCriteria)

        assertEquals(AppResult.Failure(AppError.Network), result)
        val cached = repository().observeRecommendations(Mood.HAPPY).first()
        assertTrue(cached.movies.isEmpty())
        assertNull(cached.updatedAt)
    }

    @Test
    fun `falha na pagina 2 nao deve gravar nada`() = runRepoTest {
        api.discoverPages[1] = listOf(movieDto(1))
        api.discoverErrors[2] = HttpException(Response.error<Any>(500, "".toResponseBody(null)))

        val result = repository().refreshRecommendations(Mood.HAPPY, happyCriteria)

        assertEquals(AppResult.Failure(AppError.Http(500)), result)
        assertEquals(0, dao.replaceCalls)
    }

    @Test
    fun `falha ao gravar no banco deve retornar erro sem lancar excecao`() = runRepoTest {
        api.discoverPages[1] = listOf(movieDto(1))
        dao.writeError = IllegalStateException("disco cheio")

        assertEquals(AppResult.Failure(AppError.Unknown), repository().refreshRecommendations(Mood.HAPPY, happyCriteria))
    }

    // Cache válido e expirado (caso de uso + repositório real com API e DAO falsos)

    @Test
    fun `cache valido nao deve chamar a api`() = runRepoTest {
        api.discoverPages[1] = listOf(movieDto(1))
        repository().refreshRecommendations(Mood.HAPPY, happyCriteria)
        api.discoverCalls.clear()

        clock = Clock.fixed(now.plus(Duration.ofHours(23)), ZoneOffset.UTC)
        val refresh = RefreshRecommendationsUseCase(repository(), FakeNetworkMonitor(online = true), clock)
        refresh(Mood.HAPPY)

        assertTrue(api.discoverCalls.isEmpty())
    }

    @Test
    fun `cache expirado deve chamar a api e atualizar a data`() = runRepoTest {
        api.discoverPages[1] = listOf(movieDto(1))
        repository().refreshRecommendations(Mood.HAPPY, happyCriteria)
        api.discoverCalls.clear()

        val later = now.plus(Duration.ofHours(25))
        clock = Clock.fixed(later, ZoneOffset.UTC)
        val refresh = RefreshRecommendationsUseCase(repository(), FakeNetworkMonitor(online = true), clock)
        refresh(Mood.HAPPY)

        assertEquals(listOf(1, 2), api.discoverCalls.map { it.page })
        assertEquals(later, repository().observeRecommendations(Mood.HAPPY).first().updatedAt)
    }

    // Detalhes e vídeos

    @Test
    fun `deve buscar detalhes com a data da busca e sem trailer`() = runRepoTest {
        api.detailsResponse = MovieDetailsDto(
            id = 10,
            title = "Filme",
            overview = "Sinopse",
            runtime = 100,
            genres = listOf(GenreDto(18, "Drama")),
        )

        val result = repository().fetchMovieDetails(10)

        val details = (result as AppResult.Success).data
        assertEquals("Filme", details.title)
        assertEquals(listOf("Drama"), details.genres)
        assertNull(details.trailerKey)
        assertEquals(now, details.updatedAt)
    }

    @Test
    fun `erro 404 nos detalhes deve virar erro http`() = runRepoTest {
        api.detailsError = HttpException(Response.error<Any>(404, "".toResponseBody(null)))

        assertEquals(AppResult.Failure(AppError.Http(404)), repository().fetchMovieDetails(10))
    }

    @Test
    fun `deve buscar os videos no idioma pedido`() = runRepoTest {
        api.videosByLanguage["en-US"] = VideosResponseDto(
            listOf(VideoDto(key = "abc", site = "YouTube", type = "Trailer", official = true)),
        )

        val result = repository().fetchVideos(10, "en-US")

        assertEquals("abc", (result as AppResult.Success).data.single().key)
    }

    @Test
    fun `detalhes gravados devem ficar disponiveis para observacao`() = runRepoTest {
        api.detailsResponse = MovieDetailsDto(id = 10, title = "Filme")
        val details = (repository().fetchMovieDetails(10) as AppResult.Success).data

        repository().saveMovieDetails(details.copy(trailerKey = "abc"))

        assertEquals("abc", repository().observeMovieDetails(10).first()?.trailerKey)
    }

    @Test
    fun `detalhes nunca buscados devem ser nulos`() = runRepoTest {
        assertNull(repository().observeMovieDetails(10).first())
    }
}
