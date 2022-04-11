package com.kotlin.weatherforecast.network

import com.kotlin.weatherforecast.model.WeatherResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("onecall")
    suspend fun getWeatherDetails(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String="e4904c33c70746e250d2ec358aca6a64",
        @Query("units") units: String,
        @Query("lang") lang: String
    ): Response<WeatherResponseModel>
}