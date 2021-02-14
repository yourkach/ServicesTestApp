package com.example.servicestestapp.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.example.servicestestapp.MainActivity
import com.example.servicestestapp.R
import com.example.servicestestapp.logDebug
import com.example.servicestestapp.notificationManager

class MyService : Service() {

    override fun onCreate() {
        logDebug("onCreate")
        showToast("Service created")
    }

    override fun onDestroy() {
        stopForegroundMode()
        notificationManager.cancel(NOTIFICATION_ID)
        logDebug("onDestroy")
        showToast("Service destroyed")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logDebug( "onStartCommand")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        logDebug( "onBind")
        stopForegroundMode()
        showNotification("MyService now is Bound")
        return MyServiceBinder()
    }

    override fun onRebind(intent: Intent?) {
        logDebug( "onRebind")
        stopForegroundMode()
        showNotification("MyService now is Bound (rebinded)")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        startForegroundMode()
        return true
    }

    private fun stopForegroundMode() {
        stopForeground(true)
    }

    private fun startForegroundMode() {
        startForeground(
            NOTIFICATION_ID,
            buildNotification(
                text = "Service now in foreground mode",
                iconResourceId = R.drawable.ic_runing_24,
                isOngoing = true
            )
        )
        logDebug("is in foreground: ${isServiceRunningInForeground()}")
    }

    fun showNotification(text: String) {
        notificationManager.notify(NOTIFICATION_ID, buildNotification(text))
    }

    fun startService() {
        startService(Intent(this, this::class.java))
    }

    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun buildNotification(
        text: String,
        @DrawableRes iconResourceId: Int = R.drawable.ic_dude_24,
        isOngoing: Boolean = false
    ): Notification {
        val activityIntent = Intent(this, MainActivity::class.java)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, activityIntent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MyService notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(iconResourceId)
            .setContentTitle("Notification from MyService")
            .setContentText(text)
            .setOngoing(isOngoing)
            .setOnlyAlertOnce(false)
            .setContentIntent(pendingIntent)
            .build()
    }


    private fun isServiceRunningInForeground(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = manager.getRunningServices(Integer.MAX_VALUE)
        return runningServices.any { javaClass.name == it.service.className && it.foreground }
    }


    inner class MyServiceBinder : Binder() {
        fun getService() = this@MyService
    }

    companion object {
        private const val NOTIFICATION_ID = 1124
        const val CHANNEL_ID = "MY_CHANNEL"
    }

}