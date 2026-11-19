package com.radierdev.keveoq.presentation.screens.recommendations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.Movie
import com.radierdev.keveoq.presentation.components.EmptyState
import com.radierdev.keveoq.presentation.components.ErrorState
import com.radierdev.keveoq.presentation.components.LoadingState
import com.radierdev.keveoq.presentation.components.MovieListItem
import com.radierdev.keveoq.presentation.screens.mood.labelRes
import com.radierdev.keveoq.presentation.theme.Dimens
import com.radierdev.keveoq.presentation.theme.KeveOQTheme
import com.radierdev.keveoq.presentation.util.UpdatedAtText
import com.radierdev.keveoq.presentation.util.asString
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Liga o [RecommendationsViewModel] à tela. O humor vem da rota de navegação
 * e é entregue ao ViewModel pelo Koin (`parametersOf`).
 */
@Composable
fun RecommendationsRoute(
    mood: Mood,
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    viewModel: RecommendationsViewModel = koinViewModel(key = mood.name) { parametersOf(mood) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Eventos únicos (Snackbar): coletados uma vez, enquanto a tela existir.
    LaunchedEffect(viewModel) {
        viewModel.messages.collect { messageRes ->
            snackbarHostState.showSnackbar(context.getString(messageRes))
        }
    }

    RecommendationsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onMovieClick = onMovieClick,
        onRefresh = viewModel::onRefresh,
        onRetry = viewModel::onRetry,
    )
}

/** Lista de recomendações do humor, com pull-to-refresh (RF03–RF05, RF14, RF15). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(
    uiState: RecommendationsUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onRefresh: () -> Unit,
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
                        text = stringResource(uiState.mood.labelRes),
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
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            when (val content = uiState.content) {
                RecommendationsContent.Loading -> LoadingState()
                is RecommendationsContent.Error -> ErrorState(
                    message = stringResource(content.messageRes),
                    onRetry = onRetry,
                )
                // Rolável para o gesto de puxar funcionar também no estado vazio.
                RecommendationsContent.Empty -> Box(Modifier.verticalScroll(rememberScrollState())) {
                    EmptyState(message = stringResource(R.string.recommendations_empty))
                }
                is RecommendationsContent.Success -> MovieList(
                    movies = content.movies,
                    updatedAt = content.updatedAt,
                    onMovieClick = onMovieClick,
                )
            }
        }
    }
}

@Composable
private fun MovieList(
    movies: List<Movie>,
    updatedAt: UpdatedAtText?,
    onMovieClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = Dimens.ScreenHorizontalPadding,
            vertical = Dimens.MediumSpacing,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
    ) {
        if (updatedAt != null) {
            item(key = "updated_at") {
                Text(
                    text = updatedAt.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(movies, key = { it.id }) { movie ->
            MovieListItem(
                title = movie.title,
                releaseYear = movie.releaseYear,
                posterUrl = movie.posterUrl,
                voteAverage = movie.voteAverage,
                onClick = { onMovieClick(movie.id) },
            )
        }
    }
}

private val previewMovies = listOf(
    Movie(1, "Divertida Mente 2", null, 2024, 7.6, 5000),
    Movie(2, "Shrek", null, 2001, 7.7, 17000),
    Movie(3, "Um filme com um título bem comprido para testar a quebra", null, 1999, 6.4, 300),
)

@PreviewLightDark
@Composable
private fun RecommendationsSuccessPreview() {
    KeveOQTheme {
        RecommendationsScreen(
            uiState = RecommendationsUiState(
                mood = Mood.HAPPY,
                content = RecommendationsContent.Success(previewMovies, UpdatedAtText.HoursAgo(3)),
            ),
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onMovieClick = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun RecommendationsErrorPreview() {
    KeveOQTheme {
        RecommendationsScreen(
            uiState = RecommendationsUiState(
                mood = Mood.SAD,
                content = RecommendationsContent.Error(R.string.error_network),
            ),
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onMovieClick = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun RecommendationsEmptyPreview() {
    KeveOQTheme {
        RecommendationsScreen(
            uiState = RecommendationsUiState(mood = Mood.BRAVE, content = RecommendationsContent.Empty),
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onMovieClick = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}
