package com.radierdev.keveoq.presentation.screens.mood

import app.cash.turbine.test
import com.radierdev.keveoq.R
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.fake.FakeMovieRepository
import com.radierdev.keveoq.domain.fake.FakeNetworkMonitor
import com.radierdev.keveoq.domain.fake.FakeSavedMovieRepository
import com.radierdev.keveoq.domain.fake.TestData
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.Recommendations
import com.radierdev.keveoq.domain.usecase.GetMoodsUseCase
import com.radierdev.keveoq.domain.usecase.ObserveRecommendationsUseCase
import com.radierdev.keveoq.domain.usecase.PickRandomMovieUseCase
import com.radierdev.keveoq.domain.usecase.RefreshRecommendationsUseCase
import com.radierdev.keveoq.presentation.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class) // advanceUntilIdle
class MoodViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val movieRepository = FakeMovieRepository()
    private val savedMovieRepository = FakeSavedMovieRepository()
    private val networkMonitor = FakeNetworkMonitor(online = true)

    private val viewModel = MoodViewModel(
        getMoods = GetMoodsUseCase(),
        pickRandomMovie = PickRandomMovieUseCase(
            refreshRecommendations = RefreshRecommendationsUseCase(movieRepository, networkMonitor, TestData.clock),
            observeRecommendations = ObserveRecommendationsUseCase(movieRepository, savedMovieRepository),
            random = Random(42),
        ),
    )

    @Test
    fun `deve exibir os 8 humores sem nenhum selecionado`() {
        val state = viewModel.uiState.value

        assertEquals(Mood.entries.toList(), state.moods)
        assertNull(state.selectedMood)
        assertFalse(state.isPickingRandom)
    }

    @Test
    fun `ver filmes deve ficar desabilitado ate escolher um humor`() {
        assertFalse(viewModel.uiState.value.canSeeMovies)

        viewModel.onMoodSelected(Mood.SAD)

        assertTrue(viewModel.uiState.value.canSeeMovies)
    }

    @Test
    fun `deve guardar o humor selecionado`() {
        viewModel.onMoodSelected(Mood.SAD)

        assertEquals(Mood.SAD, viewModel.uiState.value.selectedMood)
    }

    @Test
    fun `escolher outro humor deve trocar a selecao`() {
        viewModel.onMoodSelected(Mood.SAD)
        viewModel.onMoodSelected(Mood.BRAVE)

        assertEquals(Mood.BRAVE, viewModel.uiState.value.selectedMood)
    }

    @Test
    fun `cada humor deve ter nome e dica diferentes`() {
        assertEquals(8, Mood.entries.map { it.labelRes }.toSet().size)
        assertEquals(8, Mood.entries.map { it.hintRes }.toSet().size)
    }

    // Sorteio "Me surpreenda" (RF25, RF26)

    @Test
    fun `me surpreenda deve abrir os detalhes do filme sorteado`() = runTest {
        apiReturns(7)

        viewModel.events.test {
            viewModel.onSurpriseMe()
            assertEquals(MoodEvent.OpenMovie(7), awaitItem())
        }
    }

    @Test
    fun `sem filme para sortear deve mostrar mensagem e continuar na tela`() = runTest {
        networkMonitor.isOnline.value = false // offline e sem nenhum cache

        viewModel.events.test {
            viewModel.onSurpriseMe()
            assertEquals(MoodEvent.ShowMessage(R.string.message_no_random_movie), awaitItem())
        }
        assertFalse(viewModel.uiState.value.isPickingRandom)
    }

    @Test
    fun `indicador deve ficar ligado durante o sorteio e desligar ao terminar`() = runTest {
        apiReturns(7)

        viewModel.uiState.test {
            assertFalse(awaitItem().isPickingRandom)

            viewModel.onSurpriseMe()
            assertTrue(awaitItem().isPickingRandom)
            assertFalse(awaitItem().isPickingRandom)
        }
    }

    @Test
    fun `toques repetidos durante o sorteio devem ser ignorados`() = runTest {
        apiReturns(7)

        viewModel.events.test {
            viewModel.onSurpriseMe()
            viewModel.onSurpriseMe()
            viewModel.onSurpriseMe()
            advanceUntilIdle()

            assertEquals(MoodEvent.OpenMovie(7), awaitItem())
            expectNoEvents()
        }
        assertEquals(1, movieRepository.refreshCalls)
    }

    /** Define os filmes que a "API" devolve para qualquer humor. */
    private fun apiReturns(vararg ids: Int) {
        movieRepository.refreshResult = AppResult.Success(Recommendations(ids.map { TestData.movie(it) }, TestData.NOW))
    }
}
