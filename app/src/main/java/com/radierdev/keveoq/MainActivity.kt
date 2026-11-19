package com.radierdev.keveoq

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.radierdev.keveoq.notification.NotificationPermissionPrefs
import com.radierdev.keveoq.presentation.KeveOQApp
import com.radierdev.keveoq.presentation.theme.KeveOQTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val permissionPrefs: NotificationPermissionPrefs by inject()

    // O resultado não muda nada no app: negar só desliga as notificações (RF19).
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KeveOQTheme {
                KeveOQApp()
            }
        }
        if (savedInstanceState == null) requestNotificationPermissionOnce()
    }

    /**
     * Android 13+: pede a permissão de notificação só na primeira abertura (RF18).
     * Abaixo do 13 a permissão não existe (é concedida na instalação).
     */
    private fun requestNotificationPermissionOnce() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (permissionPrefs.wasPermissionRequested) return

        val alreadyGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!alreadyGranted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionPrefs.wasPermissionRequested = true
    }
}
