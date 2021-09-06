package com.sample.myweather.ui.home

import androidx.lifecycle.ViewModel
import com.sample.myweather.data.repositories.CommonRepository

class HomeViewModel(
    private val repository: CommonRepository
) : ViewModel() {

    fun addCity(city: String): Boolean {
        return repository.addCity(city)
    }

}