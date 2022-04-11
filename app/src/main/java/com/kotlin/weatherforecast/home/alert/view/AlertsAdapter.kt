package com.kotlin.weatherforecast.home.alert.view

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kotlin.weatherforecast.R
import com.kotlin.weatherforecast.model.AlertsModel
import com.kotlin.weatherforecast.model.FavLocationsModel
import com.kotlin.weatherforecast.model.WeatherResponseModel
import java.text.SimpleDateFormat
import java.util.*


class AlertsAdapter(
    val context: Context,
    var onDeleteClickListener: OnDeleteClickListener,
    private var alerts: ArrayList<AlertsModel>
    ) : RecyclerView.Adapter<AlertsAdapter.AlertsHolder>() {

    fun setAlert(alertsList: List<AlertsModel>) {
        this.alerts.apply {
            //weathers = weatherList
            clear()
            addAll(alertsList)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlertsAdapter.AlertsHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.alert_single, parent, false)
        return AlertsHolder(view)
    }

    override fun onBindViewHolder(holder: AlertsAdapter.AlertsHolder, position: Int) {
        holder.fromDate_txt.text = alerts[position].fromDate
        holder.toDate_txt.text = alerts[position].toDate
        holder.time_txt.text = alerts[position].time

        holder.delete_ic.setOnClickListener {
            holder.delete_btn.visibility = View.VISIBLE
            holder.delete_btn.setOnClickListener {
                onDeleteClickListener.onDeleteClicked(alerts[position])
                holder.delete_btn.visibility = View.GONE

            }
        }

    }


    override fun getItemCount(): Int {
        return alerts.size
    }

    inner class AlertsHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val delete_ic: ImageView = itemView.findViewById(R.id.delete_ic)
        val fromDate_txt: TextView = itemView.findViewById(R.id.fromDate_txt)
        val toDate_txt: TextView = itemView.findViewById(R.id.toDate_txt)
        val time_txt: TextView = itemView.findViewById(R.id.time_txt)
        val delete_btn: Button = itemView.findViewById(R.id.delete_btn)


    }
}