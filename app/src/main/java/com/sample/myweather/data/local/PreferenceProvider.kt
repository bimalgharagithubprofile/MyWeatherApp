package com.sample.myweather.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager


class PreferenceProvider(
    context: Context
) {

    private val appContext = context.applicationContext

    val sSingleton: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

}