package com.sample.myweather.utils

import com.google.android.gms.maps.model.LatLng
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CommonUtils {
    companion object{
        const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
        const val APPID = "fae7190d7e6433ec3a45285ffcf55c86"
        const val HELP_WEBSITE_LINK = "https://www.termsfeed.com/blog/terms-conditions-mobile-apps/#Terms_And_Conditions_For_Simple_Apps"//randomly taken from google vlog

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




}

private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}
