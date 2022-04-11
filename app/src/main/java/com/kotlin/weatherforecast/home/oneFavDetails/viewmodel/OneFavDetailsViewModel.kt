package com.kotlin.weatherforecast.home.oneFavDetails.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kotlin.weatherforecast.model.WeatherRepositoryInterface
import com.kotlin.weatherforecast.model.WeatherResponseModel
import com.kotlin.weatherforecast.utils.Constants
import com.kotlin.weatherforecast.utils.MyPreference
import kotlinx.coroutines.*

class OneFavDetailsViewModel(private val repo : WeatherRepositoryInterface, private var context: Context) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val weatherList = MutableLiveData<WeatherResponseModel>()
    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

//    init {
//        getWeatherDetails()
//    }


    val liveDataWeatherList : LiveData<WeatherResponseModel> = weatherList

    fun getWeatherDetails(lat : String, long : String) {

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = repo.getWeatherDetails(lat,long)
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