package com.example.smartestmovieapp.presentation.screens.cinema

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.local.model.cinema.CinemaEntity
import com.example.smartestmovieapp.data.repository.cinema.CinemaRepositoryImpl
import com.example.smartestmovieapp.data.responses.Resource
import com.example.smartestmovieapp.databinding.FragmentCinemaScreenBinding
import com.example.smartestmovieapp.presentation.screens.VM.CinemaVM
import com.example.smartestmovieapp.presentation.screens.category.CategoryScreenActivity
import com.example.smartestmovieapp.presentation.screens.cinema_details.CinemaDetailsActivity
import com.example.smartestmovieapp.presentation.screens.map.MapsActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.android.support.AndroidSupportInjection
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import javax.inject.Inject

class CinemaScreenFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var binding: FragmentCinemaScreenBinding
    private lateinit var adapter: CinemaAdapter
    private var cinemaList = ArrayList<CinemaEntity>()

    @Inject
    lateinit var cinemaRepository: CinemaRepositoryImpl

    @Inject
    lateinit var cinemaVM: CinemaVM

    private val cinemaCardClickListener = object : CinemaAdapter.onCinemaCardClicked {
        override fun onClick(cinema: CinemaEntity) {
            openCinemaDetails(cinema)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCinemaScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()

        binding.homeScreenToolbar.editTextSearch.hint = "Search cinema ..."

        binding.recyclerViewCinemasNearby.layoutManager =
            LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }

        adapter = CinemaAdapter(
            cinemaList,
            cinemaCardClickListener,
            null,
            R.layout.cardview_cinema_small_version
        )
        binding.recyclerViewCinemasNearby.adapter = adapter

        setupButtonConnection()
    }

    private fun setupButtonConnection() {
        binding.homeScreenToolbar.imageViewBell.setOnClickListener { goToNotificationsScreen() }
    }

    private fun goToNotificationsScreen() {
        val bundle = Bundle()
        bundle.putBoolean("networkConnection", isNetworkAvailable(requireActivity()))
        Navigation.findNavController(binding.root).navigate(R.id.fromCinemasToNotifications, bundle)
    }

    private fun isNetworkAvailable(activity: FragmentActivity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun openCinemaDetails(cinema: CinemaEntity) {
        val intent = Intent(requireActivity(), CinemaDetailsActivity::class.java)
        intent.putExtra("CINEMA", cinema)
        startActivity(intent)
    }

    private fun loadCinemaListBasedOnUserLocation(userLocation: Location) {
        cinemaVM.cinemaList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.apply {

                        val sortedByProximityCinemaList: ArrayList<CinemaEntity> =
                            ArrayList<CinemaEntity>()
                        sortedByProximityCinemaList.addAll(this)
                        Collections.sort(
                            sortedByProximityCinemaList,
                            object : Comparator<CinemaEntity> {
                                override fun compare(
                                    cinema1: CinemaEntity,
                                    cinema2: CinemaEntity
                                ): Int {
                                    val distanceUserToCinema1 =
                                        userLocation.distanceTo(Location("").apply {
                                            latitude = cinema1.lat
                                            longitude = cinema1.lng
                                        })

                                    val distanceUserToCinema2 =
                                        userLocation.distanceTo(Location("").apply {
                                            latitude = cinema2.lat
                                            longitude = cinema2.lng
                                        })

                                    return (distanceUserToCinema1 - distanceUserToCinema2).toInt()
                                }
                            })
                        val featuredCinema = sortedByProximityCinemaList.get(0)
                        sortedByProximityCinemaList.removeAt(0)

                        initializeMainCardView(featuredCinema)
                        adapter.updateList(sortedByProximityCinemaList)
                        searchCinema()
                        binding.seeMap.setOnClickListener {
                            openMapsActivity("ALL")
                        }
                        binding.viewAllCinemas.setOnClickListener {
                            goToCategoryScreen()
                        }

                    }
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Couldn't load cinemas", Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun searchCinema() {
        binding.homeScreenToolbar.editTextSearch.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                val searchedTitle = binding.homeScreenToolbar.editTextSearch.text.toString()
                val intent = Intent(requireActivity(), CategoryScreenActivity::class.java)
                intent.putExtra("TITLE", "Cinemas")
                intent.putExtra("SEARCH", searchedTitle)
                startActivity(intent)
            }
            false
        }
    }

    private fun openMapsActivity(option: String, cinema: CinemaEntity) {
        val intent = Intent(requireActivity(), MapsActivity::class.java)
        intent.putExtra("OPTION", option)
        intent.putExtra("CINEMA", cinema)
        startActivity(intent)
    }

    private fun openMapsActivity(option: String) {
        val intent = Intent(requireActivity(), MapsActivity::class.java)
        intent.putExtra("OPTION", option)
        startActivity(intent)
    }

    private fun goToCategoryScreen() {
        val intent = Intent(requireActivity(), CategoryScreenActivity::class.java)
        intent.putExtra("TITLE", "Cinemas")
        startActivity(intent)
    }

    private fun initializeMainCardView(closestCinema: CinemaEntity) {
        binding.cinemaFeatured.title.text = closestCinema.name
        binding.cinemaFeatured.address.text = closestCinema.address
        Glide.with(binding.root).load(closestCinema.imageUrl)
            .placeholder(R.drawable.default_movie_image).into(binding.cinemaFeatured.image)
        binding.frameCinemaFeatured.setOnClickListener { openCinemaDetails(closestCinema) }

        binding.cinemaFeatured.seeInMap.setOnClickListener {
            openMapsActivity("CINEMA", closestCinema)
        }

    }

    //LOCATION
    private fun checkPermissions() {
        if (EasyPermissions.hasPermissions(requireContext(), *permissions)) {
            //get location
            getUserCurrentLocation()
        } else {
            EasyPermissions.requestPermissions(
                this, REASON, 101, *permissions
            )
        }
    }


    @SuppressLint("MissingPermission")
    private fun getUserCurrentLocation() {

        if (checkLocationEnabled()) {
            binding.cinemaLayout.visibility = View.VISIBLE
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location ->
                    loadCinemaListBasedOnUserLocation(location)
                }

        } else {
            Toast.makeText(requireActivity(), "Location is not enabled", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }


    private fun checkLocationEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    companion object {
        internal const val PERMISSION_REQUEST_ACCESS_LOCATION = 101
        private const val REASON =
            "In order to use this feature, the application needs to access to the location"
        internal val permissions = arrayOf<String>(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    //handle permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION && perms.size == permissions.size)
            getUserCurrentLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
            Toast.makeText(requireContext(), "Need location permissions", Toast.LENGTH_SHORT).show()
        }
        if (EasyPermissions.somePermissionDenied(this, *permissions)) {
            Toast.makeText(requireContext(), "Need location permissions", Toast.LENGTH_SHORT).show()
        }
    }

}