package com.kotlin.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertsModel(
    @PrimaryKey
    val id: Long,
    val fromDateMillis : Long,
    val toDateMillis : Long,
    val time : String,
    val fromDate : String,
    val toDate : String
)