package com.kotlin.weatherforecast.model


import com.google.gson.annotations.SerializedName

data class WeatherResponseModel(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val alerts : ArrayList<Alerts>,
    val lat: Double,
    val lon: Double,
    val minutely: List<Minutely>,
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Double
) {
    data class Current(
        val clouds: Double,
        @SerializedName("dew_poDouble")
        val dewPoDouble: Double,
        val dt: Double,
        @SerializedName("feels_like")
        val feelsLike: Double,
        val humidity: Double,
        val pressure: Double,
        val sunrise: Double,
        val sunset: Double,
        val temp: Double,
        val uvi: Double,
        val visibility: Double,
        val weather: List<Weather>,
        @SerializedName("wind_deg")
        val windDeg: Double,
        @SerializedName("wind_gust")
        val windGust: Double,
        @SerializedName("wind_speed")
        val windSpeed: Double
    ) {
        data class Weather(
            val description: String,
            val icon: String,
            val id: Double,
            val main: String
        )
    }

    data class Daily(
        val clouds: Double,
        @SerializedName("dew_poDouble")
        val dewPoDouble: Double,
        val dt: Double,
        @SerializedName("feels_like")
        val feelsLike: FeelsLike,
        val humidity: Double,
        @SerializedName("moon_phase")
        val moonPhase: Double,
        val moonrise: Double,
        val moonset: Double,
        val pop: Double,
        val pressure: Double,
        val sunrise: Double,
        val sunset: Double,
        val temp: Temp,
        val uvi: Double,
        val weather: List<Weather>,
        @SerializedName("wind_deg")
        val windDeg: Double,
        @SerializedName("wind_gust")
        val windGust: Double,
        @SerializedName("wind_speed")
        val windSpeed: Double
    ) {
        data class FeelsLike(
            val day: Double,
            val eve: Double,
            val morn: Double,
            val night: Double
        )

        data class Temp(
            val day: Double,
            val eve: Double,
            val max: Double,
            val min: Double,
            val morn: Double,
            val night: Double
        )

        data class Weather(
            val description: String,
            val icon: String,
            val id: Double,
            val main: String
        )
    }

    data class Alerts(
        @SerializedName("sender_name")
        val senderName: String,
        val event: String,
        val start: Long,
        val end: Long,
        val description: String
    )

    data class Hourly(
        val clouds: Double,
        @SerializedName("dew_poDouble")
        val dewPoDouble: Double,
        val dt: Double,
        @SerializedName("feels_like")
        val feelsLike: Double,
        val humidity: Double,
        val pop: Double,
        val pressure: Double,
        val temp: Double,
        val uvi: Double,
        val visibility: Double,
        val weather: List<Weather>,
        @SerializedName("wind_deg")
        val windDeg: Double,
        @SerializedName("wind_gust")
        val windGust: Double,
        @SerializedName("wind_speed")
        val windSpeed: Double
    ) {
        data class Weather(
            val description: String,
            val icon: String,
            val id: Double,
            val main: String
        )
    }

    data class Minutely(
        val dt: Double,
        val precipitation: Double
    )
}