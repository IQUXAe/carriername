package com.iquxae.carriername

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager

import android.telephony.TelephonyManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast

import com.iquxae.carriername.databinding.FragmentTargetBinding
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.Shizuku
import java.util.Locale

class TargetFragment : Fragment() {
    private val TAG: String = "TargetFragment"

    private var _binding: FragmentTargetBinding? = null

    private val binding get() = _binding!!

    private var subId1: Int = -1;
    private var subId2: Int = -1;

    private var selectedSub: Int = 1;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTargetBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val subscriptionManager = context?.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
        val activeSubscriptions = subscriptionManager?.activeSubscriptionInfoList
        
        var _subId1: Int? = null
        var _subId2: Int? = null
        
        activeSubscriptions?.let { subs ->
            if (subs.size > 0) _subId1 = subs[0].subscriptionId
            if (subs.size > 1) _subId2 = subs[1].subscriptionId
        }

        Log.d(TAG, "#onViewCreated(): subId1=$subId1 subId2=$subId2")

        _subId1?.let {
            subId1 = it
            view.findViewById<RadioButton>(R.id.sub1_button).text = "Network 1 (carrier: ${getCarrierNameBySubId(it)})"
        }
        _subId2?.let {
            subId2 = it
            view.findViewById<RadioButton>(R.id.sub2_button).text = "Network 2 (carrier: ${getCarrierNameBySubId(it)})"
        }

        if (subId2 == -1) {
            view.findViewById<View>(R.id.sub2_button).visibility = View.GONE
        }

        view.findViewById<Button>(R.id.button_set).setOnClickListener { 
            val carrierName = view.findViewById<EditText>(R.id.text_entry).text.toString()
            val isoRegion = view.findViewById<EditText>(R.id.iso_region_input).text.toString()
            val sim1Code = view.findViewById<EditText>(R.id.sim1_numeric_input).text.toString()
            val sim2Code = view.findViewById<EditText>(R.id.sim2_numeric_input).text.toString()
            
            onSetName(carrierName, isoRegion)
            if (sim1Code.isNotEmpty() || sim2Code.isNotEmpty()) {
                onSetSimCodes(sim1Code, sim2Code)
            }
        }

        view.findViewById<Button>(R.id.button_reset).setOnClickListener {
            onResetName()
            view.findViewById<EditText>(R.id.text_entry).setText("")
            view.findViewById<EditText>(R.id.iso_region_input).setText("")
            view.findViewById<EditText>(R.id.sim1_numeric_input).setText("")
            view.findViewById<EditText>(R.id.sim2_numeric_input).setText("")
        }

        view.findViewById<RadioGroup>(R.id.sub_selection).setOnCheckedChangeListener { _, checkedId -> onSelectSub(checkedId) }

        onSelectSub(0)
    }

    private fun onSetName(text: String, isoRegion: String) {
        var p = PersistableBundle();
        if (isoRegion.isNotEmpty()) {
            if (isoRegion.length == 2) {
                p.putString("sim_country_iso_override_string", isoRegion.lowercase(Locale.ROOT))
            } else {
                Toast.makeText(context, "Invalid ISO region!", Toast.LENGTH_SHORT).show()
                return
            }
        }
        Toast.makeText(context, "Set carrier vanity name to \"$text\"", Toast.LENGTH_SHORT).show()

        p.putBoolean(CarrierConfigManager.KEY_CARRIER_NAME_OVERRIDE_BOOL, true)
        p.putString(CarrierConfigManager.KEY_CARRIER_NAME_STRING, text)
        p.putString(CarrierConfigManager.KEY_CARRIER_CONFIG_VERSION_STRING, /* trans rights! 🏳️‍⚧️*/ ":3")
        p.putBoolean(CarrierConfigManager.KEY_CARRIER_VOLTE_AVAILABLE_BOOL, true)

        val subId: Int;
        if (selectedSub == 1) {
            subId = subId1!!
        } else {
            subId = subId2!!
        }
        overrideCarrierConfig(subId, p)
    }

    private fun onResetName() {
        var p = PersistableBundle();
        p.putBoolean(CarrierConfigManager.KEY_CARRIER_NAME_OVERRIDE_BOOL, false)
        p.putString(CarrierConfigManager.KEY_CARRIER_NAME_STRING, "")
        val subId: Int;
        if (selectedSub == 1) {
            subId = subId1!!
        } else {
            subId = subId2!!
        }
        // Sometimes just setting the override to null doesn't work, so let's first set another override, disabling the name change
        overrideCarrierConfig(subId, p)
        overrideCarrierConfig(subId, null)
    }

    private fun onSelectSub(id: Int) {
        if (id == R.id.sub1_button || id == 0) {
            selectedSub = 1;
            Toast.makeText(context, "Selected Network 1", Toast.LENGTH_SHORT).show()
        } else if (id == R.id.sub2_button) {
            selectedSub = 2;
            Toast.makeText(context, "Selected Network 2", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCarrierNameBySubId(subId: Int): String {
        val telephonyManager = context!!.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            ?: return ""
        return telephonyManager.networkOperatorName ?: ""
    }

    private fun onSetSimCodes(sim1Code: String, sim2Code: String) {
        if (sim1Code.isEmpty() && sim2Code.isEmpty()) {
            Toast.makeText(context, "Enter at least one SIM code", Toast.LENGTH_SHORT).show()
            return
        }

        val codes = when {
            sim1Code.isNotEmpty() && sim2Code.isNotEmpty() -> "$sim1Code,$sim2Code"
            sim1Code.isNotEmpty() -> sim1Code
            else -> sim2Code
        }

        try {
            val process = Shizuku.newProcess(arrayOf("setprop", "gsm.sim.operator.numeric", codes), null, null)
            process.waitFor()
            Toast.makeText(context, "SIM codes set: $codes", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set SIM codes", e)
            Toast.makeText(context, "Failed to set SIM codes: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun overrideCarrierConfig(subId: Int, p: PersistableBundle?) {
        try {
            val carrierConfigManager = context?.getSystemService(Context.CARRIER_CONFIG_SERVICE) as? CarrierConfigManager
            // Используем Shizuku для выполнения системной команды
            val process = Shizuku.newProcess(arrayOf("am", "broadcast", "-a", "android.telephony.action.CARRIER_CONFIG_CHANGED", "--ei", "subscription", subId.toString()), null, null)
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to override carrier config", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}