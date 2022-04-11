package com.kotlin.weatherforecast.home.favourites.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.weatherforecast.MapsActivity
import com.kotlin.weatherforecast.databinding.FragmentFavouritesBinding
import com.kotlin.weatherforecast.db.AppDataBase
import com.kotlin.weatherforecast.db.ConcreteLocalSource
import com.kotlin.weatherforecast.home.favourites.viewmodel.FavouritesViewModel
import com.kotlin.weatherforecast.home.favourites.viewmodel.FavouritesViewModelFactory
import com.kotlin.weatherforecast.model.FavLocationsModel
import com.kotlin.weatherforecast.model.WeatherRepository
import com.kotlin.weatherforecast.network.ApiClient

class FavouritesFragment : Fragment(), OnLocationClickListener {


    private var _binding: FragmentFavouritesBinding? = null

    lateinit var favLocationAdapter: FavLocationAdapter
    lateinit var favLocationsLayoutManager: LinearLayoutManager
   // lateinit var favLocations : ArrayList<FavLocationsModel>

   lateinit var vmFactory : FavouritesViewModelFactory
   lateinit var favViewModel: FavouritesViewModel

    lateinit var appDataBase: AppDataBase


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        vmFactory = FavouritesViewModelFactory(
            WeatherRepository.getRepoInstance(
                ApiClient.getClientInstance()!!,
                ConcreteLocalSource(requireContext()),
                requireContext()
            ),requireContext()
        )

        favViewModel = ViewModelProvider(this,vmFactory).get(FavouritesViewModel::class.java)

        appDataBase = AppDataBase.getInstance(requireContext())


        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.addLocationFloatBtn.setOnClickListener {
            var intent = Intent(requireContext(),MapsActivity::class.java)
            intent.putExtra("from fav",1)
            startActivity(intent)
        }


       /* favLocations = arrayListOf(
            FavLocationsModel(30.30,29.31,"Alexandria",1),
            FavLocationsModel(30.30,29.31,"Alexandria",2),
            FavLocationsModel(30.30,29.31,"Alexandria",3),
            FavLocationsModel(30.30,29.31,"Alexandria",4)
        )*/

        favLocationsLayoutManager = LinearLayoutManager(requireContext())
        favLocationsLayoutManager.orientation = RecyclerView.VERTICAL
        favLocationAdapter = FavLocationAdapter(requireContext(), ArrayList(), this)
        binding.favLocationRv.adapter = favLocationAdapter
        binding.favLocationRv.layoutManager = favLocationsLayoutManager


        favViewModel.getLocations().observe(requireActivity()) { movies ->
            Log.i("TAG", "Observation: ${movies}")
            if (movies != null)
                favLocationAdapter.setFavLocations(movies)
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(favLocationsModel: FavLocationsModel) {
        findNavController().navigate(FavouritesFragmentDirections.actionFavouritesFragmentToFavDetailsNav(favLocationsModel.lat.toString(),
            favLocationsModel.lon.toString()))
    }

    override fun onDeleteClicked(favLocationsModel: FavLocationsModel) {
        favViewModel.deleteLocation(favLocationsModel)
        Toast.makeText(requireContext(),"Location deleted", Toast.LENGTH_SHORT).show()
    }

}