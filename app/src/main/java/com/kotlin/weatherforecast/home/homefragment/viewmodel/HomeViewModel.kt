package com.kotlin.weatherforecast.home.homefragment.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kotlin.weatherforecast.R
import com.kotlin.weatherforecast.model.FavLocationsModel
import com.kotlin.weatherforecast.model.WeatherRepository
import com.kotlin.weatherforecast.model.WeatherRepositoryInterface
import com.kotlin.weatherforecast.model.WeatherResponseModel
import com.kotlin.weatherforecast.utils.Constants
import com.kotlin.weatherforecast.utils.MyPreference
import kotlinx.coroutines.*

class HomeViewModel(private val repo : WeatherRepositoryInterface,private var context: Context) : ViewModel() {

    lateinit var preference: MyPreference


    val errorMessage = MutableLiveData<String>()
    val weatherList = MutableLiveData<WeatherResponseModel>()
    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    init {

    }


    val liveDataWeatherList : LiveData<WeatherResponseModel> = weatherList

    fun getWeatherDetails(context: Context) {
        preference = MyPreference.getInstance(context)!!


        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = repo.getWeatherDetails(preference.getData(Constants.LAT)!!, preference.getData(Constants.LONG)!!)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("TAG", "getWeatherDetails: ${response.raw().request().url()}")
                    weatherList.postValue(response.body())
                    loading.value = false
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}