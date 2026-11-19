package com.radierdev.keveoq.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.radierdev.keveoq.R

/**
 * Canal "Sugestões de filmes" (seção 8 do planejamento).
 *
 * Desde o Android 8 toda notificação pertence a um canal, que o usuário pode
 * silenciar nas configurações. Criar um canal que já existe não tem efeito,
 * então é seguro chamar [create] a cada abertura do app.
 */
object NotificationChannels {

    const val MOVIE_SUGGESTIONS = "movie_suggestions"

    fun create(context: Context) {
        val channel = NotificationChannel(
            MOVIE_SUGGESTIONS,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.notification_channel_description)
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}
