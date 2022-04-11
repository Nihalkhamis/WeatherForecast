package com.kotlin.weatherforecast

interface AlertDialogCommunicator {
    fun alarmSaved(fromDateMillis : Long, toDateMillis : Long, time : String, fromDate : String, toDate : String)
}