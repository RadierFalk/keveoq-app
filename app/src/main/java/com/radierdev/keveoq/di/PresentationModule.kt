package com.radierdev.keveoq.di

import com.radierdev.keveoq.presentation.KeveOQAppViewModel
import com.radierdev.keveoq.presentation.screens.details.DetailsViewModel
import com.radierdev.keveoq.presentation.screens.mood.MoodViewModel
import com.radierdev.keveoq.presentation.screens.recommendations.RecommendationsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** ViewModels: o Koin cria cada um ligado ao ciclo de vida da tela que o pediu. */
val presentationModule = module {
    viewModelOf(::KeveOQAppViewModel)
    viewModelOf(::MoodViewModel)

    // O humor vem da rota de navegação, passado pela tela com parametersOf(mood).
    viewModel { params ->
        RecommendationsViewModel(
            mood = params.get(),
            observeRecommendations = get(),
            refreshRecommendations = get(),
            clock = get(),
        )
    }

    // O ID do filme vem da rota de navegação, passado pela tela com parametersOf(movieId).
    viewModel { params ->
        DetailsViewModel(
            movieId = params.get(),
            observeMovieDetails = get(),
            observeSavedMovie = get(),
            refreshMovieDetails = get(),
            addToWatchlist = get(),
            markAsWatched = get(),
            rateMovie = get(),
        )
    }
}
