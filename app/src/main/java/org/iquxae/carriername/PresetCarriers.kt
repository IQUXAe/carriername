package org.iquxae.carriername

object PresetCarriers {
    data class CarrierPreset(
        val name: String,
        val displayName: String,
        val region: String
    )

    val presets = listOf(
        CarrierPreset("China Mobile", "China Mobile", "CN"),
        CarrierPreset("China Unicom", "China Unicom", "CN"),
        CarrierPreset("China Telecom", "China Telecom", "CN"),

        CarrierPreset("CMHK", "CMHK", "HK"),
        CarrierPreset("HKT", "HKT", "HK"),
        CarrierPreset("3HK", "3HK", "HK"),
        CarrierPreset("SmarTone", "SmarTone", "HK"),

        CarrierPreset("CTM", "CTM", "MO"),
        CarrierPreset("3 Macau", "3 Macau", "MO"),

        CarrierPreset("Chunghwa Telecom", "Chunghwa Telecom", "TW"),
        CarrierPreset("Taiwan Mobile", "Taiwan Mobile", "TW"),
        CarrierPreset("FarEasTone", "FarEasTone", "TW"),

        CarrierPreset("NTT docomo", "NTT docomo", "JP"),
        CarrierPreset("au by KDDI", "au by KDDI", "JP"),
        CarrierPreset("Softbank", "Softbank", "JP"),
        CarrierPreset("Rakuten Mobile", "Rakuten Mobile", "JP"),

        CarrierPreset("SK Telecom", "SK Telecom", "KR"),
        CarrierPreset("KT Corporation", "KT Corporation", "KR"),
        CarrierPreset("LG U+", "LG U+", "KR"),

        CarrierPreset("AT&T", "AT&T", "US"),
        CarrierPreset("T-Mobile USA", "T-Mobile USA", "US"),
        CarrierPreset("Verizon", "Verizon", "US"),
        CarrierPreset("Sprint", "Sprint", "US"),

        CarrierPreset("EE", "EE", "GB"),
        CarrierPreset("O2 UK", "O2 UK", "GB"),
        CarrierPreset("Three UK", "Three UK", "GB"),
        CarrierPreset("Vodafone UK", "Vodafone UK", "GB"),

        CarrierPreset("Singtel", "Singtel", "SG"),
        CarrierPreset("StarHub", "StarHub", "SG"),
        CarrierPreset("M1", "M1", "SG"),

        CarrierPreset("Maxis", "Maxis", "MY"),
        CarrierPreset("Celcom", "Celcom", "MY"),
        CarrierPreset("Digi", "Digi", "MY"),
        CarrierPreset("U Mobile", "U Mobile", "MY"),

        CarrierPreset("AIS", "AIS", "TH"),
        CarrierPreset("DTAC", "DTAC", "TH"),
        CarrierPreset("True Move H", "True Move H", "TH"),

        CarrierPreset("Viettel Mobile", "Viettel Mobile", "VN"),
        CarrierPreset("Vinaphone", "Vinaphone", "VN"),
        CarrierPreset("Mobifone", "Mobifone", "VN"),

        CarrierPreset("Telkomsel", "Telkomsel", "ID"),
        CarrierPreset("Indosat Ooredoo", "Indosat Ooredoo", "ID"),
        CarrierPreset("XL Axiata", "XL Axiata", "ID"),

        CarrierPreset("Globe Telecom", "Globe Telecom", "PH"),
        CarrierPreset("Smart Communications", "Smart Communications", "PH"),
        CarrierPreset("DITO Telecommunity", "DITO Telecommunity", "PH"),

        CarrierPreset("Reliance Jio", "Reliance Jio", "IN"),
        CarrierPreset("Bharti Airtel", "Bharti Airtel", "IN"),
        CarrierPreset("Vodafone Idea", "Vodafone Idea", "IN"),

        CarrierPreset("Telstra", "Telstra", "AU"),
        CarrierPreset("Optus", "Optus", "AU"),
        CarrierPreset("Vodafone AU", "Vodafone AU", "AU"),

        CarrierPreset("Bell Mobility", "Bell Mobility", "CA"),
        CarrierPreset("Rogers Wireless", "Rogers Wireless", "CA"),
        CarrierPreset("Telus Mobility", "Telus Mobility", "CA"),

        CarrierPreset("T-Mobile DE", "T-Mobile DE", "DE"),
        CarrierPreset("Vodafone DE", "Vodafone DE", "DE"),
        CarrierPreset("O2 DE", "O2 DE", "DE"),

        CarrierPreset("Orange FR", "Orange FR", "FR"),
        CarrierPreset("SFR", "SFR", "FR"),
        CarrierPreset("Free Mobile", "Free Mobile", "FR"),
        CarrierPreset("Bouygues Telecom", "Bouygues Telecom", "FR"),

        CarrierPreset("Telecom Italia", "Telecom Italia", "IT"),
        CarrierPreset("Vodafone IT", "Vodafone IT", "IT"),
        CarrierPreset("Wind Tre", "Wind Tre", "IT"),

        CarrierPreset("Movistar", "Movistar", "ES"),
        CarrierPreset("Vodafone ES", "Vodafone ES", "ES"),
        CarrierPreset("Orange ES", "Orange ES", "ES"),

        CarrierPreset("MTS", "MTS", "RU"),
        CarrierPreset("MegaFon", "MegaFon", "RU"),
        CarrierPreset("Beeline", "Beeline", "RU"),

        CarrierPreset("Vivo", "Vivo", "BR"),
        CarrierPreset("Claro", "Claro", "BR"),
        CarrierPreset("TIM Brasil", "TIM Brasil", "BR"),

        CarrierPreset("Custom", "", "")
    )
}
