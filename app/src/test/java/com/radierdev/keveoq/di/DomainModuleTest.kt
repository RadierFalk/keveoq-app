package com.radierdev.keveoq.di

import com.radierdev.keveoq.core.network.NetworkMonitor
import com.radierdev.keveoq.domain.fake.FakeMovieRepository
import com.radierdev.keveoq.domain.fake.FakeNetworkMonitor
import com.radierdev.keveoq.domain.fake.FakeSavedMovieRepository
import com.radierdev.keveoq.domain.fake.TestData
import com.radierdev.keveoq.domain.repository.MovieRepository
import com.radierdev.keveoq.domain.repository.SavedMovieRepository
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
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import com.radierdev.keveoq.domain.usecase.PickRandomMovieUseCase
import java.time.Clock
import kotlin.random.Random

/**
 * Confere que o Koin consegue construir todos os casos de uso do [domainModule].
 * As dependências que no app vêm do Android (Room, Retrofit, conectividade)
 * são trocadas por versões falsas, para o teste rodar na JVM.
 */
class DomainModuleTest {

    private val fakes = module {
        single<MovieRepository> { FakeMovieRepository() }
        single<SavedMovieRepository> { FakeSavedMovieRepository() }
        single<NetworkMonitor> { FakeNetworkMonitor() }
        single<Clock> { TestData.clock }
        single<Random> { Random(0) }
    }

    private val app: KoinApplication = koinApplication { modules(fakes, domainModule) }

    @After
    fun tearDown() {
        app.close()
    }

    @Test
    fun `koin deve construir todos os casos de uso`() {
        val koin = app.koin
        assertNotNull(koin.get<GetMoodsUseCase>())
        assertNotNull(koin.get<ObserveRecommendationsUseCase>())
        assertNotNull(koin.get<RefreshRecommendationsUseCase>())
        assertNotNull(koin.get<ObserveConnectivityUseCase>())
        assertNotNull(koin.get<ObserveMovieDetailsUseCase>())
        assertNotNull(koin.get<RefreshMovieDetailsUseCase>())
        assertNotNull(koin.get<ObserveSavedMovieUseCase>())
        assertNotNull(koin.get<ObserveWatchlistUseCase>())
        assertNotNull(koin.get<AddToWatchlistUseCase>())
        assertNotNull(koin.get<MarkAsWatchedUseCase>())
        assertNotNull(koin.get<RateMovieUseCase>())
        assertNotNull(koin.get<CalculateNotificationDelayUseCase>())
        assertNotNull(koin.get<PickRandomMovieUseCase>())
    }
}
