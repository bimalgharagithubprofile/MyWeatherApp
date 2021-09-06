package com.sample.myweather.data.network.configuration

import com.sample.myweather.utils.CommonUtils.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    fun <Api> buildClient(
        api: Class<Api>
    ) : Api {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        chain.proceed(
                            chain.request().newBuilder().also {

//                                In case we have Headers
//                                if (!chain.request().url.toString().contains("login"))
//                                    it.addHeader("Authorization", "Bearer ${SDKApplication.getOAuthState()?.accessToken}")

                            }.build()
                        )
                    }
                    .also { client ->
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

                        client.addInterceptor(logging)
                    }.build()
            )
            .build()
            .create(api)
    }

}