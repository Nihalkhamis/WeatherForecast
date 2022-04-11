package com.kotlin.weatherforecast.home.alert.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.weatherforecast.model.AlertsModel
import com.kotlin.weatherforecast.model.FavLocationsModel
import com.kotlin.weatherforecast.model.WeatherRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertViewModel(private val repo : WeatherRepositoryInterface, private var context: Context) : ViewModel() {

    fun insertAlert(alertsModel: AlertsModel) {
        viewModelScope.launch(Dispatchers.IO){
            repo.insertAlert(alertsModel)
        }
    }

    fun deleteAlert(alertsModel: AlertsModel) {
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteAlert(alertsModel)
        }
    }

    fun getAlerts(): LiveData<List<AlertsModel>> {
        return repo.getAlerts()
    }
}