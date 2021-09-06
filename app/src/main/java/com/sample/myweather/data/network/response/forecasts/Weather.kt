package com.sample.myweather.data.network.response.forecasts

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)