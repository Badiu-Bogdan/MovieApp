package com.example.smartestmovieapp.presentation.screens.cinema_details

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.local.model.cinema.CinemaEntity
import com.example.smartestmovieapp.data.local.model.cinema.RunningMovieDetails
import com.example.smartestmovieapp.data.repository.cinema.CinemaRepositoryImpl
import com.example.smartestmovieapp.data.repository.movieDetails.MovieDetailsRepository
import com.example.smartestmovieapp.data.responses.Resource
import com.example.smartestmovieapp.databinding.ActivityCinemaDetailsBinding
import com.example.smartestmovieapp.presentation.screens.VM.CinemaVM
import com.example.smartestmovieapp.presentation.screens.VM.MovieDetailsVM
import com.example.smartestmovieapp.presentation.screens.adapter.CinemaMovieAdapter
import com.example.smartestmovieapp.presentation.screens.adapter.Movie
import com.example.smartestmovieapp.presentation.screens.cinema.CinemaScreenFragment
import com.example.smartestmovieapp.presentation.screens.map.MapsActivity
import com.example.smartestmovieapp.presentation.screens.movie_details.MovieDetailsScreenActivity
import com.example.smartestmovieapp.presentation.screens.movie_details.OnMovieCardClickListener
import dagger.android.AndroidInjection
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class CinemaDetailsActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivityCinemaDetailsBinding
    private lateinit var adapter: CinemaMovieAdapter
    private lateinit var cinema: CinemaEntity

    @Inject
    lateinit var movieDetailsRepository: MovieDetailsRepository

    @Inject
    lateinit var movieDetailsVM: MovieDetailsVM

    @Inject
    lateinit var cinemaRepository: CinemaRepositoryImpl

    @Inject
    lateinit var cinemaVM: CinemaVM

    val movieCardClickListener = object : OnMovieCardClickListener {
        override fun onClick(movie: Movie) {
            val intentMovieDetails =
                Intent(this@CinemaDetailsActivity, MovieDetailsScreenActivity::class.java)
            intentMovieDetails.putExtra("MOVIE", movie)
            startActivity(intentMovieDetails)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setTheme()
        super.onCreate(savedInstanceState)

        binding = ActivityCinemaDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)


        val intent = getIntent()
        cinema = intent.getSerializableExtra("CINEMA") as CinemaEntity

        binding.cinemaName.text = cinema.name
        binding.cinemaAddress.text = cinema.address
        Glide.with(binding.root).load(cinema.imageUrl).into(binding.image)

        binding.toolbar.backButton.setOnClickListener {
            this.onBackPressed()
        }

        getCinemaMovies(cinema.id)

        binding.toolbar.seeCinema.setOnClickListener {
            checkPermissions()
        }

        adapter = CinemaMovieAdapter(ArrayList<RunningMovieDetails>(), movieCardClickListener)
        binding.recyclerView.layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerView.adapter = adapter

        binding.recyclerView2.layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerView2.adapter = adapter

        binding.recyclerView3.layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerView3.adapter = adapter

        binding.recyclerView4.layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerView4.adapter = adapter

        binding.recyclerView5.layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerView5.adapter = adapter

        binding.recyclerView6.layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerView6.adapter = adapter

        binding.recyclerView7.layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerView7.adapter = adapter
    }

    private fun getCinemaMovies(cinemaId: Int) {

        cinemaVM.getMoviesRunningAtSpecificCinema(cinemaId)
        cinemaVM.cinemaRunningMovies.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.data?.apply {
                        this.forEach {
                            val movieDetails =
                                movieDetailsRepository.getMovieDetails(it.movie_id).blockingGet()
                            val runningMovie = RunningMovieDetails(
                                movieDetails.movie_detailsID,
                                movieDetails.title,
                                movieDetails.runtime,
                                movieDetails.vote_average,
                                movieDetails.genres,
                                movieDetails.image_url,
                                it.runningHours
                            )
                            adapter.addMovie(runningMovie)
                        }
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(
                        baseContext,
                        "Something went wrong when retrieving the movies",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }

    private fun openMapsActivity(option: String, cinema: CinemaEntity) {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("OPTION", option)
        intent.putExtra("CINEMA", cinema)
        startActivity(intent)
    }

    private fun checkPermissions() {
        if (EasyPermissions.hasPermissions(this, *permissions)) {
            //get location
            openMapsActivity("CINEMA", cinema)
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
            openMapsActivity("CINEMA", cinema)
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

    companion object {
        internal const val PERMISSION_REQUEST_ACCESS_LOCATION = 101
        private const val REASON =
            "In order to use this feature, the application needs to access to the location"
        internal val permissions = arrayOf<String>(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
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