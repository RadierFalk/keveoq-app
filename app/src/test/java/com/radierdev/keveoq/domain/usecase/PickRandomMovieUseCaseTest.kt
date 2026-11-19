package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.fake.FakeMovieRepository
import com.radierdev.keveoq.domain.fake.FakeNetworkMonitor
import com.radierdev.keveoq.domain.fake.FakeSavedMovieRepository
import com.radierdev.keveoq.domain.fake.TestData
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.RandomMovieResult
import com.radierdev.keveoq.domain.model.Recommendations
import com.radierdev.keveoq.domain.model.WatchStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import kotlin.random.Random

class PickRandomMovieUseCaseTest {

    private val movieRepository = FakeMovieRepository()
    private val savedMovieRepository = FakeSavedMovieRepository()
    private val networkMonitor = FakeNetworkMonitor(online = true)

    private fun pickRandomMovie(seed: Int = 42) = PickRandomMovieUseCase(
        refreshRecommendations = RefreshRecommendationsUseCase(movieRepository, networkMonitor, TestData.clock),
        observeRecommendations = ObserveRecommendationsUseCase(movieRepository, savedMovieRepository),
        random = Random(seed),
    )

    private fun cache(mood: Mood, vararg ids: Int, ago: Duration = Duration.ofHours(1)) {
        movieRepository.setRecommendations(
            mood,
            Recommendations(ids.map { TestData.movie(it) }, TestData.NOW.minus(ago)),
        )
    }

    @Test
    fun `com conexao deve sortear um filme das recomendacoes da api`() = runTest {
        movieRepository.refreshResult = AppResult.Success(
            Recommendations(listOf(TestData.movie(1), TestData.movie(2), TestData.movie(3)), TestData.NOW),
        )

        val result = pickRandomMovie()()

        assertTrue(result is RandomMovieResult.Found)
        assertTrue((result as RandomMovieResult.Found).movieId in listOf(1, 2, 3))
    }

    @Test
    fun `filme assistido nunca deve ser sorteado`() = runTest {
        Mood.entries.forEach { cache(it, 1, 2) }
        savedMovieRepository.givenSaved(TestData.saved(1, status = WatchStatus.WATCHED))

        // Várias sementes: em nenhuma o filme 1 pode sair.
        repeat(20) { seed ->
            val result = pickRandomMovie(seed)() as RandomMovieResult.Found
            assertEquals(2, result.movieId)
        }
    }

    @Test
    fun `humor sem filmes disponiveis deve passar para outro humor`() = runTest {
        // Todos os humores só têm o filme 1, já assistido; apenas Romântico tem o filme 9.
        Mood.entries.forEach { cache(it, 1) }
        cache(Mood.ROMANTIC, 1, 9)
        savedMovieRepository.givenSaved(TestData.saved(1, status = WatchStatus.WATCHED))

        val result = pickRandomMovie()()

        assertEquals(RandomMovieResult.Found(movieId = 9, mood = Mood.ROMANTIC), result)
    }

    @Test
    fun `offline com cache em um unico humor deve sortear desse humor`() = runTest {
        networkMonitor.isOnline.value = false
        cache(Mood.NOSTALGIC, 5, 6, ago = Duration.ofDays(3)) // expirado, mas é o que existe

        val result = pickRandomMovie()() as RandomMovieResult.Found

        assertEquals(Mood.NOSTALGIC, result.mood)
        assertTrue(result.movieId in listOf(5, 6))
        assertEquals(0, movieRepository.refreshCalls)
    }

    @Test
    fun `offline sem nenhum cache nao deve encontrar filme`() = runTest {
        networkMonitor.isOnline.value = false

        assertEquals(RandomMovieResult.NoMovieAvailable, pickRandomMovie()())
        assertEquals(0, movieRepository.refreshCalls)
    }

    @Test
    fun `cache valido nao deve chamar a api`() = runTest {
        Mood.entries.forEach { cache(it, 1, 2, 3) }

        pickRandomMovie()()

        assertEquals(0, movieRepository.refreshCalls)
    }

    @Test
    fun `mesma semente deve produzir o mesmo sorteio`() = runTest {
        Mood.entries.forEach { cache(it, 1, 2, 3, 4, 5) }

        assertEquals(pickRandomMovie(seed = 7)(), pickRandomMovie(seed = 7)())
    }
}
