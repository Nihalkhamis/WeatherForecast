package com.kotlin.weatherforecast.home.homefragment.view

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.kotlin.weatherforecast.R
import com.kotlin.weatherforecast.databinding.FragmentHomeBinding
import com.kotlin.weatherforecast.db.ConcreteLocalSource
import com.kotlin.weatherforecast.home.homefragment.viewmodel.HomeViewModel
import com.kotlin.weatherforecast.home.homefragment.viewmodel.HomeViewModelFactory
import com.kotlin.weatherforecast.model.WeatherRepository
import com.kotlin.weatherforecast.network.ApiClient
import com.kotlin.weatherforecast.utils.Constants
import com.kotlin.weatherforecast.utils.MyPreference
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    lateinit var tempPerHourAdapter: TempPerHourAdapter
    lateinit var tempHourLayoutManager: LinearLayoutManager

    lateinit var tempDayAdapter: TempDayAdapter
    lateinit var tempDayLayoutManager: LinearLayoutManager

    lateinit var vmFactory: HomeViewModelFactory
    lateinit var homeViewModel : HomeViewModel

    lateinit var preference: MyPreference




    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        preference = MyPreference.getInstance(requireContext())!!


        Log.d("TAG", "onCreateView: LAST LAT is: ${preference.getData(Constants.LAT)}")
        Log.d("TAG", "onCreateView: LAST LONG is: ${preference.getData(Constants.LONG)}")

        vmFactory = HomeViewModelFactory(
            WeatherRepository.getRepoInstance(
                ApiClient.getClientInstance()!!,
                ConcreteLocalSource(requireContext()),
                requireContext()
            ),requireContext()
        )



        homeViewModel = ViewModelProvider(this,vmFactory).get(HomeViewModel::class.java)
       homeViewModel.getWeatherDetails(requireContext())

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tempHourLayoutManager = LinearLayoutManager(requireContext())
        tempHourLayoutManager.orientation = RecyclerView.HORIZONTAL
        tempPerHourAdapter = TempPerHourAdapter(requireContext(), ArrayList())
        binding.tempDayHourRv.adapter = tempPerHourAdapter
        binding.tempDayHourRv.layoutManager = tempHourLayoutManager

        tempDayAdapter = TempDayAdapter(requireContext(),ArrayList())
        tempDayLayoutManager = LinearLayoutManager(requireContext())
        tempDayLayoutManager.orientation = RecyclerView.VERTICAL
        binding.tempDayRv.adapter = tempDayAdapter
        binding.tempDayRv.layoutManager = tempDayLayoutManager


        homeViewModel.liveDataWeatherList.observe(viewLifecycleOwner) {
            Log.d("TAG", "onCreateView: Saved temp is: ${preference.getData(Constants.TEMPERATURE)}")

            Log.d("TAG", "onCreateView: RESPONSE----->$it")
            //binding.govTxt.text = it.timezone
            getAddressFromLocation(LatLng(it.lat,it.lon))
            var fullDay = convertToDate(it.current.dt)
            binding.dateTxt.text = fullDay
            binding.weatherStatusTxt.text = it.current.weather[0].description
            binding.weatherTempTxt.text = it.current.temp.toString()
            when {
                preference.getData(Constants.TEMPERATURE).equals("metric") -> {
                    binding.tempTypeTxt.text = getString(R.string.c)
                    binding.windLayout.valueTxt.text = it.current.windSpeed.toString() + getString(R.string.m_sec)

                }
                preference.getData(Constants.TEMPERATURE).equals("imperial") -> {
                    binding.tempTypeTxt.text = getString(R.string.f)
                    binding.windLayout.valueTxt.text = it.current.windSpeed.toString() + getString(R.string.mile_h)

                }
                else -> {
                    binding.tempTypeTxt.text = getString(R.string.k)
                    binding.windLayout.valueTxt.text = it.current.windSpeed.toString() + getString(R.string.m_sec)
                }
            }

            var url = "http://openweathermap.org/img/wn/${it.current.weather[0].icon}.png"
            Log.d("TAG", "onBindViewHolder: CURR Weather IMG--->$url")

            Glide.with(requireContext()).load(url).into(binding.weatherImg)

            tempPerHourAdapter.setWeatherPerHour(it.hourly)
            tempDayAdapter.setWeatherPerDay(it.daily)


            binding.pressureLayout.valueTxt.text = it.current.pressure.toString() + getString(R.string.pressure_unit)
            binding.humidityLayout.valueTxt.text = it.current.humidity.toString() + getString(R.string.percentage)
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

            homeViewModel.errorMessage.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.d("TAG", "setUpRetrofit: ERROR" + it)

            }

            homeViewModel.loading.observe(viewLifecycleOwner, Observer {
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