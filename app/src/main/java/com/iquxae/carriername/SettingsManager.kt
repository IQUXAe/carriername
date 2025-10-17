package com.iquxae.carriername

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("carrier_settings", Context.MODE_PRIVATE)
    
    fun saveSettings(carrierName: String, isoRegion: String, sim1Code: String, sim2Code: String, selectedSub: Int) {
        prefs.edit().apply {
            putString("carrier_name", carrierName)
            putString("iso_region", isoRegion)
            putString("sim1_code", sim1Code)
            putString("sim2_code", sim2Code)
            putInt("selected_sub", selectedSub)
            putBoolean("has_settings", true)
            apply()
        }
    }
    
    fun hasSettings(): Boolean = prefs.getBoolean("has_settings", false)
    
    fun getCarrierName(): String = prefs.getString("carrier_name", "") ?: ""
    fun getIsoRegion(): String = prefs.getString("iso_region", "") ?: ""
    fun getSim1Code(): String = prefs.getString("sim1_code", "") ?: ""
    fun getSim2Code(): String = prefs.getString("sim2_code", "") ?: ""
    fun getSelectedSub(): Int = prefs.getInt("selected_sub", 1)
    
    fun clearSettings() {
        prefs.edit().clear().apply()
    }
}