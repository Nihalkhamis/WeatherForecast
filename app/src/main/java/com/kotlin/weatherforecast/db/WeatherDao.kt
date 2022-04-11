package com.kotlin.weatherforecast.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kotlin.weatherforecast.model.AlertsModel
import com.kotlin.weatherforecast.model.FavLocationsModel


@Dao
interface WeatherDao {

    //Fav locations
    @Insert(onConflict = OnConflictStrategy.REPLACE)  //if some data is same/conflict, it'll be replace with new data
     fun insert(favLocationsModel: FavLocationsModel)


    @Delete
     fun delete(favLocationsModel: FavLocationsModel)

    @Query("SELECT * from favourites")
    fun getAllLocations() : LiveData<List<FavLocationsModel>>
    // why not use suspend ? because Room does not support LiveData with suspended functions.
    // LiveData already works on a background thread and should be used directly without using coroutines


    //Alerts
    @Insert(onConflict = OnConflictStrategy.REPLACE)  //if some data is same/conflict, it'll be replace with new data
    fun insertAlert(alertsModel: AlertsModel)


    @Delete
    fun deleteAlert(alertsModel: AlertsModel)

    @Query("SELECT * from alerts")
    fun getAllAlerts() : LiveData<List<AlertsModel>>

}