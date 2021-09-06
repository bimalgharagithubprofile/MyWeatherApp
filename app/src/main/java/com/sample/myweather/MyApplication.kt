package com.sample.myweather

import android.app.Application
import com.sample.myweather.data.local.PreferenceProvider
import com.sample.myweather.data.local.Storage
import com.sample.myweather.data.repositories.CommonRepository
import com.sample.myweather.data.repositories.WeatherRepository
import com.sample.myweather.ui.cities.CitiesViewModelFactory
import com.sample.myweather.ui.home.HomeViewModelFactory
import com.sample.myweather.ui.weather.WeatherViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class MyApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MyApplication))

        //Storage
        bind() from singleton { PreferenceProvider(instance()) }
        bind() from singleton { Storage(instance()) }

        //Common Repository (storage purpose)
        bind() from singleton { CommonRepository(instance()) }

        //Home
        bind() from singleton { HomeViewModelFactory(instance()) }

        //Cities
        bind() from singleton { CitiesViewModelFactory(instance()) }

        //Weather
        bind() from singleton { WeatherRepository() }
        bind() from singleton { WeatherViewModelFactory(instance()) }

    }



    override fun onCreate() {
        super.onCreate()
    }
}