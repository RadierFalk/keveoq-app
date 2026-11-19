package com.radierdev.keveoq.di

import com.radierdev.keveoq.domain.usecase.AddToWatchlistUseCase
import com.radierdev.keveoq.domain.usecase.CalculateNotificationDelayUseCase
import com.radierdev.keveoq.domain.usecase.GetMoodsUseCase
import com.radierdev.keveoq.domain.usecase.MarkAsWatchedUseCase
import com.radierdev.keveoq.domain.usecase.ObserveConnectivityUseCase
import com.radierdev.keveoq.domain.usecase.ObserveMovieDetailsUseCase
import com.radierdev.keveoq.domain.usecase.ObserveRecommendationsUseCase
import com.radierdev.keveoq.domain.usecase.ObserveSavedMovieUseCase
import com.radierdev.keveoq.domain.usecase.ObserveWatchlistUseCase
import com.radierdev.keveoq.domain.usecase.RateMovieUseCase
import com.radierdev.keveoq.domain.usecase.RefreshMovieDetailsUseCase
import com.radierdev.keveoq.domain.usecase.RefreshRecommendationsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/**
 * Casos de uso, registrados como `factory`: uma instância nova a cada pedido.
 * Eles não guardam estado, então não há motivo para mantê-los vivos.
 *
 * `factoryOf(::Classe)` resolve os parâmetros do construtor automaticamente
 * com o que já está registrado (repositórios, Clock, NetworkMonitor).
 */
val domainModule = module {
    factoryOf(::GetMoodsUseCase)
    factoryOf(::ObserveRecommendationsUseCase)
    factoryOf(::RefreshRecommendationsUseCase)
    factoryOf(::ObserveConnectivityUseCase)
    factoryOf(::ObserveMovieDetailsUseCase)
    factoryOf(::RefreshMovieDetailsUseCase)
    factoryOf(::ObserveSavedMovieUseCase)
    factoryOf(::ObserveWatchlistUseCase)
    factoryOf(::AddToWatchlistUseCase)
    factoryOf(::MarkAsWatchedUseCase)
    factoryOf(::RateMovieUseCase)
    factoryOf(::CalculateNotificationDelayUseCase)
}
