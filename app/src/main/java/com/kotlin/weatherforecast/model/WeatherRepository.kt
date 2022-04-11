package com.kotlin.weatherforecast.model

import android.content.Context
import androidx.lifecycle.LiveData
import com.kotlin.weatherforecast.db.LocalSource
import com.kotlin.weatherforecast.network.RemoteSource
import com.kotlin.weatherforecast.utils.Constants
import com.kotlin.weatherforecast.utils.MyPreference
import retrofit2.Response

class WeatherRepository(var remoteSource: RemoteSource,
                        var localSource: LocalSource,
                        var context: Context
) : WeatherRepositoryInterface {

     var preference: MyPreference = MyPreference.getInstance(context)!!



    companion object {
        private var instance: WeatherRepository? = null
        fun getRepoInstance(
            remoteSource: RemoteSource,
            localSource: LocalSource,
            context: Context
        ): WeatherRepository {
            return instance ?: WeatherRepository(remoteSource, localSource, context)
        }
    }

    override suspend fun getWeatherDetails(lat : String, long : String): Response<WeatherResponseModel> = remoteSource.getWeatherDetails(lat,long,
        preference.getData(Constants.TEMPERATURE)!!,
        preference.getData(Constants.LANGUAGE)!!
    )

    //fav locations
    override fun insertLocation(favLocationsModel: FavLocationsModel) = localSource.insert(favLocationsModel)

    override fun deleteLocation(favLocationsModel: FavLocationsModel) = localSource.delete(favLocationsModel)

    override fun getLocations(): LiveData<List<FavLocationsModel>> = localSource.getAllLocations()


    //Alerts
    override fun insertAlert(alertsModel: AlertsModel) = localSource.insertAlert(alertsModel)

    override fun deleteAlert(alertsModel: AlertsModel) = localSource.deleteAlert(alertsModel)

    override fun getAlerts(): LiveData<List<AlertsModel>> = localSource.getAllAlerts()
}