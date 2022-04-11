package com.kotlin.weatherforecast.home.alert.view

import com.kotlin.weatherforecast.model.AlertsModel

interface OnDeleteClickListener {
    fun onDeleteClicked(alertsModel: AlertsModel)
}