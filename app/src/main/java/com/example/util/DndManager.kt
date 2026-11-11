package com.example.util

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.provider.Settings

object DndManager {

    fun hasDndPermission(context: Context): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        return notificationManager?.isNotificationPolicyAccessGranted == true
    }

    fun getDndSettingsIntent(): Intent {
        return Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
    }

    /**
     * Toggles Do Not Disturb mode. Returns true if state was updated.
     */
    fun setDnd(context: Context, enable: Boolean): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

        if (hasDndPermission(context)) {
            try {
                if (enable) {
                    notificationManager?.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                    audioManager?.ringerMode = AudioManager.RINGER_MODE_SILENT
                } else {
                    notificationManager?.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                    audioManager?.ringerMode = AudioManager.RINGER_MODE_NORMAL
                }
                return true
            } catch (_: Exception) {
                // Handle security or policy exceptions gracefully
            }
        } else {
            // Fallback: try ringer mode if feasible
            try {
                if (enable) {
                    audioManager?.ringerMode = AudioManager.RINGER_MODE_SILENT
                } else {
                    audioManager?.ringerMode = AudioManager.RINGER_MODE_NORMAL
                }
            } catch (_: Exception) {}
        }
        return false
    }
}
