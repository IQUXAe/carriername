package org.iquxae.carriername

import android.content.Context
import android.os.Build
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import org.iquxae.carriername.databinding.FragmentTargetBinding
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.Shizuku
import java.util.Locale

class TargetFragment : Fragment() {
    private val TAG: String = "TargetFragment"

    private var _binding: FragmentTargetBinding? = null

    private val binding get() = _binding!!

    private var subId1: Int = -1;
    private var subId2: Int = -1;

    private var selectedSub: Int = 1
    
    private lateinit var settingsManager: SettingsManager
    private var selectedCarrier: PresetCarriers.CarrierPreset? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTargetBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        settingsManager = SettingsManager(requireContext())
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }

        try {
            val getSubIdMethod = SubscriptionManager::class.java.getDeclaredMethod("getSubId", Int::class.javaPrimitiveType)
            val _subId1 = getSubIdMethod.invoke(null, 0) as? IntArray
            val _subId2 = getSubIdMethod.invoke(null, 1) as? IntArray
            
            if (_subId1 != null && _subId1.isNotEmpty()) {
                subId1 = _subId1[0]
                view.findViewById<RadioButton>(R.id.sub1_button).text = "Network 1 (carrier: ${getCarrierNameBySubId(subId1)})"
            }
            if (_subId2 != null && _subId2.isNotEmpty()) {
                subId2 = _subId2[0]
                view.findViewById<RadioButton>(R.id.sub2_button).text = "Network 2 (carrier: ${getCarrierNameBySubId(subId2)})"
            }
            
            Log.d(TAG, "#onViewCreated(): subId1=$subId1 subId2=$subId2")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get subscription IDs", e)
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
            
            
            settingsManager.saveSettings(carrierName, isoRegion, sim1Code, sim2Code, selectedSub)
        }

        view.findViewById<Button>(R.id.button_reset).setOnClickListener {
            onResetName()
            view.findViewById<EditText>(R.id.text_entry).setText("")
            view.findViewById<EditText>(R.id.iso_region_input).setText("")
            view.findViewById<EditText>(R.id.sim1_numeric_input).setText("")
            view.findViewById<EditText>(R.id.sim2_numeric_input).setText("")
            
            settingsManager.clearSettings()
        }

        setupCarrierSpinner(view)

        view.findViewById<Button>(R.id.button_switch_carrier).setOnClickListener {
            onSwitchCarrier()
        }

        view.findViewById<Button>(R.id.button_reset_carrier).setOnClickListener {
            onResetCarrier()
        }

        view.findViewById<RadioGroup>(R.id.sub_selection).setOnCheckedChangeListener { _, checkedId -> onSelectSub(checkedId) }

        onSelectSub(0)
        
       
        loadSavedSettings(view)
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

        val subId = if (selectedSub == 1) subId1 else subId2
        
        if (subId == -1) {
            Toast.makeText(context, "No SIM card detected!", Toast.LENGTH_SHORT).show()
            return
        }
        
        overrideCarrierConfig(subId, p)
    }

    private fun onResetName() {
        val subId = if (selectedSub == 1) subId1 else subId2
        
        if (subId == -1) {
            Toast.makeText(context, "No SIM card detected!", Toast.LENGTH_SHORT).show()
            return
        }
        
        var p = PersistableBundle();
        p.putBoolean(CarrierConfigManager.KEY_CARRIER_NAME_OVERRIDE_BOOL, false)
        p.putString(CarrierConfigManager.KEY_CARRIER_NAME_STRING, "")
        
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

    private fun setupCarrierSpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.carrier_preset_spinner)
        val carrierNameInputLayout = view.findViewById<View>(R.id.custom_carrier_name_layout)
        val carrierCountryInputLayout = view.findViewById<View>(R.id.custom_country_code_layout)
        val carrierNameInput = view.findViewById<EditText>(R.id.carrier_name_input)
        val countryCodeInput = view.findViewById<EditText>(R.id.carrier_country_input)

        val groupedCarriers = mutableListOf<String>()
        PresetCarriers.presets.groupBy { it.region }.forEach { (region, carriers) ->
            if (region.isNotEmpty()) {
                val regionName = CountryPresets.countries.find { it.code == region }?.name ?: region
                groupedCarriers.add("--- $regionName ---")
                carriers.forEach { groupedCarriers.add("  ${it.name}") }
            }
        }
        groupedCarriers.add("Custom")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, groupedCarriers)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = groupedCarriers[position]
                if (selected == "Custom") {
                    selectedCarrier = PresetCarriers.CarrierPreset("Custom", "", "")
                    carrierNameInputLayout.visibility = View.VISIBLE
                    carrierCountryInputLayout.visibility = View.VISIBLE
                } else if (!selected.startsWith("---")) {
                    val carrierName = selected.trim()
                    selectedCarrier = PresetCarriers.presets.find { it.name == carrierName }
                    carrierNameInputLayout.visibility = View.GONE
                    carrierCountryInputLayout.visibility = View.GONE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        if (settingsManager.hasSwitcherSettings()) {
            val savedCarrier = settingsManager.getSwitcherCarrierName()
            val savedCountry = settingsManager.getSwitcherCountryCode()

            if (savedCarrier.isNotEmpty() || savedCountry.isNotEmpty()) {
                val preset = PresetCarriers.presets.find {
                    it.displayName == savedCarrier && it.region == savedCountry
                }

                if (preset != null) {
                    val index = groupedCarriers.indexOfFirst { it.trim() == preset.name }
                    if (index >= 0) {
                        spinner.setSelection(index)
                    }
                } else {
                    spinner.setSelection(groupedCarriers.size - 1)
                    carrierNameInput.setText(savedCarrier)
                    countryCodeInput.setText(savedCountry)
                    carrierNameInputLayout.visibility = View.VISIBLE
                    carrierCountryInputLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun onSwitchCarrier() {
        val subId = if (selectedSub == 1) subId1 else subId2

        if (subId == -1) {
            Toast.makeText(context, "No SIM card detected!", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCarrier == null) {
            Toast.makeText(context, "Select a carrier", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val carrier: String?
            val country: String?

            if (selectedCarrier?.name == "Custom") {
                val carrierNameInput = view?.findViewById<EditText>(R.id.carrier_name_input)
                val countryCodeInput = view?.findViewById<EditText>(R.id.carrier_country_input)
                carrier = carrierNameInput?.text?.toString()?.takeIf { it.isNotEmpty() }
                country = countryCodeInput?.text?.toString()?.takeIf { it.length == 2 }

                if (carrier == null && country == null) {
                    Toast.makeText(context, "Enter carrier name or country code", Toast.LENGTH_SHORT).show()
                    return
                }
            } else {
                carrier = selectedCarrier?.displayName
                country = selectedCarrier?.region?.takeIf { it.isNotEmpty() }
            }

            CarrierSwitcher.setCarrierConfig(subId, country, carrier)

            settingsManager.saveSwitcherSettings(
                carrier ?: "",
                country ?: "",
                selectedSub
            )

            Toast.makeText(context, "Carrier switched successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to switch carrier", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun onResetCarrier() {
        val subId = if (selectedSub == 1) subId1 else subId2
        
        if (subId == -1) {
            Toast.makeText(context, "No SIM card detected!", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            CarrierSwitcher.resetCarrierConfig(subId)
            
            settingsManager.saveSwitcherSettings("", "", selectedSub)
            
            Toast.makeText(context, "Carrier reset successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reset carrier", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
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
            // Using reflection to access the private newProcess method in Shizuku 13.1.5
            val shizukuClass = Class.forName("rikka.shizuku.Shizuku")
            val newProcessMethod = shizukuClass.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            )
            newProcessMethod.isAccessible = true
            val process = newProcessMethod.invoke(null, arrayOf("setprop", "gsm.sim.operator.numeric", codes), null, null) as Process
            val exitCode = process.waitFor()
            if (exitCode == 0) {
                Toast.makeText(context, "SIM codes set: $codes", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to set SIM codes (exit code: $exitCode)", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set SIM codes", e)
            Toast.makeText(context, "Failed to set SIM codes: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun overrideCarrierConfig(subId: Int, p: PersistableBundle?) {
        try {
            Log.d(TAG, "Getting ServiceManager...")
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getService = serviceManagerClass.getDeclaredMethod("getService", String::class.java)
            val binder = getService.invoke(null, "carrier_config") as android.os.IBinder
            Log.d(TAG, "Got binder: $binder")
            
            Log.d(TAG, "Loading ICarrierConfigLoader...")
            val stubClass = Class.forName("com.android.internal.telephony.ICarrierConfigLoader\$Stub")
            val asInterface = stubClass.getDeclaredMethod("asInterface", android.os.IBinder::class.java)
            
            Log.d(TAG, "Wrapping binder with Shizuku...")
            val wrappedBinder = ShizukuBinderWrapper(binder)
            val loader = asInterface.invoke(null, wrappedBinder)
            Log.d(TAG, "Got loader: $loader")
            
            Log.d(TAG, "Getting overrideConfig method...")
            val loaderInterface = Class.forName("com.android.internal.telephony.ICarrierConfigLoader")
            val overrideConfig = loaderInterface.getDeclaredMethod(
                "overrideConfig",
                Int::class.javaPrimitiveType,
                PersistableBundle::class.java,
                Boolean::class.javaPrimitiveType
            )
            Log.d(TAG, "Invoking overrideConfig for subId=$subId")
            overrideConfig.invoke(loader, subId, p, true)
            Log.d(TAG, "Successfully overridden carrier config")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to override carrier config at: ${e.stackTraceToString()}")
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadSavedSettings(view: View) {
        if (settingsManager.hasSettings()) {
            view.findViewById<EditText>(R.id.text_entry).setText(settingsManager.getCarrierName())
            view.findViewById<EditText>(R.id.iso_region_input).setText(settingsManager.getIsoRegion())
            view.findViewById<EditText>(R.id.sim1_numeric_input).setText(settingsManager.getSim1Code())
            view.findViewById<EditText>(R.id.sim2_numeric_input).setText(settingsManager.getSim2Code())
            
            val savedSub = settingsManager.getSelectedSub()
            if (savedSub == 2 && subId2 != -1) {
                view.findViewById<RadioButton>(R.id.sub2_button).isChecked = true
                selectedSub = 2
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}