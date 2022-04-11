package com.kotlin.weatherforecast.network

import android.util.Log
import com.kotlin.weatherforecast.model.WeatherResponseModel
import com.kotlin.weatherforecast.utils.MyPreference
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiClient private constructor(): RemoteSource{


    companion object {
        private var instance: ApiClient? = null
        fun getClientInstance(): ApiClient? {
            return instance?: ApiClient()
        }
    }

    object RetrofitHelper {
        val baseUrl = "https://api.openweathermap.org/data/2.5/"



        fun getInstance(): Retrofit {
            return Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
//                .client(setInterceptor())
                // we need to add converter factory to
                // convert JSON object to Java object
                .build()
        }
    }

    override suspend fun getWeatherDetails(lat : String, long : String, units : String, language : String): Response<WeatherResponseModel> {
        val weatherService = RetrofitHelper.getInstance().create(ApiInterface::class.java)
       // return weatherService.getWeatherDetails("29.33","33.33")
        Log.d("TAG", "getWeatherDetails: LAT CLIENT $lat")
        Log.d("TAG", "getWeatherDetails: LONG CLIENT $long")
        return weatherService.getWeatherDetails(lat,long,units = units, lang = language)
    }
}