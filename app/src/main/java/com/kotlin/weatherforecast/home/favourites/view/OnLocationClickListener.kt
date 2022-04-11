package com.kotlin.weatherforecast.home.favourites.view

import com.kotlin.weatherforecast.model.FavLocationsModel


interface OnLocationClickListener {
    fun onClick(favLocationsModel: FavLocationsModel)
    fun onDeleteClicked(favLocationsModel: FavLocationsModel)
}