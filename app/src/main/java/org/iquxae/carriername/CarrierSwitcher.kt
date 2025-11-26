package org.iquxae.carriername

import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import android.telephony.CarrierConfigManager
import android.telephony.TelephonyManager
import android.util.Log
import rikka.shizuku.ShizukuBinderWrapper

object CarrierSwitcher {
    private const val TAG = "CarrierSwitcher"

    fun getCarrierNameBySubId(context: Context, subId: Int): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            ?: return ""

        return try {
            val createForSubscriptionId = TelephonyManager::class.java.getMethod(
                "createForSubscriptionId",
                Int::class.javaPrimitiveType
            )
            val subTelephonyManager = createForSubscriptionId.invoke(telephonyManager, subId) as TelephonyManager
            subTelephonyManager.networkOperatorName
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get carrier name", e)
            telephonyManager.networkOperatorName
        }
    }

    fun setCarrierConfig(subId: Int, countryCode: String?, carrierName: String?) {
        val bundle = PersistableBundle()

        if (!countryCode.isNullOrEmpty() && countryCode.length == 2) {
            bundle.putString(
                CarrierConfigManager.KEY_SIM_COUNTRY_ISO_OVERRIDE_STRING,
                countryCode.lowercase()
            )
        }

        if (!carrierName.isNullOrEmpty()) {
            bundle.putBoolean(CarrierConfigManager.KEY_CARRIER_NAME_OVERRIDE_BOOL, true)
            bundle.putString(CarrierConfigManager.KEY_CARRIER_NAME_STRING, carrierName)
        }

        overrideCarrierConfig(subId, bundle)
    }

    fun resetCarrierConfig(subId: Int) {
        overrideCarrierConfig(subId, null)
    }

    private fun overrideCarrierConfig(subId: Int, bundle: PersistableBundle?) {
        try {
            val telephonyFrameworkInitializer = Class.forName("android.telephony.TelephonyFrameworkInitializer")
            val getTelephonyServiceManager = telephonyFrameworkInitializer.getDeclaredMethod("getTelephonyServiceManager")
            val telephonyServiceManager = getTelephonyServiceManager.invoke(null)
            
            val getCarrierConfigServiceRegisterer = telephonyServiceManager.javaClass.getDeclaredMethod("getCarrierConfigServiceRegisterer")
            val carrierConfigServiceRegisterer = getCarrierConfigServiceRegisterer.invoke(telephonyServiceManager)
            
            val get = carrierConfigServiceRegisterer.javaClass.getDeclaredMethod("get")
            val binder = get.invoke(carrierConfigServiceRegisterer) as android.os.IBinder
            
            val stubClass = Class.forName("com.android.internal.telephony.ICarrierConfigLoader\$Stub")
            val asInterface = stubClass.getDeclaredMethod("asInterface", android.os.IBinder::class.java)
            val wrappedBinder = ShizukuBinderWrapper(binder)
            val carrierConfigLoader = asInterface.invoke(null, wrappedBinder)
            
            val overrideConfig = carrierConfigLoader.javaClass.getDeclaredMethod(
                "overrideConfig",
                Int::class.javaPrimitiveType,
                PersistableBundle::class.java,
                Boolean::class.javaPrimitiveType
            )
            overrideConfig.invoke(carrierConfigLoader, subId, bundle, true)
            Log.d(TAG, "Successfully overridden carrier config for subId=$subId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to override carrier config", e)
            throw e
        }
    }
}
