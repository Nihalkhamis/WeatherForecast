package com.kotlin.weatherforecast.home.favourites.view

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
import com.kotlin.weatherforecast.model.FavLocationsModel
import com.kotlin.weatherforecast.model.WeatherResponseModel
import java.text.SimpleDateFormat
import java.util.*


class FavLocationAdapter(
    val context: Context,
    private var favLocations: ArrayList<FavLocationsModel>,
    val onLocationClickListener: OnLocationClickListener,

    ) : RecyclerView.Adapter<FavLocationAdapter.FavLocationHolder>() {

    fun setFavLocations(favLocationsList: List<FavLocationsModel>) {
        this.favLocations.apply {
            //weathers = weatherList
            clear()
            addAll(favLocationsList)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavLocationAdapter.FavLocationHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.location_fav_single, parent, false)
        return FavLocationHolder(view)
    }

    override fun onBindViewHolder(holder: FavLocationAdapter.FavLocationHolder, position: Int) {
        holder.location_txt.text = favLocations[position].location
        holder.favLocation_card.setOnClickListener {
            onLocationClickListener.onClick(favLocations[position])
        }
        holder.delete_ic.setOnClickListener {
            holder.delete_btn.visibility = View.VISIBLE
            holder.delete_btn.setOnClickListener {
                onLocationClickListener.onDeleteClicked(favLocations[position])
                holder.delete_btn.visibility = View.GONE

            }
        }

    }


    override fun getItemCount(): Int {
        return favLocations.size
    }

    inner class FavLocationHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val delete_ic: ImageView = itemView.findViewById(R.id.delete_ic)
        val location_txt: TextView = itemView.findViewById(R.id.location_txt)
        val favLocation_card: ConstraintLayout = itemView.findViewById(R.id.favLocation_card)
        val delete_btn: Button = itemView.findViewById(R.id.delete_btn)


    }
}