package com.kotlin.weatherforecast.db

import androidx.lifecycle.LiveData
import com.kotlin.weatherforecast.model.AlertsModel
import com.kotlin.weatherforecast.model.FavLocationsModel

interface LocalSource {
    fun getAllLocations() : LiveData<List<FavLocationsModel>>
     fun delete(favLocationsModel: FavLocationsModel)
     fun insert(favLocationsModel: FavLocationsModel)

     fun deleteAlert(alertsModel: AlertsModel)
     fun insertAlert(alertsModel: AlertsModel)
     fun getAllAlerts() : LiveData<List<AlertsModel>>
}