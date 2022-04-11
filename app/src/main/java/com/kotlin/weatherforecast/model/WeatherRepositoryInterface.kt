package com.kotlin.weatherforecast.model

import androidx.lifecycle.LiveData
import retrofit2.Response

interface WeatherRepositoryInterface {
    suspend fun getWeatherDetails(lat : String, long : String) : Response<WeatherResponseModel>


    //fav locations
    fun insertLocation(favLocationsModel: FavLocationsModel)
    fun deleteLocation(favLocationsModel: FavLocationsModel)
    fun getLocations() : LiveData<List<FavLocationsModel>>



    //Alerts
    fun insertAlert(alertsModel: AlertsModel)
    fun deleteAlert(alertsModel: AlertsModel)
    fun getAlerts() : LiveData<List<AlertsModel>>

}