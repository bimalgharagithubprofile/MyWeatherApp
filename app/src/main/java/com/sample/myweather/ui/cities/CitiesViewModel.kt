package com.sample.myweather.ui.cities

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.myweather.data.repositories.CommonRepository
import com.sample.myweather.utils.Coroutines
import com.sample.myweather.utils.lazyDeferred
import com.sample.myweather.utils.log
import kotlinx.coroutines.launch

class CitiesViewModel(
    private val repository: CommonRepository
) : ViewModel() {

    val listenerBookmarkADD = MutableLiveData<Boolean>()
    val listenerBookmarkREMOVE = MutableLiveData<Map<Int, Boolean>>()
    val listenerRemove = MutableLiveData<Map<Int, Boolean>>()

    init {
        viewModelScope.launch {
            log("This is viewModelScope")
        }
    }

    val citiesData by lazyDeferred {
        repository.getCities()
    }

    val bookmarkCitiesData by lazyDeferred {
        repository.getBookmarkCities()
    }


    fun setNewBookmark(city: String) = Coroutines.io(viewModelScope) {
        val existingBookmarksCities = repository.prepareBookmarkCityLists()
        if(existingBookmarksCities.contains(city)){
            listenerBookmarkADD.postValue(false)
        } else {
            val response = repository.addBookmark(existingBookmarksCities, city)
            listenerBookmarkADD.postValue(response)
        }
    }

    fun removeBookmark(position: Int, city: String) = Coroutines.io(viewModelScope) {
        val response = repository.removeBookmark(city)

        listenerBookmarkREMOVE.postValue(mapOf(position to response))
    }

    fun removeCity(position: Int, city: String) = Coroutines.io(viewModelScope) {
        val response = repository.removeCity(city)

        listenerRemove.postValue(mapOf(position to response))
    }


}