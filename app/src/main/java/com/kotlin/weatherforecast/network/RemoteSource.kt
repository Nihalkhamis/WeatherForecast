package com.kotlin.weatherforecast.network

import com.kotlin.weatherforecast.model.WeatherResponseModel
import retrofit2.Response

interface RemoteSource {
    suspend fun getWeatherDetails(lat : String, long : String, units : String, language : String) : Response<WeatherResponseModel>

}