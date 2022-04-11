package com.kotlin.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favourites")
data class FavLocationsModel(
    @PrimaryKey
    val id: String,
    val lat: Double,
    val lon: Double,
    val location: String
)