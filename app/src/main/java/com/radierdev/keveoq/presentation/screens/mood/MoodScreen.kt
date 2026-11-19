package com.radierdev.keveoq.presentation.screens.mood

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radierdev.keveoq.R
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.presentation.theme.Dimens
import com.radierdev.keveoq.presentation.theme.KeveOQTheme
import org.koin.compose.viewmodel.koinViewModel

/** Liga o [MoodViewModel] à tela e repassa as ações de navegação. */
@Composable
fun MoodRoute(
    onSeeMovies: (Mood) -> Unit,
    onOpenWatchlist: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenMovie: (Int) -> Unit,
    viewModel: MoodViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val currentOnOpenMovie by rememberUpdatedState(onOpenMovie)

    // Eventos únicos do sorteio: abrir o filme ou avisar que não há filme.
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is MoodEvent.OpenMovie -> currentOnOpenMovie(event.movieId)
                is MoodEvent.ShowMessage -> snackbarHostState.showSnackbar(context.getString(event.messageRes))
            }
        }
    }

    MoodScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onMoodSelected = viewModel::onMoodSelected,
        onSeeMovies = { uiState.selectedMood?.let(onSeeMovies) },
        onSurpriseMe = viewModel::onSurpriseMe,
        onOpenWatchlist = onOpenWatchlist,
        onOpenAbout = onOpenAbout,
    )
}

/**
 * Tela inicial (RF01, RF02, RF24, RF25): "Como você está hoje?", grade com os 8 humores,
 * o botão "Me surpreenda" (sorteio) e "Ver filmes", habilitado só após a seleção.
 * Sem estado próprio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodScreen(
    uiState: MoodUiState,
    snackbarHostState: SnackbarHostState,
    onMoodSelected: (Mood) -> Unit,
    onSeeMovies: () -> Unit,
    onSurpriseMe: () -> Unit,
    onOpenWatchlist: () -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.MediumSpacing),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Decorativo: o nome do app ao lado já é lido pelo leitor de tela.
                        Image(
                            painter = painterResource(R.drawable.ic_keveoq_logo),
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.LogoSize),
                        )
                        Text(stringResource(R.string.app_name))
                    }
                },
                actions = {
                    IconButton(onClick = onOpenWatchlist) {
                        Icon(
                            painter = painterResource(R.drawable.ic_bookmark),
                            contentDescription = stringResource(R.string.action_open_watchlist),
                        )
                    }
                    IconButton(onClick = onOpenAbout) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = stringResource(R.string.action_open_about),
                        )
                    }
                },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.ItemSpacing),
                verticalArrangement = Arrangement.spacedBy(Dimens.MediumSpacing),
            ) {
                SurpriseMeButton(isPicking = uiState.isPickingRandom, onClick = onSurpriseMe)
                Button(
                    onClick = onSeeMovies,
                    enabled = uiState.canSeeMovies,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.action_see_movies))
                }
            }
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(innerPadding)
                .selectableGroup(),
            contentPadding = PaddingValues(
                horizontal = Dimens.ScreenHorizontalPadding,
                vertical = Dimens.MediumSpacing,
            ),
            horizontalArrangement = Arrangement.spacedBy(Dimens.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.ContentPadding),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.screen_mood),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = Dimens.ContentPadding),
                )
            }
            items(uiState.moods, key = { it.name }) { mood ->
                MoodCard(
                    mood = mood,
                    selected = mood == uiState.selectedMood,
                    onClick = { onMoodSelected(mood) },
                )
            }
        }
    }
}

/**
 * Botão secundário "Me surpreenda" (`OutlinedButton`, identidade visual).
 * Durante o sorteio troca o ícone de dado por um indicador e o texto por
 * "Sorteando um filme…", e fica desabilitado para não aceitar novos toques (RF26).
 */
@Composable
private fun SurpriseMeButton(isPicking: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        enabled = !isPicking,
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (isPicking) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.IconSmall),
                strokeWidth = Dimens.ProgressStrokeWidth,
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_casino),
                contentDescription = null, // o texto do botão já descreve a ação
                modifier = Modifier.size(Dimens.IconSmall),
            )
        }
        Text(
            text = stringResource(if (isPicking) R.string.picking_random_movie else R.string.action_surprise_me),
            modifier = Modifier.padding(start = Dimens.MediumSpacing),
        )
    }
}

/**
 * Card de humor. Não selecionado: `surfaceContainerLowest` com borda `outlineVariant`.
 * Selecionado: `primaryContainer` com borda `primary` de 2 dp (identidade visual).
 * Funciona como opção de um grupo (Role.RadioButton) para leitores de tela.
 */
@Composable
private fun MoodCard(
    mood: Mood,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (selected) colors.primaryContainer else colors.surfaceContainerLowest,
        contentColor = if (selected) colors.onPrimaryContainer else colors.onSurface,
        border = if (selected) {
            BorderStroke(Dimens.SelectedBorderWidth, colors.primary)
        } else {
            BorderStroke(Dimens.BorderWidth, colors.outlineVariant)
        },
        modifier = modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
    ) {
        Column(
            modifier = Modifier.padding(Dimens.ItemSpacing),
            verticalArrangement = Arrangement.spacedBy(Dimens.SmallSpacing),
        ) {
            Text(text = stringResource(mood.labelRes), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(mood.hintRes),
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) colors.onPrimaryContainer else colors.onSurfaceVariant,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun MoodScreenPreview() {
    KeveOQTheme {
        MoodScreen(
            uiState = MoodUiState(moods = Mood.entries, selectedMood = Mood.SAD),
            snackbarHostState = SnackbarHostState(),
            onMoodSelected = {}, onSeeMovies = {}, onSurpriseMe = {}, onOpenWatchlist = {}, onOpenAbout = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun MoodScreenNoSelectionPreview() {
    KeveOQTheme {
        MoodScreen(
            uiState = MoodUiState(moods = Mood.entries),
            snackbarHostState = SnackbarHostState(),
            onMoodSelected = {}, onSeeMovies = {}, onSurpriseMe = {}, onOpenWatchlist = {}, onOpenAbout = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun MoodScreenPickingRandomPreview() {
    KeveOQTheme {
        MoodScreen(
            uiState = MoodUiState(moods = Mood.entries, isPickingRandom = true),
            snackbarHostState = SnackbarHostState(),
            onMoodSelected = {}, onSeeMovies = {}, onSurpriseMe = {}, onOpenWatchlist = {}, onOpenAbout = {},
        )
    }
}
