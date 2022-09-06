package com.example.smartestmovieapp.presentation.screens.movie_details

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.local.model.favorites.FavoriteMovieEntity
import com.example.smartestmovieapp.data.local.model.genres.GenreEntity
import com.example.smartestmovieapp.data.local.model.movie_details.MovieDetailsEntity
import com.example.smartestmovieapp.data.local.model.similar_movies.SimilarMovie
import com.example.smartestmovieapp.data.repository.cinema.CinemaRepositoryImpl
import com.example.smartestmovieapp.data.repository.favorites.FavoriteMovieRepository
import com.example.smartestmovieapp.data.repository.genres.GenreRepositoryImpl
import com.example.smartestmovieapp.data.repository.movieDetails.MovieDetailsRepositoryImpl
import com.example.smartestmovieapp.data.repository.similar_movies.SimilarMoviesRepositoryImpl
import com.example.smartestmovieapp.data.responses.Resource
import com.example.smartestmovieapp.databinding.ActivityMovieDetailsScreenBinding
import com.example.smartestmovieapp.presentation.screens.VM.*
import com.example.smartestmovieapp.presentation.screens.adapter.Movie
import dagger.android.AndroidInjection
import javax.inject.Inject


class MovieDetailsScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var movieDetailsRepositoryImpl: MovieDetailsRepositoryImpl

    @Inject
    lateinit var similarMoviesRepositoryImpl: SimilarMoviesRepositoryImpl

    @Inject
    lateinit var genresRepositoryImpl: GenreRepositoryImpl

    @Inject
    lateinit var cinemaRepositoryImpl: CinemaRepositoryImpl

    @Inject
    lateinit var movieDetailsVM: MovieDetailsVM

    @Inject
    lateinit var trailerVM: TrailerVM

    @Inject
    lateinit var favoriteMovieRepository: FavoriteMovieRepository

    @Inject
    lateinit var favoritesVM: FavoritesVM

    @Inject
    lateinit var similarMoviesVM: SimilarMoviesVM

    @Inject
    lateinit var cinemaVM: CinemaVM

    private lateinit var binding: ActivityMovieDetailsScreenBinding
    private lateinit var selectedMovie: Movie
    private lateinit var similarMoviesList: ArrayList<Movie>
    private lateinit var moviesAdapter: MoviesAdapterWithListener
    private lateinit var currentMovieDetailsEntity: MovieDetailsEntity
    private var inDatabase: Boolean = false
    private val CHOOSER_MESSAGE = "How would you like to open the link?"
    private lateinit var intentOpenTrailer: Intent
    private lateinit var intentChooser: Intent


    val movieCardClickListener = object : OnMovieCardClickListener {
        override fun onClick(movie: Movie) {
            val intentMovieDetails =
                Intent(this@MovieDetailsScreenActivity, MovieDetailsScreenActivity::class.java)
            intentMovieDetails.putExtra("MOVIE", movie)
            startActivity(intentMovieDetails)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set status bar transparent
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        initIntentChooser()
        initMovieDetailsData()

        binding.toolbar.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.toolbar.addToFavorites.setOnClickListener {
            addToFavorites()
        }
        binding.checkAvailabilityButton.setOnClickListener {
            cinemaVM.cinemaList.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        val listOfCinemaWhereRunningSelectedMovie = StringBuilder()
                        val cinemaList = it.data!!
                        cinemaList.forEach { cinema ->
                            val runningMovies =
                                cinemaRepositoryImpl.getRunningMoviesOfCinema(cinema.id)
                                    .blockingFirst()
                            runningMovies.forEach {
                                if (it.movie_id == selectedMovie.id)
                                    listOfCinemaWhereRunningSelectedMovie
                                        .append(cinema.id.toString())
                                        .append(",")
                            }
                        }
                        if (listOfCinemaWhereRunningSelectedMovie.isBlank())
                            Toast.makeText(
                                this,
                                "This movie is not playing in any cinema",
                                Toast.LENGTH_LONG
                            ).show()
                        else {
                            listOfCinemaWhereRunningSelectedMovie.deleteAt(
                                listOfCinemaWhereRunningSelectedMovie.length - 1
                            )
                            val intent = Intent(this, CheckAvailabilityActivity::class.java)
                            intent.putExtra("IDs", listOfCinemaWhereRunningSelectedMovie.toString())
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(this, "error: ${it.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                    is Resource.Loading -> {}
                }
            }
        }

        moviesAdapter = MoviesAdapterWithListener(
            similarMoviesList,
            R.layout.cardview_small_version,
            movieCardClickListener
        )
        binding.recyclerView.layoutManager =
            LinearLayoutManager(baseContext).apply { orientation = LinearLayoutManager.HORIZONTAL }
        binding.recyclerView.adapter = moviesAdapter
        binding.watchTrailerButtonHome.setOnClickListener {
            getTrailer()
        }
    }

    private fun initIntentChooser() {
        intentOpenTrailer = Intent(Intent.ACTION_VIEW)
        intentChooser = Intent.createChooser(intentOpenTrailer, CHOOSER_MESSAGE)
    }

    private fun initMovieDetailsData() {
        selectedMovie = intent.getSerializableExtra("MOVIE") as Movie
        binding.movieName.text = selectedMovie.title
        Glide.with(binding.root)
            .load(selectedMovie.imageUrl)
            .placeholder(R.drawable.default_movie_image)
            //.onlyRetrieveFromCache(true)
            //.error(R.drawable.default_movie_image)
            //.fallback(R.drawable.default_movie_image)
            .into(binding.image)
        similarMoviesList = ArrayList<Movie>()
        checkAddedToDatabase(selectedMovie.id)
        movieDetailsVM.getMovieDetails(selectedMovie.id)
        movieDetailsVM.movieDetailsLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    currentMovieDetailsEntity = it.data!!
                    buildMovieDetails(currentMovieDetailsEntity)
                    initSimilarMoviesData(currentMovieDetailsEntity)
                }
                is Resource.Error -> {
                    // Toast.makeText(this, "error: ${it.message}", Toast.LENGTH_LONG)
                    //    .show()
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun convertGenresNameToIDList(
        currentMovie: MovieDetailsEntity,
        allGenres: List<GenreEntity>
    ): List<String> {
        val currentMovieIDList: MutableList<String> = mutableListOf()
        currentMovie.genres.forEach { currentGenreName ->
            for (genre in allGenres)
                if (genre.name == currentGenreName)
                    currentMovieIDList.add(genre.genreID.toString())
        }
        return currentMovieIDList.toList()
    }

    private fun buildMovieDetails(movieEntity: MovieDetailsEntity) {

        binding.movieDescription.text = movieEntity.description
        binding.rating.rating = (movieEntity.vote_average / 2).toFloat()

        val stringBuilder = StringBuilder()
        stringBuilder.append(movieEntity.language[0].uppercase())
            .append(movieEntity.language.subSequence(1, movieEntity.language.length))
            .append(" | ")

        movieEntity.genres.forEach { genre -> stringBuilder.append(genre).append(", ") }
        stringBuilder.deleteAt(stringBuilder.lastIndex)
        stringBuilder.deleteAt(stringBuilder.lastIndex)
        stringBuilder.append(" | ")

        val hours = movieEntity.runtime / 60
        val minutes = movieEntity.runtime % 60
        stringBuilder.append(hours).append("h").append(minutes).append("m")

        binding.movieDetails.text = stringBuilder.toString()
    }

    private fun initSimilarMoviesData(currentMovie: MovieDetailsEntity) {
        similarMoviesVM.getMovies(currentMovie.movie_detailsID)
        similarMoviesVM.similarMoviesLiveData.observe(this) { it ->
            when (it) {
                is Resource.Success -> {
                    similarMoviesList.clear()
                    if (isNetworkAvailable(this)) {
                        getRemoteSimilarMovies(it.data!!)
                    } else {
                        getLocalSimilarMovies(currentMovie, it.data!!)
                    }

                    moviesAdapter.notifyDataSetChanged()
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }

    private fun getRemoteSimilarMovies(responseSimilarMoviesList: List<SimilarMovie>) {
        responseSimilarMoviesList.forEach {
            val rating = String.format("%.1f", it.vote_average).toDouble()
            this.similarMoviesList.add(
                Movie(
                    it.id,
                    it.title,
                    rating.toString(),
                    rating,
                    mutableListOf(),
                    0,
                    it.image_url
                )
            )
        }
    }

    private fun getLocalSimilarMovies(
        currentMovie: MovieDetailsEntity,
        responseSimilarMoviesList: List<SimilarMovie>
    ) {
        val allGenres =
            genresRepositoryImpl.localDataSource.getAllGenres()
        val currentMovieIDList = convertGenresNameToIDList(currentMovie, allGenres.blockingGet())
        for (similarMovie in responseSimilarMoviesList) {
            if (currentMovie.title == similarMovie.title)
                continue
            if (currentMovieIDList.any { it in similarMovie.genres }) {
                this.similarMoviesList.add(
                    Movie(
                        similarMovie.id,
                        similarMovie.title,
                        similarMovie.vote_average.toString(),
                        similarMovie.vote_average,
                        mutableListOf(),
                        0,
                        similarMovie.image_url
                    )
                )
            }

        }
    }

    private fun getTrailer() {
        if (isNetworkAvailable(this)) {
            trailerVM.getTrailer(selectedMovie.id)
            trailerVM.trailerLiveData.observeOnce(this) {
                if (it is Resource.Success) {
                    intentOpenTrailer.data = Uri.parse(it.data?.video_url)
                    startActivity(intentChooser)
                }
            }
        } else {
            Toast.makeText(
                this,
                "No internet connection! Please connect to internet through WIFI or mobile data",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun isNetworkAvailable(activity: FragmentActivity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun checkAddedToDatabase(id: Int) {
        favoritesVM.checkMovieAddedToFavorites(id)
        favoritesVM.alreadyInFavorites.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.data?.apply {
                        val view = binding.toolbar.addToFavorites
                        val fav_icon = view.findViewById<ImageView>(R.id.fav_icon)
                        fav_icon.setBackgroundResource(R.drawable.ic_favorite)
                        inDatabase = true
                    }
                }
                is Resource.Error -> {}
                else -> {}
            }
        }
    }

    private fun addToFavorites() {
        if (!inDatabase) {
            currentMovieDetailsEntity.apply {
                favoritesVM.addMovieToFavorites(
                    FavoriteMovieEntity(
                        selectedMovie.id,
                        selectedMovie.title,
                        currentMovieDetailsEntity.description,
                        currentMovieDetailsEntity.language,
                        currentMovieDetailsEntity.runtime,
                        currentMovieDetailsEntity.vote_average,
                        currentMovieDetailsEntity.genres,
                        currentMovieDetailsEntity.image_url
                    )
                )
            }
            val view = binding.toolbar.addToFavorites
            val fav_icon = view.findViewById<ImageView>(R.id.fav_icon)
            fav_icon.setBackgroundResource(R.drawable.ic_favorite)
            inDatabase = true
        } else {
            favoritesVM.removeMovieFromfavorites(selectedMovie.id)
            val view = binding.toolbar.addToFavorites
            val fav_icon = view.findViewById<ImageView>(R.id.fav_icon)
            fav_icon.setBackgroundResource(R.drawable.ic_favorite_border)
            inDatabase = false
        }
    }

    fun <T> MutableLiveData<Resource<T>>.observeOnce(
        lifecycleOwner: LifecycleOwner,
        observer: Observer<Resource<T>>
    ) {
        observe(lifecycleOwner, object : Observer<Resource<T>> {
            override fun onChanged(t: Resource<T>?) {
                observer.onChanged(t)
                val isNotLoading = t !is Resource.Loading
                if (isNotLoading)
                    removeObserver(this)
            }
        })
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