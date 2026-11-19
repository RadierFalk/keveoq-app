package com.radierdev.keveoq.presentation.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radierdev.keveoq.R
import com.radierdev.keveoq.domain.model.MovieDetails
import com.radierdev.keveoq.presentation.components.ErrorState
import com.radierdev.keveoq.presentation.components.LoadingState
import com.radierdev.keveoq.presentation.components.PosterImage
import com.radierdev.keveoq.presentation.components.RatingBar
import com.radierdev.keveoq.presentation.components.TrailerPlayer
import com.radierdev.keveoq.presentation.theme.Dimens
import com.radierdev.keveoq.presentation.theme.KeveOQTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.Instant

/** Liga o [DetailsViewModel] à tela. O ID do filme vem da rota de navegação. */
@Composable
fun DetailsRoute(
    movieId: Int,
    onBack: () -> Unit,
    viewModel: DetailsViewModel = koinViewModel(key = "details_$movieId") { parametersOf(movieId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { messageRes ->
            snackbarHostState.showSnackbar(context.getString(messageRes))
        }
    }

    DetailsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onRetry = viewModel::onRetry,
        onAddToWatchlist = viewModel::onAddToWatchlist,
        onMarkAsWatched = viewModel::onMarkAsWatched,
        onRate = viewModel::onRate,
    )
}

/**
 * Detalhes do filme (RF06, RF08, RF10, RF11): pôster, título, ano, duração, nota,
 * gêneros, ação conforme o status e sinopse.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    uiState: DetailsUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onAddToWatchlist: () -> Unit,
    onMarkAsWatched: () -> Unit,
    onRate: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
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
        val contentModifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        when (uiState) {
            DetailsUiState.Loading -> LoadingState(contentModifier)
            is DetailsUiState.Error -> ErrorState(
                message = stringResource(uiState.messageRes),
                onRetry = onRetry,
                modifier = contentModifier,
            )
            is DetailsUiState.Success -> DetailsContent(
                details = uiState.details,
                action = uiState.action,
                trailerKey = uiState.trailerKey,
                onAddToWatchlist = onAddToWatchlist,
                onMarkAsWatched = onMarkAsWatched,
                onRate = onRate,
                modifier = contentModifier,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailsContent(
    details: MovieDetails,
    action: WatchlistAction,
    trailerKey: String?,
    onAddToWatchlist: () -> Unit,
    onMarkAsWatched: () -> Unit,
    onRate: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenHorizontalPadding)
            .padding(bottom = Dimens.SectionSpacing),
        verticalArrangement = Arrangement.spacedBy(Dimens.SectionSpacing),
    ) {
        PosterImage(
            posterUrl = details.posterUrl,
            contentDescription = details.title,
            contentScale = ContentScale.Fit, // pôster inteiro, sem cortes
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.PosterDetailsHeight),
        )

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.MediumSpacing)) {
            Text(
                text = details.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            MovieFacts(details)
            if (details.genres.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.MediumSpacing),
                    verticalArrangement = Arrangement.spacedBy(Dimens.MediumSpacing),
                ) {
                    details.genres.forEach { genre -> GenreChip(genre) }
                }
            }
        }

        WatchlistActionSection(
            action = action,
            onAddToWatchlist = onAddToWatchlist,
            onMarkAsWatched = onMarkAsWatched,
            onRate = onRate,
        )

        // Sem trailer válido ou sem conexão, a seção inteira não aparece (RF20, RF21).
        if (trailerKey != null) {
            TrailerSection(trailerKey)
        }

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.MediumSpacing)) {
            Text(
                text = stringResource(R.string.details_overview_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = details.overview.ifBlank { stringResource(R.string.details_overview_unavailable) },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

/**
 * Trailer no player incorporado. Se o player falhar, troca por uma mensagem e o botão
 * "Assistir no YouTube", que abre o link no app do YouTube ou no navegador (RF22).
 * O `LocalUriHandler` do Compose abre o link sem o ViewModel precisar de `Context`.
 */
@Composable
private fun TrailerSection(trailerKey: String) {
    var playerFailed by remember(trailerKey) { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Column(verticalArrangement = Arrangement.spacedBy(Dimens.MediumSpacing)) {
        Text(
            text = stringResource(R.string.details_trailer_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        if (playerFailed) {
            Text(
                text = stringResource(R.string.details_trailer_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(
                onClick = { uriHandler.openUri(youTubeUrl(trailerKey)) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.action_watch_on_youtube))
            }
        } else {
            TrailerPlayer(videoKey = trailerKey, onError = { playerFailed = true })
        }
    }
}

/** Link externo do trailer (CLAUDE.md). */
private fun youTubeUrl(key: String) = "https://www.youtube.com/watch?v=$key"

/** Ano, duração e nota, em texto secundário. */
@Composable
private fun MovieFacts(details: MovieDetails) {
    val voteDescription = stringResource(R.string.vote_average_description, details.voteAverage)
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        details.releaseYear?.let { FactText(it.toString()) }
        details.runtimeMinutes?.let { FactText(runtimeText(it)) }
        Row(
            modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = voteDescription },
            horizontalArrangement = Arrangement.spacedBy(Dimens.SmallSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(Dimens.IconSmall),
            )
            FactText(stringResource(R.string.vote_average, details.voteAverage))
        }
    }
}

@Composable
private fun FactText(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

/** "2 h 16 min" ou "45 min". */
@Composable
private fun runtimeText(minutes: Int): String {
    val hours = minutes / 60
    val rest = minutes % 60
    return if (hours > 0) {
        stringResource(R.string.runtime_hours_minutes, hours, rest)
    } else {
        stringResource(R.string.runtime_minutes, rest)
    }
}

/** Chip de gênero: `secondaryContainer` / `onSecondaryContainer`, forma `small`, texto `labelMedium`. */
@Composable
private fun GenreChip(genre: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = genre,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = Dimens.ContentPadding, vertical = Dimens.SmallSpacing),
        )
    }
}

/**
 * Ação conforme o status (RN07, RN08): "Quero assistir" (`Button`),
 * "Já assisti" (`OutlinedButton`) ou estrelas para avaliar o filme assistido.
 */
@Composable
private fun WatchlistActionSection(
    action: WatchlistAction,
    onAddToWatchlist: () -> Unit,
    onMarkAsWatched: () -> Unit,
    onRate: (Int) -> Unit,
) {
    when (action) {
        WatchlistAction.AddToWatchlist -> Button(onClick = onAddToWatchlist, modifier = Modifier.fillMaxWidth()) {
            Icon(painter = painterResource(R.drawable.ic_bookmark), contentDescription = null)
            Text(
                text = stringResource(R.string.action_add_to_watchlist),
                modifier = Modifier.padding(start = Dimens.MediumSpacing),
            )
        }
        WatchlistAction.MarkAsWatched -> OutlinedButton(onClick = onMarkAsWatched, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.action_mark_as_watched))
        }
        is WatchlistAction.Rate -> Column(verticalArrangement = Arrangement.spacedBy(Dimens.SmallSpacing)) {
            Text(
                text = stringResource(R.string.details_rate_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            RatingBar(rating = action.rating, onRate = onRate)
        }
    }
}

private val previewDetails = MovieDetails(
    id = 1,
    title = "Divertida Mente 2",
    overview = "Riley entra na adolescência e novas emoções chegam à sala de controle.",
    posterUrl = null,
    voteAverage = 7.6,
    releaseYear = 2024,
    runtimeMinutes = 96,
    genres = listOf("Animação", "Família", "Comédia", "Drama"),
    trailerKey = null,
    updatedAt = Instant.EPOCH,
)

@PreviewLightDark
@Composable
private fun DetailsNotSavedPreview() {
    KeveOQTheme {
        DetailsScreen(
            uiState = DetailsUiState.Success(previewDetails, WatchlistAction.AddToWatchlist),
            snackbarHostState = SnackbarHostState(),
            onBack = {}, onRetry = {}, onAddToWatchlist = {}, onMarkAsWatched = {}, onRate = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun DetailsWantToWatchPreview() {
    KeveOQTheme {
        DetailsScreen(
            uiState = DetailsUiState.Success(previewDetails, WatchlistAction.MarkAsWatched),
            snackbarHostState = SnackbarHostState(),
            onBack = {}, onRetry = {}, onAddToWatchlist = {}, onMarkAsWatched = {}, onRate = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun DetailsWatchedPreview() {
    KeveOQTheme {
        DetailsScreen(
            uiState = DetailsUiState.Success(previewDetails, WatchlistAction.Rate(rating = 4)),
            snackbarHostState = SnackbarHostState(),
            onBack = {}, onRetry = {}, onAddToWatchlist = {}, onMarkAsWatched = {}, onRate = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun DetailsErrorPreview() {
    KeveOQTheme {
        DetailsScreen(
            uiState = DetailsUiState.Error(R.string.error_network),
            snackbarHostState = SnackbarHostState(),
            onBack = {}, onRetry = {}, onAddToWatchlist = {}, onMarkAsWatched = {}, onRate = {},
        )
    }
}
