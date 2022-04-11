package com.kotlin.weatherforecast

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class AlertDialog(val alertDialogCommunicator: AlertDialogCommunicator) : DialogFragment() {


    lateinit var from_btn: AppCompatButton
    lateinit var to_btn: AppCompatButton
    lateinit var save_btn: AppCompatButton
    lateinit var notificationTime_btn: AppCompatButton

    lateinit var fromDate_txt: TextView
    lateinit var toDate_txt: TextView

    lateinit var fromTime_txt: TextView
    lateinit var toTime_txt: TextView

   var fromDateMillis by Delegates.notNull<Long>();
   var toDateMillis by Delegates.notNull<Long>();
    lateinit var time : String;


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner);

        return inflater.inflate(R.layout.alerts_dialog, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        from_btn = view.findViewById(R.id.from_btn);
        to_btn = view.findViewById(R.id.to_btn);
        save_btn = view.findViewById(R.id.save_btn);
        notificationTime_btn = view.findViewById(R.id.notificationTime_btn);

        fromDate_txt = view.findViewById(R.id.fromDate_txt);
        toDate_txt = view.findViewById(R.id.toDate_txt);

        fromTime_txt = view.findViewById(R.id.fromTime_txt);
        toTime_txt = view.findViewById(R.id.toTime_txt);

        from_btn.setOnClickListener {
            val calendar = Calendar.getInstance()
            var mYear = calendar[Calendar.YEAR]
            var mMonth = calendar[Calendar.MONTH]
            var mDay = calendar[Calendar.DAY_OF_MONTH]

            //show dialog
            val datePickerDialog = DatePickerDialog(
                requireContext()!!,
                R.style.DialogTheme,
                { view, year, month, dayOfMonth ->

                    val format = SimpleDateFormat("EEE, d MMM", Locale.ENGLISH)
                    calendar.set(year, month, dayOfMonth);
                    val strDate = format.format(calendar.time)
                    fromDate_txt.text = strDate.toString()

                    val date: Date = format.parse(strDate.toString())// without year
                    val calendar = Calendar.getInstance()
                    calendar.set(year,month,dayOfMonth)
                    fromDateMillis = calendar.timeInMillis
                    Log.d("ALERT", "onViewCreated: $fromDateMillis")

                },
                mYear,
                mMonth,
                mDay
            )
            datePickerDialog.show()
        }

        to_btn.setOnClickListener {
            val calendar = Calendar.getInstance()
            var mYear = calendar[Calendar.YEAR]
            var mMonth = calendar[Calendar.MONTH]
            var mDay = calendar[Calendar.DAY_OF_MONTH]

            //show dialog
            val datePickerDialog = DatePickerDialog(
                requireContext()!!,
                R.style.DialogTheme,
                { view, year, month, dayOfMonth ->
                    val format = SimpleDateFormat("EEE, d MMM", Locale.ENGLISH)
                    calendar.set(year, month, dayOfMonth);
                    val strDate = format.format(calendar.time)
                    toDate_txt.text = strDate.toString()

                    val date: Date = format.parse(strDate.toString())
                    val calendar = Calendar.getInstance()
                    calendar.set(year,month,dayOfMonth)
                    toDateMillis = calendar.timeInMillis
                    Log.d("ALERT", "onViewCreated: $toDateMillis")



                },
                mYear,
                mMonth,
                mDay
            )
            datePickerDialog.show()
        }



        notificationTime_btn.setOnClickListener {
            val mTimePicker: TimePickerDialog
            val calendar = Calendar.getInstance()
            val mHour = calendar[Calendar.HOUR_OF_DAY]
            val mMinute = calendar[Calendar.MINUTE]

            mTimePicker =
                TimePickerDialog(requireContext(), R.style.DialogTheme, object : TimePickerDialog.OnTimeSetListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

                        time = "$hourOfDay:$minute"

                        fromTime_txt.text = "${hourOfDay%12} : $minute "+ if (hourOfDay >= 12) "PM" else "AM"
                        toTime_txt.text = "${hourOfDay%12} : $minute "+ if (hourOfDay >= 12) "PM" else "AM"
                    }
                }, mHour, mMinute, false)

            mTimePicker.show()
        }


        save_btn.setOnClickListener {
            if ((!fromDate_txt.equals("")) && (!toDate_txt.equals("") && ((!fromTime_txt.equals("")) || (!toTime_txt.equals(""))))){

                Log.d("TAG", "onViewCreated: FROM DATE MILLIS: $fromDateMillis")
                Log.d("TAG", "onViewCreated: TO DATE MILLIS: $toDateMillis")
                Log.d("TAG", "onTimeSet: HOUR DAY: $time")
                alertDialogCommunicator.alarmSaved(fromDateMillis,toDateMillis,time,fromDate = fromDate_txt.text.toString(),
                    toDate = toDate_txt.text.toString())


            }
            else{
                Toast.makeText(requireContext(),"Enter date and time first !",Toast.LENGTH_SHORT)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val window = dialog!!.window
    }
}