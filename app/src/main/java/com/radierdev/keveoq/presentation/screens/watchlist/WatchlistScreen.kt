package com.radierdev.keveoq.presentation.screens.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radierdev.keveoq.R
import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.domain.model.WatchStatus
import com.radierdev.keveoq.presentation.components.EmptyState
import com.radierdev.keveoq.presentation.components.ErrorState
import com.radierdev.keveoq.presentation.components.LoadingState
import com.radierdev.keveoq.presentation.components.MovieListItem
import com.radierdev.keveoq.presentation.components.RatingBar
import com.radierdev.keveoq.presentation.theme.Dimens
import com.radierdev.keveoq.presentation.theme.KeveOQTheme
import org.koin.compose.viewmodel.koinViewModel
import java.time.Instant

/** Liga o [WatchlistViewModel] à tela. */
@Composable
fun WatchlistRoute(
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onChooseMood: () -> Unit,
    viewModel: WatchlistViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { messageRes ->
            snackbarHostState.showSnackbar(context.getString(messageRes))
        }
    }

    WatchlistScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onFilterSelected = viewModel::onFilterSelected,
        onMovieClick = onMovieClick,
        onMarkAsWatched = viewModel::onMarkAsWatched,
        onRate = viewModel::onRate,
        onChooseMood = onChooseMood,
        onRetry = viewModel::onRetry,
    )
}

/** Lista "Quero assistir" com filtro por status, "Já assisti" e avaliação (RF09–RF12). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    uiState: WatchlistUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onFilterSelected: (WatchlistFilter) -> Unit,
    onMovieClick: (Int) -> Unit,
    onMarkAsWatched: (Int) -> Unit,
    onRate: (movieId: Int, rating: Int) -> Unit,
    onChooseMood: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.screen_watchlist),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            FilterRow(selected = uiState.filter, onFilterSelected = onFilterSelected)
            when (val content = uiState.content) {
                WatchlistContent.Loading -> LoadingState()
                WatchlistContent.Error -> ErrorState(
                    message = stringResource(R.string.error_watchlist),
                    onRetry = onRetry,
                )
                WatchlistContent.Empty -> EmptyState(
                    message = stringResource(emptyMessageFor(uiState.filter)),
                    actionLabel = stringResource(R.string.action_choose_mood),
                    onAction = onChooseMood,
                )
                is WatchlistContent.Success -> SavedMovieList(
                    movies = content.movies,
                    onMovieClick = onMovieClick,
                    onMarkAsWatched = onMarkAsWatched,
                    onRate = onRate,
                )
            }
        }
    }
}

@Composable
private fun FilterRow(
    selected: WatchlistFilter,
    onFilterSelected: (WatchlistFilter) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = Dimens.ScreenHorizontalPadding)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.MediumSpacing),
    ) {
        WatchlistFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onFilterSelected(filter) },
                label = { Text(stringResource(labelFor(filter)), style = MaterialTheme.typography.labelMedium) },
            )
        }
    }
}

@Composable
private fun SavedMovieList(
    movies: List<SavedMovie>,
    onMovieClick: (Int) -> Unit,
    onMarkAsWatched: (Int) -> Unit,
    onRate: (movieId: Int, rating: Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = Dimens.ScreenHorizontalPadding,
            vertical = Dimens.MediumSpacing,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
    ) {
        items(movies, key = { it.movieId }) { movie ->
            MovieListItem(
                title = movie.title,
                releaseYear = movie.releaseYear,
                posterUrl = movie.posterUrl,
                onClick = { onMovieClick(movie.movieId) },
            ) {
                Text(
                    text = stringResource(statusLabelFor(movie.status)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                when (movie.status) {
                    WatchStatus.WANT_TO_WATCH -> OutlinedButton(onClick = { onMarkAsWatched(movie.movieId) }) {
                        Text(stringResource(R.string.action_mark_as_watched))
                    }
                    WatchStatus.WATCHED -> RatingBar(
                        rating = movie.rating,
                        onRate = { rating -> onRate(movie.movieId, rating) },
                    )
                }
            }
        }
    }
}

private fun labelFor(filter: WatchlistFilter): Int = when (filter) {
    WatchlistFilter.ALL -> R.string.filter_all
    WatchlistFilter.WANT_TO_WATCH -> R.string.filter_want_to_watch
    WatchlistFilter.WATCHED -> R.string.filter_watched
}

private fun statusLabelFor(status: WatchStatus): Int = when (status) {
    WatchStatus.WANT_TO_WATCH -> R.string.status_want_to_watch
    WatchStatus.WATCHED -> R.string.status_watched
}

private fun emptyMessageFor(filter: WatchlistFilter): Int = when (filter) {
    WatchlistFilter.ALL -> R.string.watchlist_empty
    WatchlistFilter.WANT_TO_WATCH -> R.string.watchlist_empty_want_to_watch
    WatchlistFilter.WATCHED -> R.string.watchlist_empty_watched
}

private val previewMovies = listOf(
    SavedMovie(1, "Divertida Mente 2", null, 2024, WatchStatus.WANT_TO_WATCH, null, Instant.EPOCH),
    SavedMovie(2, "Shrek", null, 2001, WatchStatus.WATCHED, 4, Instant.EPOCH),
)

@PreviewLightDark
@Composable
private fun WatchlistSuccessPreview() {
    KeveOQTheme {
        WatchlistScreen(
            uiState = WatchlistUiState(content = WatchlistContent.Success(previewMovies)),
            snackbarHostState = SnackbarHostState(),
            onBack = {}, onFilterSelected = {}, onMovieClick = {}, onMarkAsWatched = {},
            onRate = { _, _ -> }, onChooseMood = {}, onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun WatchlistEmptyPreview() {
    KeveOQTheme {
        WatchlistScreen(
            uiState = WatchlistUiState(content = WatchlistContent.Empty),
            snackbarHostState = SnackbarHostState(),
            onBack = {}, onFilterSelected = {}, onMovieClick = {}, onMarkAsWatched = {},
            onRate = { _, _ -> }, onChooseMood = {}, onRetry = {},
        )
    }
}
