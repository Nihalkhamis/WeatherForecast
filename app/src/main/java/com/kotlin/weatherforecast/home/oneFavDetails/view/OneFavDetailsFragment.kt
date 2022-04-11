package com.kotlin.weatherforecast.home.oneFavDetails.view

import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.kotlin.weatherforecast.R
import com.kotlin.weatherforecast.databinding.FragmentHomeBinding
import com.kotlin.weatherforecast.db.ConcreteLocalSource
import com.kotlin.weatherforecast.home.homefragment.view.TempDayAdapter
import com.kotlin.weatherforecast.home.homefragment.view.TempPerHourAdapter
import com.kotlin.weatherforecast.home.homefragment.viewmodel.HomeViewModel
import com.kotlin.weatherforecast.home.homefragment.viewmodel.HomeViewModelFactory
import com.kotlin.weatherforecast.home.oneFavDetails.viewmodel.OneFavDetailsViewModel
import com.kotlin.weatherforecast.home.oneFavDetails.viewmodel.OneFavDetailsViewModelFactory
import com.kotlin.weatherforecast.model.WeatherRepository
import com.kotlin.weatherforecast.network.ApiClient
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class OneFavDetailsFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null

    lateinit var tempPerHourAdapter: TempPerHourAdapter
    lateinit var tempHourLayoutManager: LinearLayoutManager

    lateinit var tempDayAdapter: TempDayAdapter
    lateinit var tempDayLayoutManager: LinearLayoutManager

    lateinit var vmFactory: OneFavDetailsViewModelFactory
    lateinit var oneFavDetailsViewModel : OneFavDetailsViewModel



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        vmFactory = OneFavDetailsViewModelFactory(
            WeatherRepository.getRepoInstance(
                ApiClient.getClientInstance()!!,
                ConcreteLocalSource(requireContext()),
                requireContext()
            ),requireContext()
        )

        oneFavDetailsViewModel = ViewModelProvider(this,vmFactory).get(OneFavDetailsViewModel::class.java)
        oneFavDetailsViewModel.getWeatherDetails(OneFavDetailsFragmentArgs.fromBundle(requireArguments()).lat,OneFavDetailsFragmentArgs.fromBundle(requireArguments()).long)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tempHourLayoutManager = LinearLayoutManager(requireContext())
        tempHourLayoutManager.orientation = RecyclerView.HORIZONTAL
        tempPerHourAdapter = TempPerHourAdapter(requireContext(), ArrayList())
        binding.tempDayHourRv.adapter = tempPerHourAdapter
        binding.tempDayHourRv.layoutManager = tempHourLayoutManager

        tempDayAdapter = TempDayAdapter(requireContext(), ArrayList())
        tempDayLayoutManager = LinearLayoutManager(requireContext())
        tempDayLayoutManager.orientation = RecyclerView.VERTICAL
        binding.tempDayRv.adapter = tempDayAdapter
        binding.tempDayRv.layoutManager = tempDayLayoutManager


        oneFavDetailsViewModel.liveDataWeatherList.observe(viewLifecycleOwner) {
            Log.d("TAG", "onCreateView: RESPONSE----->$it")
            //binding.govTxt.text = it.timezone
            getAddressFromLocation(LatLng(it.lat,it.lon))
            var fullDay = convertToDate(it.current.dt)
            binding.dateTxt.text = fullDay
            binding.weatherStatusTxt.text = it.current.weather[0].description
            binding.weatherTempTxt.text = it.current.temp.toString()

            var url = "http://openweathermap.org/img/wn/${it.current.weather[0].icon}.png"
            Log.d("TAG", "onBindViewHolder: CURR Weather IMG--->$url")

            Glide.with(requireContext()).load(url).into(binding.weatherImg)

            tempPerHourAdapter.setWeatherPerHour(it.hourly)
            tempDayAdapter.setWeatherPerDay(it.daily)


            binding.pressureLayout.valueTxt.text = it.current.pressure.toString() + getString(R.string.pressure_unit)
            binding.humidityLayout.valueTxt.text = it.current.humidity.toString() + getString(R.string.percentage)
            binding.windLayout.valueTxt.text = it.current.windSpeed.toString() + getString(R.string.percentage)
            binding.cloudLayout.valueTxt.text = it.current.clouds.toString() + getString(R.string.percentage)
            binding.ultraVioletLayout.valueTxt.text = it.current.uvi.toString()
            binding.visibilityLayout.valueTxt.text = it.current.visibility.toString() + getString(R.string.m)



            binding.pressureLayout.titleTxt.text = getString(R.string.pressure)
            binding.humidityLayout.titleTxt.text = getString(R.string.humidity)
            binding.windLayout.titleTxt.text = getString(R.string.wind)
            binding.cloudLayout.titleTxt.text = getString(R.string.cloud)
            binding.ultraVioletLayout.titleTxt.text = getString(R.string.ultraViolet)
            binding.visibilityLayout.titleTxt.text = getString(R.string.visibility)


//            var pressureImg = "http://openweathermap.org/img/wn/${it.current.weather[0].icon}.png"
//            Glide.with(requireContext()).load(pressureImg).into(binding.pressureLayout.image)

        }

        oneFavDetailsViewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            Log.d("TAG", "setUpRetrofit: ERROR" + it)

        }

        oneFavDetailsViewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it) {
                //binding.progressDialog.visibility = View.VISIBLE
            } else {
                // binding.progressDialog.visibility = View.GONE
            }
        })

        return root
    }

    private fun convertToDate(dt : Double) : String {
        val date = Date((dt * 1000).toLong())
        val format = SimpleDateFormat("EEE, d MMM", Locale.ENGLISH)
        return format.format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getAddressFromLocation(latLng: LatLng) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault()) //get default language
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // binding.govTxt.text = (addresses[0].getAddressLine(0) + " " + addresses[0].countryName)
            binding.govTxt.text = (addresses[0].adminArea + "\n" + addresses[0].getAddressLine(0))
            //binding.govTxt.text = it.timezone

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}