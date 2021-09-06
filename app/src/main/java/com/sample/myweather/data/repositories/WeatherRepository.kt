package com.sample.myweather.data.repositories

import com.sample.myweather.data.network.ApiWeather
import com.sample.myweather.data.network.configuration.RetrofitClient
import com.sample.myweather.data.network.configuration.SafeApiRequest
import com.sample.myweather.utils.CommonUtils.Companion.APPID

class WeatherRepository : SafeApiRequest() {
    // this instance can be IOC (get from DI) but i made retrofitClient builder generic type
    // this way our retrofitClient can build on any api-interface
    // if we use multi-repo approach then we can build multiple api-interface using same retrofitClient
    private val retrofitClient = RetrofitClient().buildClient(ApiWeather::class.java)



    //wrap the callback with SafeApiRequest to handle success & error(s) & other (if any)
    suspend fun getForecasts(lat: Double, lon: Double, units: String) = apiRequest {
        retrofitClient.getForecasts(lat, lon, APPID, units)
    }


}