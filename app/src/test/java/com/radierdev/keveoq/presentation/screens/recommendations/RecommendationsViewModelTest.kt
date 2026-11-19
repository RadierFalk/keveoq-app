package com.radierdev.keveoq.presentation.screens.recommendations

import app.cash.turbine.test
import com.radierdev.keveoq.R
import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.fake.FakeMovieRepository
import com.radierdev.keveoq.domain.fake.FakeNetworkMonitor
import com.radierdev.keveoq.domain.fake.FakeSavedMovieRepository
import com.radierdev.keveoq.domain.fake.TestData
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.Recommendations
import com.radierdev.keveoq.domain.model.WatchStatus
import com.radierdev.keveoq.domain.usecase.ObserveRecommendationsUseCase
import com.radierdev.keveoq.domain.usecase.RefreshRecommendationsUseCase
import com.radierdev.keveoq.presentation.MainDispatcherRule
import com.radierdev.keveoq.presentation.util.UpdatedAtText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Duration

@OptIn(ExperimentalCoroutinesApi::class) // advanceUntilIdle
class RecommendationsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val movieRepository = FakeMovieRepository()
    private val savedMovieRepository = FakeSavedMovieRepository()
    private val networkMonitor = FakeNetworkMonitor(online = true)

    private fun createViewModel(mood: Mood = Mood.HAPPY) = RecommendationsViewModel(
        mood = mood,
        observeRecommendations = ObserveRecommendationsUseCase(movieRepository, savedMovieRepository),
        refreshRecommendations = RefreshRecommendationsUseCase(movieRepository, networkMonitor, TestData.clock),
        clock = TestData.clock,
    )

    private fun apiReturns(vararg ids: Int) {
        movieRepository.refreshResult = AppResult.Success(
            Recommendations(ids.map { TestData.movie(it) }, TestData.NOW),
        )
    }

    private fun givenCache(ago: Duration, vararg ids: Int) {
        movieRepository.setRecommendations(
            Mood.HAPPY,
            Recommendations(ids.map { TestData.movie(it) }, TestData.NOW.minus(ago)),
        )
    }

    @Test
    fun `sem cache deve ir de loading para success com os filmes da api`() = runTest {
        apiReturns(1, 2)
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(RecommendationsContent.Loading, awaitItem().content)

            val content = awaitItem().content as RecommendationsContent.Success
            assertEquals(listOf(1, 2), content.movies.map { it.id })
            assertEquals(UpdatedAtText.JustNow, content.updatedAt)
        }
    }

    @Test
    fun `sem cache e api sem filmes deve ir de loading para empty`() = runTest {
        movieRepository.refreshResult = AppResult.Success(Recommendations(emptyList(), null))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(RecommendationsContent.Loading, awaitItem().content)
            assertEquals(RecommendationsContent.Empty, awaitItem().content)
        }
    }

    @Test
    fun `sem cache e sem conexao deve ir de loading para error com tentar novamente`() = runTest {
        networkMonitor.isOnline.value = false
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(RecommendationsContent.Loading, awaitItem().content)
            assertEquals(RecommendationsContent.Error(R.string.error_network), awaitItem().content)
        }
    }

    @Test
    fun `tentar novamente apos erro deve carregar os filmes`() = runTest {
        networkMonitor.isOnline.value = false
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1) // Loading
            assertTrue(awaitItem().content is RecommendationsContent.Error)

            networkMonitor.isOnline.value = true
            apiReturns(7)
            viewModel.onRetry()

            assertEquals(RecommendationsContent.Loading, awaitItem().content)
            val content = awaitItem().content as RecommendationsContent.Success
            assertEquals(listOf(7), content.movies.map { it.id })
        }
    }

    @Test
    fun `cache valido deve mostrar os filmes sem chamar a api e informar ha quanto tempo`() = runTest {
        givenCache(Duration.ofHours(3), 1, 2)
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1) // valor inicial
            val content = awaitItem().content as RecommendationsContent.Success
            assertEquals(listOf(1, 2), content.movies.map { it.id })
            assertEquals(UpdatedAtText.HoursAgo(3), content.updatedAt)
        }
        assertEquals(0, movieRepository.refreshCalls)
    }

    @Test
    fun `falha com cache deve manter os filmes e mostrar snackbar`() = runTest {
        givenCache(Duration.ofHours(30), 1, 2)
        networkMonitor.isOnline.value = false
        val viewModel = createViewModel()

        viewModel.messages.test {
            assertEquals(R.string.error_network, awaitItem())
        }
        viewModel.uiState.test {
            skipItems(1) // valor inicial do StateFlow
            val content = awaitItem().content as RecommendationsContent.Success
            assertEquals(listOf(1, 2), content.movies.map { it.id })
        }
    }

    @Test
    fun `falha sem cache nao deve mostrar snackbar`() = runTest {
        networkMonitor.isOnline.value = false
        val viewModel = createViewModel()

        viewModel.messages.test {
            advanceUntilIdle()
            expectNoEvents()
        }
    }

    @Test
    fun `pull to refresh deve buscar mesmo com cache valido`() = runTest {
        givenCache(Duration.ofMinutes(10), 1)
        apiReturns(5, 6)
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            assertEquals(listOf(1), (awaitItem().content as RecommendationsContent.Success).movies.map { it.id })

            viewModel.onRefresh()

            assertTrue(awaitItem().isRefreshing)
            advanceUntilIdle()
            val state = expectMostRecentItem()
            assertFalse(state.isRefreshing)
            assertEquals(listOf(5, 6), (state.content as RecommendationsContent.Success).movies.map { it.id })
        }
        assertEquals(1, movieRepository.refreshCalls)
    }

    @Test
    fun `filme marcado como assistido deve sumir da lista`() = runTest {
        givenCache(Duration.ofHours(1), 1, 2)
        savedMovieRepository.givenSaved(TestData.saved(2))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            assertEquals(listOf(1, 2), (awaitItem().content as RecommendationsContent.Success).movies.map { it.id })

            savedMovieRepository.updateStatus(2, WatchStatus.WATCHED)

            assertEquals(listOf(1), (awaitItem().content as RecommendationsContent.Success).movies.map { it.id })
        }
    }

    @Test
    fun `cache com todos os filmes ja assistidos deve ficar empty`() = runTest {
        givenCache(Duration.ofHours(1), 1)
        savedMovieRepository.givenSaved(TestData.saved(1, status = WatchStatus.WATCHED))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            assertEquals(RecommendationsContent.Empty, awaitItem().content)
        }
    }
}
