package com.radierdev.keveoq.di

import androidx.work.WorkManager
import com.radierdev.keveoq.core.dispatcher.AppDispatchers
import com.radierdev.keveoq.core.network.AndroidNetworkMonitor
import com.radierdev.keveoq.core.network.NetworkMonitor
import com.radierdev.keveoq.notification.NotificationPermissionPrefs
import com.radierdev.keveoq.notification.NotificationScheduler
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.time.Clock

/**
 * Dependências de infraestrutura usadas por várias camadas.
 * Todas são `single`: uma única instância durante toda a vida do app.
 */
val appModule = module {
    single {
        AppDispatchers(
            io = Dispatchers.IO,
            default = Dispatchers.Default,
            main = Dispatchers.Main,
        )
    }
    single<Clock> { Clock.systemDefaultZone() }
    single<NetworkMonitor> { AndroidNetworkMonitor(androidContext()) }

    // Notificação periódica (seção 8 do planejamento)
    single { NotificationPermissionPrefs(androidContext()) }
    single { WorkManager.getInstance(androidContext()) }
    single { NotificationScheduler(workManager = get(), calculateDelay = get()) }
}
