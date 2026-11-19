package com.radierdev.keveoq

import android.app.Application
import com.radierdev.keveoq.di.appModule
import com.radierdev.keveoq.di.dataModule
import com.radierdev.keveoq.di.domainModule
import com.radierdev.keveoq.di.presentationModule
import com.radierdev.keveoq.notification.NotificationChannels
import com.radierdev.keveoq.notification.NotificationScheduler
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Ponto de entrada do processo do app: roda antes de qualquer tela.
 *
 * 1. Inicia o Koin com todos os módulos de dependências.
 * 2. Cria o canal de notificação.
 * 3. Agenda a notificação periódica. Com a política KEEP, abrir o app de novo
 *    não duplica o agendamento (CA30).
 */
class KeveOQApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@KeveOQApp)
            modules(appModule, dataModule, domainModule, presentationModule)
        }
        NotificationChannels.create(this)
        get<NotificationScheduler>().schedule()
    }
}
