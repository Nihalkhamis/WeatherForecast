package com.kotlin.weatherforecast.home.settings.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.*
import com.kotlin.weatherforecast.MainActivity
import com.kotlin.weatherforecast.MapsActivity
import com.kotlin.weatherforecast.R
import com.kotlin.weatherforecast.databinding.FragmentHomeBinding
import com.kotlin.weatherforecast.databinding.SettingsFragmentBinding
import com.kotlin.weatherforecast.home.HomeActivity
import com.kotlin.weatherforecast.home.settings.viewmodel.SettingsViewModel
import com.kotlin.weatherforecast.utils.Constants
import com.kotlin.weatherforecast.utils.MyPreference

class SettingsFragment : Fragment() {

    private var _binding: SettingsFragmentBinding? = null

    private val binding get() = _binding!!

    lateinit var preference: MyPreference

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val LOCATION_PERMISSION_ID = 10


    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.settings_fragment, container, false)

        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())


        preference = MyPreference.getInstance(requireContext())!!


        return root;
    }

    fun restartActivity() {
        requireActivity().finishAffinity()
        requireContext().startActivity(Intent(requireContext(), HomeActivity::class.java))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        binding.arabicRb.setOnCheckedChangeListener { p0, p1 ->
            if (p1) {
                preference.saveData(Constants.LANGUAGE, "ar")
                restartActivity()
            }
        }
        binding.englishRb.setOnCheckedChangeListener { v, x ->
            if (x) {
                preference.saveData(Constants.LANGUAGE, "en")
                restartActivity()
            }
        }



        binding.celsiusRb.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                preference.saveData(Constants.TEMPERATURE, "metric")
                binding.meterSecRb.isChecked = true
            }

        }
        binding.fahrenheitRb.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                preference.saveData(Constants.TEMPERATURE, "imperial")
                binding.mileHRb.isChecked = true

            }
        }
        binding.kelvinRb.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                preference.saveData(Constants.TEMPERATURE, "")
                binding.meterSecRb.isChecked = true
            }
        }




        binding.enableRb.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                preference.saveData(Constants.NOTIFICATIONS, "enable")

            }
        }
        binding.disableRb.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                preference.saveData(Constants.NOTIFICATIONS, "disable")

            }
        }

        binding.mapRb.setOnClickListener {
                val intent = Intent(getActivity(), MapsActivity::class.java)
                getActivity()?.startActivity(intent)

        }
        binding.gpsRb.setOnClickListener {
                getLastLocation()
        }


    }

    private fun setUpView() {
        if (preference.getData(Constants.LANGUAGE) == "ar") {
            binding.arabicRb.isChecked = true
        } else {
            binding.englishRb.isChecked = true
        }
        if (preference.getData(Constants.TEMPERATURE) == "imperial") {
            binding.mileHRb.isChecked = true
            binding.fahrenheitRb.isChecked = true
        } else if (preference.getData(Constants.TEMPERATURE) == "metric") {
            binding.celsiusRb.isChecked = true
            binding.meterSecRb.isChecked = true
        } else {
            binding.meterSecRb.isChecked = true
            binding.kelvinRb.isChecked = true

        }
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (locationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val mLocation = task.result
                    requestNewLocationData()
                }
            } else {
                displayPromptForEnablingGPS(requireActivity())
            }
        } else {
            requestLocationPermission()
        }
    }


    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun locationEnabled(): Boolean {
        val locationManager =
            activity?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                requestLocationPermission()
            }
        }
    }

    fun displayPromptForEnablingGPS(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        val action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        val message = "Do you want open GPS setting?"
        builder.setMessage(message)
            .setPositiveButton(
                "OK"
            ) { d, id ->
                activity.startActivity(Intent(action))
                d.dismiss()
            }
            .setNegativeButton(
                "Cancel"
            ) { d, id -> d.cancel() }
        builder.create().show()
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        @SuppressLint("RestrictedApi") val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.equals(null)) {
                    return
                }
                val mLocation = locationResult.lastLocation

                saveLatLongToShared(mLocation.latitude.toString(), mLocation.longitude.toString())

                Log.d("TAG", "onLocationResult: LAT ${mLocation.latitude}")
                Log.d("TAG", "onLocationResult: LONG ${mLocation.longitude}")

                //startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                val intent = Intent(activity, HomeActivity::class.java)
                activity?.startActivity(intent)

            }
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private fun saveLatLongToShared(lat: String, long: String) {
        preference.saveData(Constants.LAT, lat)
        preference.saveData(Constants.LONG, long)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}