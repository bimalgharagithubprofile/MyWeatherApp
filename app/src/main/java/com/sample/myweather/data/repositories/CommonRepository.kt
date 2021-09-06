package com.sample.myweather.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sample.myweather.data.local.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type

class CommonRepository(private val storage: Storage) {

    private val listType: Type = object : TypeToken<List<String?>?>() {}.type
    private val result = MutableLiveData<List<String>>()

    suspend fun getCities(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {

            val response = prepareCityLists()
            result.postValue(response)

            result
        }
    }
    suspend fun getBookmarkCities(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {

            val response = prepareBookmarkCityLists()
            result.postValue(response)

            result
        }
    }

    private fun prepareCityLists() : List<String> {
        val gson = Gson()

        val allCities = storage.getCities(storage.KEY_ALL_CITIES)
        if(allCities == null){
            val defaultCities = getDefaultCities()

            val jsonString: String = gson.toJson(defaultCities)

            storage.addCities(storage.KEY_ALL_CITIES, jsonString)

            return defaultCities
        } else {
            val outputList: List<String> = gson.fromJson(allCities, listType)

            return outputList
        }
    }
    fun prepareBookmarkCityLists() : List<String> {
        val gson = Gson()

        val bookmarkCities = storage.getCities(storage.KEY_BOOKMARK_CITIES)
        return if(bookmarkCities == null) {
            emptyList()
        } else {
            val outputList: List<String> = gson.fromJson(bookmarkCities, listType)

            outputList
        }
    }


    fun addBookmark(existingBookmarksCities: List<String>, city: String) : Boolean {
        return try {
            val gson = Gson()

            val jsonString: String = if(existingBookmarksCities.isEmpty()){
                val newBookmarksCities = listOf(city)
                gson.toJson(newBookmarksCities)
            } else {
                val newBookmarksCities = existingBookmarksCities + city
                gson.toJson(newBookmarksCities)
            }

            storage.addCities(storage.KEY_BOOKMARK_CITIES, jsonString)

            true

        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }
    fun removeBookmark(city: String): Boolean {
        try {
            val gson = Gson()

            val existingBookmarksCities = prepareBookmarkCityLists()
            return if (existingBookmarksCities.contains(city)) {

                val newBookmarksCities = existingBookmarksCities - city
                val jsonString: String = gson.toJson(newBookmarksCities)

                storage.addCities(storage.KEY_BOOKMARK_CITIES, jsonString)

                true
            } else {
                false
            }
        }catch (e: Exception){
            e.printStackTrace()
            return false
        }
    }

    fun addCity(city: String) : Boolean {
        return try{
            val gson = Gson()

            val existingAllCities = prepareCityLists()

            val newBookmarksCities = existingAllCities + city
            val jsonString: String = gson.toJson(newBookmarksCities)

            storage.addCities(storage.KEY_ALL_CITIES, jsonString)

            true

        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }
    fun removeCity(city: String): Boolean {
        try{
            val gson = Gson()

            val existingAllCities = prepareCityLists().toMutableList() //main
            val existingBookmarksCities = prepareBookmarkCityLists().toMutableList() //bookmarks

            //delete from main
            existingAllCities.remove(city)
            val jsonStringMain: String = gson.toJson(existingAllCities)
            storage.addCities(storage.KEY_ALL_CITIES, jsonStringMain)

            //delete from bookmarks
            if(existingBookmarksCities.contains(city)) {
                existingBookmarksCities.remove(city)
                val jsonStringBookmarks: String = gson.toJson(existingBookmarksCities)
                storage.addCities(storage.KEY_BOOKMARK_CITIES, jsonStringBookmarks)
            }

            return true

        }catch (e: Exception){
            e.printStackTrace()
            return false
        }
    }

    private fun getDefaultCities(): List<String> {
        return listOf(
            "Adilabad@19.6641,78.5320",
            "Hyderabad@17.3850,78.4867",
            "Jagtial@18.7895,78.9120",
            "Karimnagar@18.4386,79.1288",
            "Khammam@17.2473,80.1514",
            "Karimnagar@18.4386,79.1288",
            "Miryalaguda@16.8739,79.5662",
            "Nalgonda@17.0575,79.2684",
            "Nizamabad@18.6725,78.0941",
            "Mahabubnagar@16.7488,78.0035",
            "Ramagundam@18.7519,79.5134",
            "Suryapet@17.1314,79.6336",
            "Siddipet@18.1018,78.8520",
            "Warangal@17.9689,79.5941",
        )
    }




}