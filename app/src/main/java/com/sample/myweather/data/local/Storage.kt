package com.sample.myweather.data.local


class Storage(private val prefs: PreferenceProvider) {

    public val KEY_ALL_CITIES = "all_cities"
    public val KEY_BOOKMARK_CITIES = "bookmark_cities"



    open fun addCities(key: String, value: String) {
        prefs.sSingleton.edit().putString(key, value).apply()
    }
    open fun getCities(key: String): String? {
        return prefs.sSingleton.getString(key, null)
    }

}