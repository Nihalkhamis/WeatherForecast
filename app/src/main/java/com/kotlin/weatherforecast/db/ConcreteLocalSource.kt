package com.kotlin.weatherforecast.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.kotlin.weatherforecast.model.AlertsModel
import com.kotlin.weatherforecast.model.FavLocationsModel

class ConcreteLocalSource(var context: Context) : LocalSource{

    private val dao: WeatherDao

    init {
        val db: AppDataBase = AppDataBase.getInstance(context)
        dao  = db.weatherDao()

    }


    //Fav locations
    override fun getAllLocations(): LiveData<List<FavLocationsModel>> = dao.getAllLocations()

    override  fun delete(favLocationsModel: FavLocationsModel) = dao.delete(favLocationsModel)

    override  fun insert(favLocationsModel: FavLocationsModel) = dao.insert(favLocationsModel)





    //Alerts
    override fun deleteAlert(alertsModel: AlertsModel) = dao.deleteAlert(alertsModel)

    override fun insertAlert(alertsModel: AlertsModel) = dao.insertAlert(alertsModel)

    override fun getAllAlerts(): LiveData<List<AlertsModel>> = dao.getAllAlerts()
}