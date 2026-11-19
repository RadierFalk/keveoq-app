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
import androidx.navigation.toRoute
import com.radierdev.keveoq.R
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.presentation.theme.Dimens
// As telas também se chamam XRoute (padrão do CLAUDE.md), como as rotas deste pacote.
// Os aliases deixam claro, aqui, que estes são os composables das telas.
import com.radierdev.keveoq.presentation.screens.details.DetailsRoute as DetailsScreenRoute
import com.radierdev.keveoq.presentation.screens.mood.MoodRoute as MoodScreenRoute
import com.radierdev.keveoq.presentation.screens.recommendations.RecommendationsRoute as RecommendationsScreenRoute
import com.radierdev.keveoq.presentation.screens.watchlist.WatchlistRoute as WatchlistScreenRoute

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
            MoodScreenRoute(
                onSeeMovies = { mood -> navController.navigate(RecommendationsRoute(mood = mood.name)) },
                onOpenWatchlist = { navController.navigate(WatchlistRoute) },
                onOpenAbout = { navController.navigate(AboutRoute) },
            )
        }
        composable<RecommendationsRoute> { backStackEntry ->
            // toRoute() lê os argumentos da rota já tipados; o humor volta ao enum aqui.
            val route = backStackEntry.toRoute<RecommendationsRoute>()
            RecommendationsScreenRoute(
                mood = Mood.valueOf(route.mood),
                onBack = { navController.popBackStack() },
                onMovieClick = { movieId -> navController.navigate(DetailsRoute(movieId = movieId)) },
            )
        }
        composable<DetailsRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<DetailsRoute>()
            DetailsScreenRoute(
                movieId = route.movieId,
                onBack = { navController.popBackStack() },
            )
        }
        composable<WatchlistRoute> {
            WatchlistScreenRoute(
                onBack = { navController.popBackStack() },
                onMovieClick = { movieId -> navController.navigate(DetailsRoute(movieId = movieId)) },
                // Volta para a tela inicial, que já está na pilha.
                onChooseMood = { navController.popBackStack(MoodRoute, inclusive = false) },
            )
        }
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
