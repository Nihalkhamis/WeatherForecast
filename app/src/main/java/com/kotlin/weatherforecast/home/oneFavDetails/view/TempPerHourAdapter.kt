package com.kotlin.weatherforecast.home.oneFavDetails.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.weatherforecast.R
import com.kotlin.weatherforecast.model.WeatherResponseModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TempPerHourAdapter(
    val context: Context, private val weathers: ArrayList<WeatherResponseModel.Hourly>
) : RecyclerView.Adapter<TempPerHourAdapter.TempPerHourHolder>() {


    fun setWeatherPerHour(weatherList: List<WeatherResponseModel.Hourly>) {
        this.weathers.apply {
            clear()
            addAll(weatherList)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TempPerHourHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.temp_hour_day_single, parent, false)
        return TempPerHourHolder(view)
    }

    override fun onBindViewHolder(holder: TempPerHourHolder, position: Int) {

       var time = convertToTime(weathers[position].dt)

        holder.time_txt.text = time
       // holder.time_zone_txt.text = weathers[position].timeZone
        holder.weather_temp.text = weathers[position].temp.toString()
       // holder.temp_format_txt.text = weathers[position].tempFormat
        //holder.weather_img.setImageResource(weathers[position].weatherImg)

        var url = "http://openweathermap.org/img/wn/${weathers[position].weather[0].icon}.png"
        Log.d("TAG", "onBindViewHolder: icon in per HOUR--->$url")
        Glide.with(context).load(url).into(holder.weather_img)
    }

    private fun convertToTime(dt : Double) : String {
        val date = Date((dt * 1000).toLong())
        val format = SimpleDateFormat("h:mm a", Locale.ENGLISH)
        return format.format(date)
    }

    override fun getItemCount(): Int {
       return weathers.size
    }

    inner class TempPerHourHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weather_img: ImageView = itemView.findViewById(R.id.weather_img)
        val time_txt: TextView = itemView.findViewById(R.id.time_txt)
       // val time_zone_txt: TextView = itemView.findViewById(R.id.time_zone_txt)
        val weather_temp: TextView = itemView.findViewById(R.id.weather_temp)

    }
}