package com.sample.myweather.utils

import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

class CommonUtils {
    companion object{
        const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
        const val APPID = "fae7190d7e6433ec3a45285ffcf55c86"
        const val HELP_WEBSITE_LINK = "https://github.com/bimalgharagithubprofile/MyWeatherApp/blob/main/README.md"//randomly taken from google vlog

        private var instance: CommonUtils? = null
        fun getInstance(): CommonUtils? {
            if (instance == null) {
                synchronized(CommonUtils::class.java) {
                    if (instance == null) {
                        instance = CommonUtils()
                    }
                }
            }
            return instance
        }
    }

    fun getCityNameFromCity(rawString: String) : String {
        val arrays = rawString.split("@")
        return arrays[0]
    }
    fun getLocationFromCity(rawString: String) : LatLng {
        val arrays = rawString.split("@")
        val locationArrays = arrays[1].split(",")
        return LatLng(locationArrays[0].toDouble(), locationArrays[1].toDouble())
    }

    fun getDayName(givenDate: String) : String {
        return try {
            val inFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val date = inFormat.parse(givenDate)
            val outFormat = SimpleDateFormat("EEEE", Locale.US)
            outFormat.format(date)
        }catch (e: Exception){
            e.printStackTrace()
            "Fun Day"
        }
    }

    fun getCurrentDateTime(format: String): String {
        val now = Calendar.getInstance().time
        return now.toString(format)
    }




}

private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}
