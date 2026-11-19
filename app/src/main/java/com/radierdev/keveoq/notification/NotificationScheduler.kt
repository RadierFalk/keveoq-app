package com.radierdev.keveoq.notification

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.radierdev.keveoq.domain.usecase.CalculateNotificationDelayUseCase
import java.util.concurrent.TimeUnit

/**
 * Agenda a notificação de sugestão com o WorkManager (seção 8, RN10).
 *
 * - Repete a cada 4 dias.
 * - A primeira execução espera até a próxima janela 19h–21h (cálculo do domain).
 * - `KEEP`: se o trabalho já estiver agendado, o novo pedido é ignorado. Assim,
 *   abrir o app várias vezes não cria agendamentos duplicados (CA30).
 *
 * Limitação: o WorkManager não garante horário exato; o sistema pode adiar a
 * execução para economizar bateria.
 */
class NotificationScheduler(
    private val workManager: WorkManager,
    private val calculateDelay: CalculateNotificationDelayUseCase,
) {

    fun schedule() {
        val initialDelay = calculateDelay()
        val request = PeriodicWorkRequestBuilder<MovieSuggestionWorker>(REPEAT_INTERVAL_DAYS, TimeUnit.DAYS)
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    companion object {
        const val UNIQUE_WORK_NAME = "movie_suggestion"
        const val REPEAT_INTERVAL_DAYS = 4L
    }
}
