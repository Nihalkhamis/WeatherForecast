package com.kotlin.weatherforecast


import com.kotlin.weatherforecast.model.AlertsModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.annotation.RequiresApi
import android.os.Build
import com.kotlin.weatherforecast.WeatherAlertsWorkManager
import android.content.Intent
import com.kotlin.weatherforecast.MainActivity
import android.app.PendingIntent
import com.kotlin.weatherforecast.R
import android.media.RingtoneManager
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.kotlin.weatherforecast.db.AppDataBase
import com.kotlin.weatherforecast.db.ConcreteLocalSource
import com.kotlin.weatherforecast.home.alert.viewmodel.AlertViewModel
import com.kotlin.weatherforecast.home.alert.viewmodel.AlertsViewModelFactory
import com.kotlin.weatherforecast.model.WeatherRepository
import com.kotlin.weatherforecast.network.ApiClient
import com.kotlin.weatherforecast.utils.Constants
import com.kotlin.weatherforecast.utils.MyPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class WeatherAlertsWorkManager(var context: Context, workerParams: WorkerParameters) : Worker(
    context, workerParams
) {
    lateinit var preference: MyPreference
    lateinit var weatherRepository: WeatherRepository


    var alerts: ArrayList<AlertsModel>? = null
    var position = 0
    private fun parseJSON(string: String?) {
        val gson = Gson()
        val type = object : TypeToken<List<AlertsModel?>?>() {}.type
        alerts = gson.fromJson(string, type)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val data = inputData
        Log.d(TAG, "doWork:Time Now From Data" + data.getInt("time_nw", 0))
        val now = LocalDateTime.now()
        Log.d(TAG, "doWork:Time Now " + now.minute + now.hour)
        if (data.getInt("time_nw", 0) == now.minute + now.hour) return Result.success()
        Log.d(TAG, "After if")


        //current time // -> 1010101
        // current time (data.getlong("current_tim"))///
        parseJSON(data.getString(Constants.ALERT_LIST))
        position = data.getInt(Constants.ALERT_POSITION, 0)
        Log.d(TAG, "doWork: 70$alerts")
        Log.d(TAG, "doWork: 70" + alerts!!.size)
        Log.d(TAG, "doWork: 70" + alerts!![position])
        Log.d(TAG, "doWork: 60$position")

//        notification(context, medicines.get(position));
        fetchData()
        //sendNotificationDialog(medicines.get(position));
        setAlarm(alerts)
        return Result.success()
    }

    fun showNotification(context: Context, title: String?, message: String?, reqCode: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val CHANNEL_ID = "10" // The id of the channel.
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.weather_ic)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Channel Name" // The user-visible name of the channel.
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(mChannel)
        }
        notificationManager.notify(
            reqCode,
            notificationBuilder.build()
        ) // 0 is the request code, it should be unique id
        Log.d("showNotification", "showNotification: $reqCode")
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun setAlarm(alerts: ArrayList<AlertsModel>?) {
        WorkManager.getInstance().cancelAllWorkByTag("alarm")
        if (alerts != null && !alerts.isEmpty()) {
            val gson = Gson()
            val medListString = gson.toJson(alerts)
            val timeNow = LocalDateTime.now()
            var timeAt = LocalDate.now().atTime(
                Integer.valueOf(alerts[0].time.split(":").toTypedArray()[0].trim { it <= ' ' }),
                Integer.valueOf(
                    alerts[0].time.split(":").toTypedArray()[1].trim { it <= ' ' })
            ) //hours minutes
            var duration = Duration.between(timeNow, timeAt)
            var position = 0
            for (i in alerts.indices) {
                timeAt = LocalDate.now().atTime(
                    Integer.valueOf(alerts[i].time.split(":").toTypedArray()[0].trim { it <= ' ' }),
                    Integer.valueOf(
                        alerts[i].time.split(":").toTypedArray()[1].trim { it <= ' ' })
                )
                //check today between start date and end date
                if (Calendar.getInstance().time.time >= alerts[i].fromDateMillis && Calendar.getInstance().time.time <= alerts[i].toDateMillis) {
                    if (timeAt.isAfter(timeNow) && duration.abs().toMillis() > Duration.between(
                            timeNow,
                            timeAt
                        ).toMillis()
                    ) {
                        duration = Duration.between(timeNow, timeAt)
                        position = i
                        Log.d(TAG, "setAlarm: --> $timeAt")
                        Log.d(TAG, "setAlarm: --> position:$i")
                        Log.d(TAG, "setAlarm: --> " + "medicine:" + alerts[i])
                    }
                }
            }
            val data = Data.Builder()
                .putString(Constants.ALERT_LIST, medListString)
                .putInt(Constants.ALERT_POSITION, position)
                .putInt("time_nw", timeNow.minute + timeNow.hour)
                .build()


            //set one time work request
            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
                WeatherAlertsWorkManager::class.java
            )
                .setInitialDelay(duration)
                .setInputData(data)
                .addTag("alarm")
                .build()
            WorkManager.getInstance().enqueue(oneTimeWorkRequest)
        }
    }

    private fun fetchData() {
        //call api
        preference = MyPreference.getInstance(context)!!

       weatherRepository =  WeatherRepository.getRepoInstance(
           ApiClient.getClientInstance()!!,
           ConcreteLocalSource(context),
          context
       )

         CoroutineScope(Dispatchers.IO).launch {
            val response = weatherRepository.getWeatherDetails(preference.getData(Constants.LAT)!!, preference.getData(Constants.LONG)!!)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("TAG", "getWeatherDetails: ${response.raw().request().url()}")
                    val desc = response.body()?.alerts?.get(0)?.description
                    Log.d(TAG, "fetchData: ALERTS--->$desc")

                    if (desc != null){
                        showNotification(context,"Weather Alerts", desc,100)
                    }
                    else{
                        showNotification(context,"Weather Alerts", "No Weather Alerts",100)
                    }

                } else {
                    Log.d(TAG, "fetchData: ${response.message()}")
                }
            }
        }

//        showNotification(context, alerts!![position].id.toString() + "", "Weather Alerts", 100)
    }

    companion object {
        private const val TAG = "MEDREMINDER"
        const val HOUR = "HOUR"
        const val MIN = "MIN"
    }
}