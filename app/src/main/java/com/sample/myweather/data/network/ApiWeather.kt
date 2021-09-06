package com.sample.myweather.data.network

import com.sample.myweather.data.network.response.forecasts.ResponseForecast
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiWeather {

    @Headers("Content-Type: application/json")
    @GET("forecast")
    suspend fun getForecasts(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("units") units: String
    ) : ResponseForecast


}