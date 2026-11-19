package com.radierdev.keveoq.presentation.screens.mood

import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.usecase.GetMoodsUseCase
import com.radierdev.keveoq.presentation.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MoodViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val viewModel = MoodViewModel(GetMoodsUseCase())

    @Test
    fun `deve exibir os 8 humores sem nenhum selecionado`() {
        val state = viewModel.uiState.value

        assertEquals(Mood.entries.toList(), state.moods)
        assertNull(state.selectedMood)
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
}
