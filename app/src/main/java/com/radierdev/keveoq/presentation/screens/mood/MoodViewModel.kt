package com.radierdev.keveoq.presentation.screens.mood

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.usecase.GetMoodsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Estado da tela de humor. O conteúdo é fixo (não depende de rede nem de banco),
 * por isso só existe o estado de conteúdo (seção 9.2 do planejamento).
 */
@Immutable
data class MoodUiState(
    val moods: List<Mood>,
    val selectedMood: Mood? = null,
) {
    /** "Ver filmes" só fica habilitado depois de escolher um humor (RF02). */
    val canSeeMovies: Boolean get() = selectedMood != null
}

/**
 * Guarda o humor selecionado. Como o ViewModel sobrevive à rotação da tela,
 * a seleção não se perde ao girar o aparelho.
 */
class MoodViewModel(
    getMoods: GetMoodsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoodUiState(moods = getMoods()))
    val uiState: StateFlow<MoodUiState> = _uiState.asStateFlow()

    fun onMoodSelected(mood: Mood) {
        _uiState.update { it.copy(selectedMood = mood) }
    }
}
