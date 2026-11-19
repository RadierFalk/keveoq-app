package com.radierdev.keveoq.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.radierdev.keveoq.R
import com.radierdev.keveoq.presentation.screens.mood.MoodRoute
import com.radierdev.keveoq.presentation.theme.Dimens

/**
 * Grafo de navegação do app. Começa na seleção de humor.
 *
 * Por enquanto cada destino mostra um [PendingScreen] provisório;
 * as telas reais entram nas tarefas T20 (humor) a T25 (sobre).
 */
@Composable
fun KeveOQNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MoodRoute,
        modifier = modifier,
    ) {
        composable<MoodRoute> {
            MoodRoute(
                onSeeMovies = { mood -> navController.navigate(RecommendationsRoute(mood = mood.name)) },
                onOpenWatchlist = { navController.navigate(WatchlistRoute) },
                onOpenAbout = { navController.navigate(AboutRoute) },
            )
        }
        composable<RecommendationsRoute> { PendingScreen(stringResource(R.string.screen_recommendations)) }
        composable<DetailsRoute> { PendingScreen(stringResource(R.string.screen_details)) }
        composable<WatchlistRoute> { PendingScreen(stringResource(R.string.screen_watchlist)) }
        composable<AboutRoute> { PendingScreen(stringResource(R.string.screen_about)) }
    }
}

/** Tela provisória, removida quando todas as telas reais existirem (T25). */
@Composable
private fun PendingScreen(title: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(Dimens.ScreenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.screen_pending),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
