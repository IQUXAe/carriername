package org.iquxae.carriername

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PersistableBundle
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.Shizuku

class RestoreService : Service() {
    private val TAG = "RestoreService"
    private val CHANNEL_ID = "restore_channel"
    private val NOTIFICATION_ID = 1
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        
        CoroutineScope(Dispatchers.IO).launch {
            delay(10000)
            restoreSettings()
            delay(3000)
            stopSelf()
        }
        
        return START_NOT_STICKY
    }
    
    private fun restoreSettings() {
        val settingsManager = SettingsManager(this)
        
        if (!settingsManager.hasSettings()) {
            Log.d(TAG, "No settings to restore")
            return
        }
        
       
        if (Shizuku.getBinder() == null) {
            Log.w(TAG, "Shizuku not available, retrying in 10 seconds")
            CoroutineScope(Dispatchers.IO).launch {
                delay(10000)
                restoreSettings()
            }
            return
        }
        
        try {
            val carrierName = settingsManager.getCarrierName()
            val isoRegion = settingsManager.getIsoRegion()
            val sim1Code = settingsManager.getSim1Code()
            val sim2Code = settingsManager.getSim2Code()
            val selectedSub = settingsManager.getSelectedSub()
            
            if (sim1Code.isNotEmpty() || sim2Code.isNotEmpty()) {
                restoreSimCodes(sim1Code, sim2Code)
            }
            
            
            if (carrierName.isNotEmpty() || isoRegion.isNotEmpty()) {
                restoreCarrierConfig(carrierName, isoRegion, selectedSub)
            }
            
            Log.d(TAG, "Settings restored successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to restore settings", e)
        }
    }
    
    private fun restoreSimCodes(sim1Code: String, sim2Code: String) {
        if (sim1Code.isEmpty() && sim2Code.isEmpty()) return
        
        val codes = when {
            sim1Code.isNotEmpty() && sim2Code.isNotEmpty() -> "$sim1Code,$sim2Code"
            sim1Code.isNotEmpty() -> sim1Code
            else -> sim2Code
        }
        
        repeat(3) { attempt ->
            try {
                val process = Shizuku.newProcess(arrayOf("setprop", "gsm.sim.operator.numeric", codes), null, null)
                process.waitFor()
                Log.d(TAG, "SIM codes restored: $codes (attempt ${attempt + 1})")
                Thread.sleep(2000)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to restore SIM codes (attempt ${attempt + 1})", e)
                Thread.sleep(2000)
            }
        }
    }
    
    private fun restoreCarrierConfig(carrierName: String, isoRegion: String, selectedSub: Int) {
        try {
            val getSubIdMethod = SubscriptionManager::class.java.getDeclaredMethod("getSubId", Int::class.javaPrimitiveType)
            val subIdArray = getSubIdMethod.invoke(null, selectedSub - 1) as? IntArray
            
            if (subIdArray == null || subIdArray.isEmpty()) {
                Log.w(TAG, "No subscription found for slot ${selectedSub - 1}")
                return
            }
            
            val subId = subIdArray[0]
            val bundle = PersistableBundle()
            
            if (isoRegion.isNotEmpty()) {
                bundle.putString("sim_country_iso_override_string", isoRegion.lowercase())
            }
            
            bundle.putBoolean(CarrierConfigManager.KEY_CARRIER_NAME_OVERRIDE_BOOL, true)
            bundle.putString(CarrierConfigManager.KEY_CARRIER_NAME_STRING, carrierName)
            bundle.putString(CarrierConfigManager.KEY_CARRIER_CONFIG_VERSION_STRING, ":3")
            bundle.putBoolean(CarrierConfigManager.KEY_CARRIER_VOLTE_AVAILABLE_BOOL, true)
            
            overrideCarrierConfig(subId, bundle)
            Log.d(TAG, "Carrier config restored for subId: $subId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to restore carrier config", e)
        }
    }
    
    private fun overrideCarrierConfig(subId: Int, bundle: PersistableBundle) {
        val serviceManagerClass = Class.forName("android.os.ServiceManager")
        val getService = serviceManagerClass.getDeclaredMethod("getService", String::class.java)
        val binder = getService.invoke(null, "carrier_config") as android.os.IBinder
        
        val stubClass = Class.forName("com.android.internal.telephony.ICarrierConfigLoader\$Stub")
        val asInterface = stubClass.getDeclaredMethod("asInterface", android.os.IBinder::class.java)
        
        val wrappedBinder = ShizukuBinderWrapper(binder)
        val loader = asInterface.invoke(null, wrappedBinder)
        
        val loaderInterface = Class.forName("com.android.internal.telephony.ICarrierConfigLoader")
        val overrideConfig = loaderInterface.getDeclaredMethod(
            "overrideConfig",
            Int::class.javaPrimitiveType,
            PersistableBundle::class.java,
            Boolean::class.javaPrimitiveType
        )
        overrideConfig.invoke(loader, subId, bundle, true)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Carrier Settings Restore",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Restoring carrier settings")
            .setContentText("Applying saved settings...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
}