package com.kotlin.weatherforecast

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment


class MapDialog(val mapDialogCommunicator: MapDialogCommunicator) : DialogFragment() {

    lateinit var save_btn : AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner);

        return inflater.inflate(R.layout.map_dialog, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        save_btn = view.findViewById(R.id.save_btn)

        save_btn.setOnClickListener {
            Log.d("TAG", "onViewCreated: ->>> Dialog 35")
            // Toast.makeText(context,"OK",Toast.LENGTH_SHORT).show()
            mapDialogCommunicator.pickedLocation()
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
        val window = dialog!!.window
        window!!.setGravity(Gravity.BOTTOM)
//        val wlp: WindowManager.LayoutParams = dialog!!.window!!.attributes
//        wlp.gravity = Gravity.BOTTOM

        //dialog!!.setCancelable(false)
    }
}