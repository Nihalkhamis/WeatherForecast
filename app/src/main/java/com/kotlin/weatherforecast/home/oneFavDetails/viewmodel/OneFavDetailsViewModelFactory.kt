package com.kotlin.weatherforecast.home.oneFavDetails.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotlin.weatherforecast.model.WeatherRepository
import com.kotlin.weatherforecast.model.WeatherRepositoryInterface

class OneFavDetailsViewModelFactory (private val repository: WeatherRepositoryInterface, private val context: Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(OneFavDetailsViewModel::class.java)) {
            OneFavDetailsViewModel(this.repository,context) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}