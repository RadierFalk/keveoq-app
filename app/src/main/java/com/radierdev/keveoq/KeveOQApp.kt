package com.radierdev.keveoq

import android.app.Application
import com.radierdev.keveoq.di.appModule
import com.radierdev.keveoq.di.dataModule
import com.radierdev.keveoq.di.domainModule
import com.radierdev.keveoq.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Ponto de entrada do processo do app: roda antes de qualquer tela.
 * Inicia o Koin com todos os módulos de dependências.
 */
class KeveOQApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@KeveOQApp)
            modules(appModule, dataModule, domainModule, presentationModule)
        }
    }
}
