package com.kotlin.weatherforecast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class InitialSetupDialog(val dialogActivityCommunicator: DialogActivityCommunicator) : DialogFragment() {

    lateinit var location_rg: RadioGroup
    lateinit var locationRadioButton: RadioButton
    lateinit var ok_btn: AppCompatButton



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner);

        return inflater.inflate(R.layout.initial_setup_dialog, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        location_rg = view.findViewById(R.id.location_rg)


        ok_btn = view.findViewById(R.id.ok_btn)


        ok_btn.setOnClickListener {
            // Toast.makeText(context,"OK",Toast.LENGTH_SHORT).show()
            var selectedId: Int = location_rg.checkedRadioButtonId
            locationRadioButton = view.findViewById(selectedId)
            Log.d("TAG", "onViewCreated: ${locationRadioButton.text}")

            if (locationRadioButton.text == getString(R.string.GPS)) {
                dialogActivityCommunicator.fetchGpsLocation()
            }
            else{
                dialogActivityCommunicator.fetchMapLocation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog!!.setCancelable(false)
    }
}