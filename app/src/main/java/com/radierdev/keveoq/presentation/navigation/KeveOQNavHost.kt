package com.radierdev.keveoq.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.radierdev.keveoq.domain.model.Mood
// As telas também se chamam XRoute (padrão do CLAUDE.md), como as rotas deste pacote.
// Os aliases deixam claro, aqui, que estes são os composables das telas.
import com.radierdev.keveoq.presentation.screens.about.AboutRoute as AboutScreenRoute
import com.radierdev.keveoq.presentation.screens.details.DetailsRoute as DetailsScreenRoute
import com.radierdev.keveoq.presentation.screens.mood.MoodRoute as MoodScreenRoute
import com.radierdev.keveoq.presentation.screens.recommendations.RecommendationsRoute as RecommendationsScreenRoute
import com.radierdev.keveoq.presentation.screens.watchlist.WatchlistRoute as WatchlistScreenRoute

/** Grafo de navegação do app (seção 9.1 do planejamento). Começa na seleção de humor. */
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
                onOpenMovie = { movieId -> navController.navigate(DetailsRoute(movieId = movieId)) },
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
        composable<AboutRoute> {
            AboutScreenRoute(onBack = { navController.popBackStack() })
        }
    }
}
