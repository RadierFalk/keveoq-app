package com.radierdev.keveoq.notification

import android.content.Context

/**
 * Lembra se o app já pediu a permissão de notificação (decisão 8 do planejamento).
 *
 * Assim o pedido aparece só na primeira abertura (RF18). Se o usuário negar,
 * o app não volta a perguntar e funciona normalmente, sem notificações (RF19).
 */
class NotificationPermissionPrefs(context: Context) {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var wasPermissionRequested: Boolean
        get() = preferences.getBoolean(KEY_PERMISSION_REQUESTED, false)
        set(value) = preferences.edit().putBoolean(KEY_PERMISSION_REQUESTED, value).apply()

    private companion object {
        const val PREFS_NAME = "keveoq_prefs"
        const val KEY_PERMISSION_REQUESTED = "notification_permission_requested"
    }
}
