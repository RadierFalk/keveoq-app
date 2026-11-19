package com.radierdev.keveoq.domain.usecase

import app.cash.turbine.test
import com.radierdev.keveoq.domain.fake.FakeMovieRepository
import com.radierdev.keveoq.domain.fake.FakeSavedMovieRepository
import com.radierdev.keveoq.domain.fake.TestData
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.Recommendations
import com.radierdev.keveoq.domain.model.WatchStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ObserveRecommendationsUseCaseTest {

    private val movieRepository = FakeMovieRepository()
    private val savedMovieRepository = FakeSavedMovieRepository()
    private val observeRecommendations =
        ObserveRecommendationsUseCase(movieRepository, savedMovieRepository)

    @Test
    fun `deve emitir as recomendacoes do cache com a data de atualizacao`() = runTest {
        val movies = listOf(TestData.movie(1), TestData.movie(2))
        movieRepository.setRecommendations(Mood.HAPPY, Recommendations(movies, TestData.NOW))

        observeRecommendations(Mood.HAPPY).test {
            val result = awaitItem()
            assertEquals(listOf(1, 2), result.movies.map { it.id })
            assertEquals(TestData.NOW, result.updatedAt)
        }
    }

    @Test
    fun `deve emitir lista vazia sem data quando nao houver cache`() = runTest {
        observeRecommendations(Mood.SAD).test {
            val result = awaitItem()
            assertTrue(result.movies.isEmpty())
            assertNull(result.updatedAt)
        }
    }

    @Test
    fun `deve aplicar as regras de recomendacao ao cache`() = runTest {
        val movies = listOf(TestData.movie(1), TestData.movie(2, voteCount = 50), TestData.movie(1))
        movieRepository.setRecommendations(Mood.HAPPY, Recommendations(movies, TestData.NOW))

        observeRecommendations(Mood.HAPPY).test {
            assertEquals(listOf(1), awaitItem().movies.map { it.id })
        }
    }

    @Test
    fun `deve remover das recomendacoes um filme marcado como assistido em tempo real`() = runTest {
        val movies = listOf(TestData.movie(1), TestData.movie(2), TestData.movie(3))
        movieRepository.setRecommendations(Mood.HAPPY, Recommendations(movies, TestData.NOW))
        savedMovieRepository.givenSaved(TestData.saved(2))

        observeRecommendations(Mood.HAPPY).test {
            assertEquals(listOf(1, 2, 3), awaitItem().movies.map { it.id })

            savedMovieRepository.updateStatus(2, WatchStatus.WATCHED)

            assertEquals(listOf(1, 3), awaitItem().movies.map { it.id })
        }
    }

    @Test
    fun `filme apenas salvo para assistir deve continuar nas recomendacoes`() = runTest {
        movieRepository.setRecommendations(
            Mood.HAPPY,
            Recommendations(listOf(TestData.movie(1)), TestData.NOW),
        )
        savedMovieRepository.givenSaved(TestData.saved(1, status = WatchStatus.WANT_TO_WATCH))

        observeRecommendations(Mood.HAPPY).test {
            assertEquals(listOf(1), awaitItem().movies.map { it.id })
        }
    }
}
