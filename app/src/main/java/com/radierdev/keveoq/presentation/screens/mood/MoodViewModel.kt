package com.radierdev.keveoq.presentation.screens.mood

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radierdev.keveoq.R
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.RandomMovieResult
import com.radierdev.keveoq.domain.usecase.GetMoodsUseCase
import com.radierdev.keveoq.domain.usecase.PickRandomMovieUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado da tela de humor. A grade de humores é fixa; o único estado que muda
 * com o tempo é o sorteio em andamento (RF26).
 */
@Immutable
data class MoodUiState(
    val moods: List<Mood>,
    val selectedMood: Mood? = null,
    val isPickingRandom: Boolean = false,
) {
    /** "Ver filmes" só fica habilitado depois de escolher um humor (RF02). */
    val canSeeMovies: Boolean get() = selectedMood != null
}

/** Eventos únicos da tela de humor. */
sealed interface MoodEvent {
    /** Abrir os detalhes do filme sorteado (RF25). */
    data class OpenMovie(val movieId: Int) : MoodEvent

    /** Mostrar uma mensagem no Snackbar (ex.: nenhum filme para sortear). */
    data class ShowMessage(@StringRes val messageRes: Int) : MoodEvent
}

/**
 * Guarda o humor selecionado (sobrevive à rotação, porque o ViewModel sobrevive)
 * e conduz o sorteio "Me surpreenda".
 */
class MoodViewModel(
    getMoods: GetMoodsUseCase,
    private val pickRandomMovie: PickRandomMovieUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoodUiState(moods = getMoods()))
    val uiState: StateFlow<MoodUiState> = _uiState.asStateFlow()

    private val _events = Channel<MoodEvent>(Channel.BUFFERED)
    val events: Flow<MoodEvent> = _events.receiveAsFlow()

    fun onMoodSelected(mood: Mood) {
        _uiState.update { it.copy(selectedMood = mood) }
    }

    /** "Me surpreenda": ignora novos toques enquanto um sorteio está em andamento (RF26). */
    fun onSurpriseMe() {
        if (_uiState.value.isPickingRandom) return
        _uiState.update { it.copy(isPickingRandom = true) }
        viewModelScope.launch {
            val event = when (val result = pickRandomMovie()) {
                is RandomMovieResult.Found -> MoodEvent.OpenMovie(result.movieId)
                RandomMovieResult.NoMovieAvailable -> MoodEvent.ShowMessage(R.string.message_no_random_movie)
            }
            _uiState.update { it.copy(isPickingRandom = false) }
            _events.send(event)
        }
    }
}
