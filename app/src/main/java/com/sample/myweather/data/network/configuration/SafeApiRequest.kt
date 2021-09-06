package com.sample.myweather.data.network.configuration

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class SafeApiRequest {

    suspend fun<T> apiRequest(call: suspend () -> T) : NetworkResource<T> {
        return withContext(Dispatchers.IO){
            try{
                NetworkResource.Success(call.invoke())
            }catch (throwable: Throwable){
                when (throwable){
                    is HttpException -> {
                        NetworkResource.Failure(false, throwable.code(), throwable.response()?.errorBody())
                    }
                    else -> {
                        NetworkResource.Failure(true, null, null)
                    }
                }
            }
        }
    }

}