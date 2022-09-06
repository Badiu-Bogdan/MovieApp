package com.example.smartestmovieapp.presentation.screens.movie_details

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.local.model.cinema.CinemaEntity
import com.example.smartestmovieapp.data.repository.cinema.CinemaRepositoryImpl
import com.example.smartestmovieapp.databinding.ActivityCheckAvailabilityBinding
import com.example.smartestmovieapp.presentation.screens.cinema.CinemaAdapter
import com.example.smartestmovieapp.presentation.screens.cinema.CinemaScreenFragment
import com.example.smartestmovieapp.presentation.screens.cinema_details.CinemaDetailsActivity
import com.example.smartestmovieapp.presentation.screens.map.MapsActivity
import dagger.android.AndroidInjection
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class CheckAvailabilityActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    @Inject
    lateinit var cinemaRepositoryImpl: CinemaRepositoryImpl
    private lateinit var binding: ActivityCheckAvailabilityBinding
    private lateinit var cinemaAdapter: CinemaAdapter
    private var cinemaList = ArrayList<CinemaEntity>()
    private lateinit var chosenCinema: CinemaEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityCheckAvailabilityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //set status bar transparent
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val listOfCinemaWhereRunningSelectedMovie = intent.getStringExtra("IDs")
        val cinemaIDsList = listOfCinemaWhereRunningSelectedMovie?.split(",")
        Log.d("ListCinema", cinemaIDsList.toString())
        cinemaIDsList?.forEach { id ->
            val cinema = cinemaRepositoryImpl.getCinemaById(id.toInt())
            cinemaList.add(
                CinemaEntity(
                    cinema.id,
                    cinema.name,
                    cinema.address,
                    cinema.lat,
                    cinema.lng,
                    cinema.imageUrl
                )
            )
        }
        binding.recyclerViewCategory.layoutManager = LinearLayoutManager(this)
        cinemaAdapter = CinemaAdapter(
            cinemaList,
            cinemaCardClickListener,
            seeInMapListener,
            R.layout.cardview_cinema_wide_version
        )
        binding.recyclerViewCategory.adapter = cinemaAdapter
    }

    private val cinemaCardClickListener = object : CinemaAdapter.onCinemaCardClicked {
        override fun onClick(cinema: CinemaEntity) {
            openCinemaDetails(cinema)
        }

    }

    private val seeInMapListener = object : CinemaAdapter.onCinemaCardClicked {
        override fun onClick(cinema: CinemaEntity) {
            chosenCinema = cinema
            checkPermissions()
        }

    }

    private fun openCinemaDetails(cinema: CinemaEntity) {
        val intent = Intent(this, CinemaDetailsActivity::class.java)
        intent.putExtra("CINEMA", cinema)
        startActivity(intent)
    }

    private fun openMapsActivity(option: String, cinema: CinemaEntity) {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("OPTION", option)
        intent.putExtra("CINEMA", cinema)
        startActivity(intent)
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

    private fun checkPermissions() {
        if (EasyPermissions.hasPermissions(this, *CinemaScreenFragment.permissions)) {
            //get location
            openMapsActivity("CINEMA", chosenCinema)
        } else {
            EasyPermissions.requestPermissions(
                this, REASON, 101, *permissions
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION && perms.size == permissions.size)
            openMapsActivity("CINEMA", chosenCinema)
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