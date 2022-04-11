package com.kotlin.weatherforecast.home.oneFavDetails.view

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kotlin.weatherforecast.R
import com.kotlin.weatherforecast.model.WeatherResponseModel
import java.text.SimpleDateFormat
import java.util.*


class TempDayAdapter(
    val context: Context, private var weathers: ArrayList<WeatherResponseModel.Daily>
) : RecyclerView.Adapter<TempDayAdapter.TempDayHolder>() {

    fun setWeatherPerDay(weatherList: List<WeatherResponseModel.Daily>) {
        this.weathers.apply {
            //weathers = weatherList
            clear()
            addAll(weatherList)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TempDayAdapter.TempDayHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.temp_day_single, parent, false)
        return TempDayHolder(view)
    }

    override fun onBindViewHolder(holder: TempDayAdapter.TempDayHolder, position: Int) {
//        var dt = weathers[position].dt
//
//        //val jsonObject2 = JSONObject(dt)
//
//
//        var day = Date(dt.times(1000));
//        val sdf = SimpleDateFormat("EEEE")
//        Log.d("TAG", "onBindViewHolder: DATE-----> ${sdf.format(day)}")

        var day = convertToDate(weathers[position].dt)

        holder.day_txt.text =day
        holder.weather_status_txt.text = weathers[position].weather[0].description
        holder.toTemp_txt.text = weathers[position].temp.min.toString()
        holder.fromTemp_txt.text = weathers[position].temp.max.toString()
        //holder.tempFormat_txt.text = weathers[position].tempFormat
       // holder.weather_img.setImageResource(weathers[position].weatherImg)



//        console.log(day.toUTCString()) // 'Fri, 15 Jan 2021 04:32:29 GMT'
//        console.log(day.toDateString()) // 'Fri Jan 15 2021'
//        console.log(day.toISOString()) // '2021-01-15T04:32:29.000Z'
//        console.log(day.toString()) // 'Fri Jan 15 2021 07:32:29 GMT+0300 (GMT+03:00)'

        var url = "http://openweathermap.org/img/wn/${weathers[position].weather[0].icon}@2x.png"
        Log.d("TAG", "onBindViewHolder: icon in per DAY--->$url")
//        Glide.with(context).load(url).apply(
//            RequestOptions().override(35, 35).placeholder(R.drawable.ic_launcher_background)
//        ).dontAnimate().into(holder.weather_img)

        Glide.with(context).load(url).into(holder.weather_img)


    }

    private fun convertToDate(dt : Double) : String {
        val date = Date((dt * 1000).toLong())
        val format = SimpleDateFormat("EEE", Locale.ENGLISH)
        return format.format(date)
    }

    override fun getItemCount(): Int {
        return weathers.size
    }

    inner class TempDayHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weather_img: ImageView = itemView.findViewById(R.id.weather_img)
        val day_txt: TextView = itemView.findViewById(R.id.day_txt)
        val weather_status_txt: TextView = itemView.findViewById(R.id.weather_status_txt)
        val fromTemp_txt: TextView = itemView.findViewById(R.id.fromTemp_txt)
        val toTemp_txt: TextView = itemView.findViewById(R.id.toTemp_txt)

    }
}