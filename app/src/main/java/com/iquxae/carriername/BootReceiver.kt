package com.iquxae.carriername

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Boot completed, starting restore service")
            val serviceIntent = Intent(context, RestoreService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}