package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.fake.FakeMovieRepository
import com.radierdev.keveoq.domain.fake.FakeNetworkMonitor
import com.radierdev.keveoq.domain.fake.TestData
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.Recommendations
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration

class RefreshRecommendationsUseCaseTest {

    private val movieRepository = FakeMovieRepository()
    private val networkMonitor = FakeNetworkMonitor(online = true)
    private val refresh =
        RefreshRecommendationsUseCase(movieRepository, networkMonitor, TestData.clock)

    private fun cacheUpdated(ago: Duration) {
        movieRepository.setRecommendations(
            Mood.HAPPY,
            Recommendations(listOf(TestData.movie(1)), TestData.NOW.minus(ago)),
        )
    }

    @Test
    fun `cache valido nao deve chamar a api`() = runTest {
        cacheUpdated(ago = Duration.ofHours(3))

        val result = refresh(Mood.HAPPY)

        assertEquals(AppResult.Success(Unit), result)
        assertEquals(0, movieRepository.refreshCalls)
    }

    @Test
    fun `cache expirado deve buscar na api`() = runTest {
        cacheUpdated(ago = Duration.ofHours(25))

        val result = refresh(Mood.HAPPY)

        assertEquals(AppResult.Success(Unit), result)
        assertEquals(1, movieRepository.refreshCalls)
    }

    @Test
    fun `cache vazio deve buscar na api`() = runTest {
        refresh(Mood.HAPPY)

        assertEquals(1, movieRepository.refreshCalls)
    }

    @Test
    fun `force deve buscar na api mesmo com cache valido`() = runTest {
        cacheUpdated(ago = Duration.ofMinutes(5))

        refresh(Mood.HAPPY, force = true)

        assertEquals(1, movieRepository.refreshCalls)
    }

    @Test
    fun `sem conexao deve retornar erro de rede sem chamar a api`() = runTest {
        cacheUpdated(ago = Duration.ofHours(25))
        networkMonitor.isOnline.value = false

        val result = refresh(Mood.HAPPY)

        assertEquals(AppResult.Failure(AppError.Network), result)
        assertEquals(0, movieRepository.refreshCalls)
    }

    @Test
    fun `sem conexao e com cache valido deve retornar sucesso sem chamar a api`() = runTest {
        cacheUpdated(ago = Duration.ofHours(1))
        networkMonitor.isOnline.value = false

        assertEquals(AppResult.Success(Unit), refresh(Mood.HAPPY))
        assertEquals(0, movieRepository.refreshCalls)
    }

    @Test
    fun `force sem conexao deve retornar erro de rede`() = runTest {
        cacheUpdated(ago = Duration.ofHours(1))
        networkMonitor.isOnline.value = false

        assertEquals(AppResult.Failure(AppError.Network), refresh(Mood.HAPPY, force = true))
        assertEquals(0, movieRepository.refreshCalls)
    }

    @Test
    fun `falha da api deve ser repassada`() = runTest {
        movieRepository.refreshResult = AppResult.Failure(AppError.Http(500))

        assertEquals(AppResult.Failure(AppError.Http(500)), refresh(Mood.HAPPY))
    }

    @Test
    fun `deve buscar com os criterios do humor`() = runTest {
        refresh(Mood.ANXIOUS)

        assertEquals(setOf(27, 53), movieRepository.lastCriteria?.withoutGenres)
    }
}
