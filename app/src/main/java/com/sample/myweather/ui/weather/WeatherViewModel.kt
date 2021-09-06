package com.sample.myweather.ui.weather

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.myweather.data.network.configuration.NetworkResource
import com.sample.myweather.data.network.response.forecasts.ResponseForecast
import com.sample.myweather.data.repositories.WeatherRepository
import com.sample.myweather.utils.log

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    var lat: Double? = null
    var lon: Double? = null
    private val units: String = "metric"

    var networkError: MutableLiveData<String> = MutableLiveData()//send error(s) when occurs
    var resultForecast: MutableLiveData<ResponseForecast> = MutableLiveData()



    suspend fun getForecasts() {
        if(lat!=null && lon != null) {
            val response = repository.getForecasts(lat!!, lon!!, units)
            Log.d("chk", "callback: $response")

            when(response){
                is NetworkResource.Success -> {
                    log("response success: $response")

                    response.value.let {
                        resultForecast.postValue(it)
                    }
                }
                is NetworkResource.Failure -> {
                    log("response failed: $response")
                    if(response.isNetworkError)
                        networkError.postValue("Please check internet connection !")
                    else
                        networkError.postValue("${response.errorCode}\n${response.errorBody}")
                }
            }
        }
    }
}