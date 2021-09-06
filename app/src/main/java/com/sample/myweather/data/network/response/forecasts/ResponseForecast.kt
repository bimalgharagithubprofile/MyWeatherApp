package com.sample.myweather.data.network.response.forecasts

data class ResponseForecast(
    val city: City,
    val cnt: Int,
    val cod: String,
    var list: List<Day>,
    val message: Int
)