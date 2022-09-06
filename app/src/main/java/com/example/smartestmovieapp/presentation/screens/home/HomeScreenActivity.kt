package com.example.smartestmovieapp.presentation.screens.home

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.local.model.cinema.CinemaEntity
import com.example.smartestmovieapp.data.local.model.cinema.CinemaMoviesEntity
import com.example.smartestmovieapp.data.repository.cinema.CinemaRepositoryImpl
import com.example.smartestmovieapp.data.responses.Resource
import com.example.smartestmovieapp.databinding.ActivityHomeScreenBinding
import com.example.smartestmovieapp.presentation.screens.VM.CinemaVM
import com.example.smartestmovieapp.presentation.screens.notifications.NotificationsFragment
import dagger.android.AndroidInjection
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import javax.inject.Inject


class HomeScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeScreenBinding

    @Inject
    lateinit var cinemaRepo: CinemaRepositoryImpl

    @Inject
    lateinit var cinemaVM: CinemaVM


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragmentContainerView)
        binding.bottomNavigationView.setupWithNavController(navController)

        // Function that watches the destination from navGraph to turn off the bottom navigation
        // for the screens that are not setup with bottom navigation
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.notificationsFragment)
                binding.bottomNavigationView.visibility = View.GONE
            else
                binding.bottomNavigationView.visibility = View.VISIBLE
        }

        setTransparentStatusBar()
        checkCinemaDatabase()
    }

    fun Activity.setTransparentStatusBar() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun checkCinemaDatabase() {
        cinemaVM.getCinemaDatabaseDataCount()
        cinemaVM.cinemaDataCount.observe(this) {

            if (it is Resource.Success) {
                it.data?.apply {
                    if (this == 0) initCinemaDatabase()
                }
            }
        }
    }

    private fun initCinemaDatabase() {

        val cinemas = loadDataFromJson(baseContext)
        for (item in 0 until cinemas.length()) {

            val jsoncinema = cinemas.getJSONObject(item)
            val cinemaItem = getCinemaFromJSON(jsoncinema)
            val runningMoviesInCinema = getRunninMoviesFromCinemaJSON(jsoncinema)
            cinemaVM.insertCinema(cinemaItem)
            cinemaVM.saveRunningMoviesList(runningMoviesInCinema)

        }

    }

    private fun loadDataFromJson(context: Context): JSONArray {

        val inputStream = context.resources.openRawResource(R.raw.cinemas)
        BufferedReader(inputStream.reader()).use {
            val jsonObj = JSONObject(it.readText())
            return jsonObj.getJSONArray("cinemas")
        }
    }

    private fun getCinemaFromJSON(cinemaJson: JSONObject): CinemaEntity {
        val id = cinemaJson.getInt("id")
        val name = cinemaJson.getString("name")
        val address = cinemaJson.getString("address")
        val lat = cinemaJson.getDouble("lat")
        val lng = cinemaJson.getDouble("lng")
        val image = cinemaJson.getString("photo_url")

        return CinemaEntity(
            id,
            name,
            address,
            lat,
            lng,
            image
        )
    }

    private fun getRunninMoviesFromCinemaJSON(cinemaJson: JSONObject): List<CinemaMoviesEntity> {
        var runningMoviesList = ArrayList<CinemaMoviesEntity>()

        val cinemaId = cinemaJson.getInt("id")
        val runningMoviesArray = cinemaJson.getJSONArray("running")
        //Log.v("TEST", "Message: " + runningMoviesArray.toString())

        for (index in 0 until runningMoviesArray.length()) {
            // get the string of running hours
            val runningHours = ArrayList<String>()
            val hours = (runningMoviesArray.get(index) as JSONObject).getJSONArray("hours")
            for (indexHours in 0 until hours.length()) {
                runningHours.add(hours.get(indexHours).toString())
            }

            //get movie id
            val movieId = (runningMoviesArray.get(index) as JSONObject).getInt("movie_id")

            //add new running movie to the list of movies running at the current cinema
            runningMoviesList.add(
                CinemaMoviesEntity(
                    0,
                    movieId,
                    runningHours,
                    cinemaId
                )
            )
            // Log.v("TEST", "Message_2: " + hours.toString())
        }
        return runningMoviesList
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