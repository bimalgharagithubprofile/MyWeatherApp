package com.sample.myweather.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sample.myweather.data.repositories.CommonRepository
import com.sample.myweather.data.repositories.WeatherRepository

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(
    private val repository: CommonRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}