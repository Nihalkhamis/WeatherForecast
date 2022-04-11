package com.kotlin.weatherforecast.home.favourites.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.weatherforecast.model.FavLocationsModel
import com.kotlin.weatherforecast.model.WeatherRepository
import com.kotlin.weatherforecast.model.WeatherRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouritesViewModel(private val repo : WeatherRepositoryInterface, private var context: Context) : ViewModel() {

   /* private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text*/

    fun insertLocation(favLocationsModel: FavLocationsModel) {
        viewModelScope.launch(Dispatchers.IO){
            repo.insertLocation(favLocationsModel)
        }
    }

    fun deleteLocation(favLocationsModel: FavLocationsModel) {
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteLocation(favLocationsModel)
        }
    }

    fun getLocations(): LiveData<List<FavLocationsModel>> {
        return repo.getLocations()
    }


}