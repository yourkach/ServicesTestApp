package com.example.servicestestapp

import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.core.app.NotificationManagerCompat

val ContextWrapper.notificationManager: NotificationManagerCompat
    get() = NotificationManagerCompat.from(this)

fun Any.logDebug(text: String) {
    val tag = this::class.java.simpleName.let { if (it.length > 23) it.substring(0, 23) else it }
    Log.d(tag, text)
}