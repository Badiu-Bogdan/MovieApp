package com.example.smartestmovieapp.presentation.screens.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.local.model.cinema.CinemaEntity
import com.example.smartestmovieapp.data.repository.cinema.CinemaRepositoryImpl
import com.example.smartestmovieapp.data.responses.Resource
import com.example.smartestmovieapp.databinding.ActivityMapsBinding
import com.example.smartestmovieapp.presentation.screens.VM.CinemaVM
import com.example.smartestmovieapp.presentation.screens.cinema.CinemaScreenFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.AndroidInjection
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val ZOOM = 15.0f
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var cinemaRepository: CinemaRepositoryImpl

    @Inject
    lateinit var cinemaVM: CinemaVM

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setTheme()
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager.findFragmentById(com.example.smartestmovieapp.R.id.maps) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setPadding(0, 100, 0, 0)
        if (checkLocationEnabled()) mMap.isMyLocationEnabled = true

        val option = intent.getStringExtra("OPTION")
        when (option) {
            "CINEMA" -> {
                val cinema = intent.getSerializableExtra("CINEMA") as CinemaEntity
                centerMarkerAtCoordinates(cinema)
            }
            "ALL" -> {
                showCinemas()
            }
        }
    }

    private fun centerMarkerAtCoordinates(cinema: CinemaEntity) {
        val cinemaCoordinates = LatLng(cinema.lat, cinema.lng)
        addCinemaMarker(cinema)
        centerMapOnLocation(cinemaCoordinates)
    }

    private fun addCinemaMarker(cinema: CinemaEntity) {
        val cinemaCoordinates = LatLng(cinema.lat, cinema.lng)

        Glide.with(binding.root).asBitmap()
            .load(cinema.imageUrl)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                ) {
                    val markerOptions = MarkerOptions()
                        .position(cinemaCoordinates)
                        .title(cinema.name)
                        .icon(
                            getBitmapDescriptorFromBitmap(resource)?.let {
                                BitmapDescriptorFactory.fromBitmap(it)
                            })

                    mMap.addMarker(markerOptions)
                }
            })
    }

    fun centerMapOnLocation(location: Location) {
        val userLocation = LatLng(location.getLatitude(), location.getLongitude())
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, ZOOM))
    }

    fun centerMapOnLocation(location: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM))
    }

    private fun showCinemas() {
        cinemaVM.cinemaList.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let {
                        it.forEach { cinema ->
                            addCinemaMarker(cinema)
                            getUserCurrentLocation()
                        }

                    }
                }
                else -> {}
            }
        }
    }

    private fun getBitmapDescriptorFromBitmap(res: Bitmap): Bitmap? {
        val marker: View =
            layoutInflater.inflate(com.example.smartestmovieapp.R.layout.custom_marker, null)
        val markerImage: ImageView = marker.findViewById(com.example.smartestmovieapp.R.id.image)
        markerImage.setImageBitmap(res)
        val displayMetrics = DisplayMetrics()
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap2 = Bitmap.createBitmap(
            marker.getMeasuredWidth(),
            marker.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888
        )
        marker.draw(Canvas(bitmap2))
        return bitmap2
    }

    @SuppressLint("MissingPermission")
    private fun getUserCurrentLocation() {

        if (checkLocationEnabled()) {
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location ->
                    centerMapOnLocation(location)
                }

        } else {
            Toast.makeText(this, "Location is not enabled", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun checkLocationEnabled(): Boolean {
        val locationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    //handle permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 101
        private const val REASON =
            "In order to use this feature, the application needs to access to the location"
        private val permissions = arrayOf<String>(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == CinemaScreenFragment.PERMISSION_REQUEST_ACCESS_LOCATION && perms.size == CinemaScreenFragment.permissions.size)
            getUserCurrentLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
            Toast.makeText(this, "Need location permissions", Toast.LENGTH_SHORT).show()
        }
        if (EasyPermissions.somePermissionDenied(this, *CinemaScreenFragment.permissions)) {
            Toast.makeText(this, "Need location permissions", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setTheme() {
        val themePrefs = baseContext.getSharedPreferences("chosenTheme", Context.MODE_PRIVATE)
        val defaultThemeChosen = themePrefs.getBoolean("Default", true)
        if (defaultThemeChosen) {
            this.setTheme(R.style.Theme_SmartestMovieApp)
        } else {
            this.setTheme(R.style.Theme_SmartestMovieApp_SecondTheme)
        }
    }
}
