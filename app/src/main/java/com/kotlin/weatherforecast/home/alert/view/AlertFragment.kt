package com.kotlin.weatherforecast.home.alert.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.gson.Gson
import com.kotlin.weatherforecast.AlertDialog
import com.kotlin.weatherforecast.AlertDialogCommunicator
import com.kotlin.weatherforecast.WeatherAlertsWorkManager
import com.kotlin.weatherforecast.databinding.FragmentAlertBinding
import com.kotlin.weatherforecast.db.AppDataBase
import com.kotlin.weatherforecast.db.ConcreteLocalSource
import com.kotlin.weatherforecast.home.alert.viewmodel.AlertViewModel
import com.kotlin.weatherforecast.home.alert.viewmodel.AlertsViewModelFactory
import com.kotlin.weatherforecast.model.AlertsModel
import com.kotlin.weatherforecast.model.WeatherRepository
import com.kotlin.weatherforecast.network.ApiClient
import com.kotlin.weatherforecast.utils.Constants
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class AlertFragment : Fragment(), AlertDialogCommunicator, OnDeleteClickListener {

    private var _binding: FragmentAlertBinding? = null
    lateinit var alertDialog: AlertDialog

    lateinit var alertsAdapter: AlertsAdapter
    lateinit var alertsLayoutManager: LinearLayoutManager

    lateinit var vmFactory: AlertsViewModelFactory
    lateinit var alertViewModel: AlertViewModel

    lateinit var appDataBase: AppDataBase

    //var isAlarmSet = false


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        vmFactory = AlertsViewModelFactory(
            WeatherRepository.getRepoInstance(
                ApiClient.getClientInstance()!!,
                ConcreteLocalSource(requireContext()),
                requireContext()
            ), requireContext()
        )

        alertViewModel = ViewModelProvider(this, vmFactory).get(AlertViewModel::class.java)

        appDataBase = AppDataBase.getInstance(requireContext())

        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        val root: View = binding.root

        alertDialog = AlertDialog(this)

        binding.addAlertFloatBtn.setOnClickListener {
            alertDialog.show(requireFragmentManager(), "MyCustomFragment")
        }


        alertsLayoutManager = LinearLayoutManager(requireContext())
        alertsLayoutManager.orientation = RecyclerView.VERTICAL
        alertsAdapter = AlertsAdapter(requireContext(), this, ArrayList())
        binding.alertsRv.adapter = alertsAdapter
        binding.alertsRv.layoutManager = alertsLayoutManager

        alertViewModel.getAlerts().observe(requireActivity()) { alerts ->
            Log.i("TAG", "Observation: ${alerts}")
            if (alerts != null)
                alertsAdapter.setAlert(alerts)

                setAlert(alerts)

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun alarmSaved(
        fromDateMillis: Long,
        toDateMillis: Long,
        time: String,
        fromDate: String,
        toDate: String
    ) {
        alertViewModel.insertAlert(
            AlertsModel(
                id = fromDateMillis + toDateMillis, fromDateMillis = fromDateMillis,
                toDateMillis = toDateMillis, time = time, fromDate = fromDate, toDate = toDate
            )
        )
        alertDialog.dismiss()
    }

    override fun onDeleteClicked(alertsModel: AlertsModel) {
        alertViewModel.deleteAlert(alertsModel)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun setAlert(alerts: List<AlertsModel>?) {

      //  isAlarmSet = true

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
                Log.d("TAG10", "setAlert: CURRENT TIME-->${Calendar.getInstance().time.time}")
                Log.d("TAG10", "setAlert: FROM TIME-->${alerts[i].fromDateMillis}")
                Log.d("TAG10", "setAlert: TO TIME-->${alerts[i].toDateMillis}")
                Log.d("TAG10", "setAlert: BOOLEAN-->${Calendar.getInstance().time.time >= alerts[i].fromDateMillis && Calendar.getInstance().time.time <= alerts[i].toDateMillis}")
                if (Calendar.getInstance().time.time >= alerts[i].fromDateMillis && Calendar.getInstance().time.time <= alerts[i].toDateMillis) {
                    if (timeAt.isAfter(timeNow) && duration.abs().toMillis() > Duration.between(
                            timeNow,
                            timeAt
                        ).toMillis()
                    ) {
                        duration = Duration.between(timeNow, timeAt)
                        position = i
                        Log.d("TAG", "setAlarm: --> $timeAt")
                        Log.d("TAG", "setAlarm: --> position:$i")
                        Log.d(
                            "TAG",
                            "setAlarm: --> " + "medicine:" + alerts[i]
                        )
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

}
