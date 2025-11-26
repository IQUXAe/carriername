package org.iquxae.carriername

object CountryPresets {
    data class CountryInfo(
        val code: String,
        val name: String
    )

    val countries = listOf(
        CountryInfo("CN", "China"),
        CountryInfo("HK", "Hong Kong"),
        CountryInfo("MO", "Macau"),
        CountryInfo("TW", "Taiwan"),
        CountryInfo("JP", "Japan"),
        CountryInfo("KR", "South Korea"),
        CountryInfo("US", "United States"),
        CountryInfo("GB", "United Kingdom"),
        CountryInfo("DE", "Germany"),
        CountryInfo("FR", "France"),
        CountryInfo("IT", "Italy"),
        CountryInfo("ES", "Spain"),
        CountryInfo("PT", "Portugal"),
        CountryInfo("RU", "Russia"), // SVO ZOV Goyda
        CountryInfo("IN", "India"),
        CountryInfo("AU", "Australia"),
        CountryInfo("NZ", "New Zealand"),
        CountryInfo("SG", "Singapore"),
        CountryInfo("MY", "Malaysia"),
        CountryInfo("TH", "Thailand"),
        CountryInfo("VN", "Vietnam"),
        CountryInfo("ID", "Indonesia"),
        CountryInfo("PH", "Philippines"),
        CountryInfo("CA", "Canada"),
        CountryInfo("MX", "Mexico"),
        CountryInfo("BR", "Brazil"),
        CountryInfo("AR", "Argentina"),
        CountryInfo("ZA", "South Africa")
    ).sortedBy { it.name }
}
