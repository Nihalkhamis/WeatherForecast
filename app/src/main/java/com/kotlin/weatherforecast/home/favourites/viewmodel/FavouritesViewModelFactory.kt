package com.kotlin.weatherforecast.home.favourites.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotlin.weatherforecast.model.WeatherRepository
import com.kotlin.weatherforecast.model.WeatherRepositoryInterface

class FavouritesViewModelFactory (private val repository: WeatherRepositoryInterface, private val context: Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavouritesViewModel::class.java)) {
            FavouritesViewModel(repository,context) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}