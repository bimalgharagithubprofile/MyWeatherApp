package com.sample.myweather.ui.cities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sample.myweather.data.repositories.CommonRepository

@Suppress("UNCHECKED_CAST")
class CitiesViewModelFactory(
    private val repository: CommonRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CitiesViewModel(repository) as T
    }
}