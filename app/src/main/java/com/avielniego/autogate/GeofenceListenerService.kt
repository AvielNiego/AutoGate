package com.avielniego.autogate

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


class GeofenceListenerService : IntentService("GeofenceListenerService") {
    companion object {
        private const val GATE_PHONE_NUMBER = "0542021662"
        private val LOG_TAG = GeofenceListenerService::class.java.simpleName
    }

    override fun onHandleIntent(intent: Intent) {
        if (GeofencingEvent.fromIntent(intent).hasError()) {
            Log.e(LOG_TAG, "Error in Geofence")
            return
        }
        Log.d(LOG_TAG, "Geofence Triggered")
        when (GeofencingEvent.fromIntent(intent).geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                show()
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                GeofenceRequest(this).request()
            }
        }
    }

    private fun show() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager, getString(R.string.app_name))
        show(buildNotification())
    }

    private fun show(notification: Notification) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(generateUniqueId(), notification)
    }

    private fun generateUniqueId() = (System.currentTimeMillis() % 1_000_000_000).toInt()

    private fun createNotificationChannel(notificationManager: NotificationManager, appName: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return
        val notificationChannel = NotificationChannel("1",
                "11",
                NotificationManager.IMPORTANCE_HIGH).apply {
            enableLights(false)
            enableVibration(false)
            description = appName
        }
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Geofence")
                .setAutoCancel(true)
                .setSubText("Geofence subtitle")
                .setOnlyAlertOnce(true)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentIntent(createPendingIntent(this)).build()
    }

    private fun createPendingIntent(context: Context): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}