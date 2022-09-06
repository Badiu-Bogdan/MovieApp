package com.example.smartestmovieapp.presentation.screens.category

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.local.model.cinema.CinemaEntity
import com.example.smartestmovieapp.data.repository.cinema.CinemaRepositoryImpl
import com.example.smartestmovieapp.data.repository.favorites.FavoriteMovieRepositoryImpl
import com.example.smartestmovieapp.data.repository.movieDetails.MovieDetailsRepositoryImpl
import com.example.smartestmovieapp.data.repository.popular.PopularRepositoryImpl
import com.example.smartestmovieapp.data.repository.topRated.TopRatedRepositoryImpl
import com.example.smartestmovieapp.data.repository.upcoming.UpcomingRepositoryImpl
import com.example.smartestmovieapp.data.responses.Resource
import com.example.smartestmovieapp.databinding.ActivityCategoryBinding
import com.example.smartestmovieapp.presentation.screens.VM.*
import com.example.smartestmovieapp.presentation.screens.adapter.Movie
import com.example.smartestmovieapp.presentation.screens.adapter.MoviesAdapter
import com.example.smartestmovieapp.presentation.screens.adapter.OpenTrailerListener
import com.example.smartestmovieapp.presentation.screens.cinema.CinemaAdapter
import com.example.smartestmovieapp.presentation.screens.cinema_details.CinemaDetailsActivity
import com.example.smartestmovieapp.presentation.screens.home.HomeScreenActivity
import com.example.smartestmovieapp.presentation.screens.map.MapsActivity
import com.example.smartestmovieapp.presentation.screens.movie_details.MovieDetailsScreenActivity
import com.example.smartestmovieapp.presentation.screens.movie_details.OnMovieCardClickListener
import dagger.android.AndroidInjection
import javax.inject.Inject

class CategoryScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var adapter: MoviesAdapter
    private lateinit var cinemaAdapter: CinemaAdapter
    private var movieList = ArrayList<Movie>()
    private var cinemaList = ArrayList<CinemaEntity>()
    private var searchTitle: String? = null
    private var searchFavorite: String? = null

    @Inject
    lateinit var popularRepositoryImpl: PopularRepositoryImpl

    @Inject
    lateinit var topRatedRepositoryImpl: TopRatedRepositoryImpl

    @Inject
    lateinit var upcomingRepositoryImpl: UpcomingRepositoryImpl

    @Inject
    lateinit var cinemaRepositoryImpl: CinemaRepositoryImpl

    @Inject
    lateinit var favoritesRepositoryImpl: FavoriteMovieRepositoryImpl

    @Inject
    lateinit var movieDetailsRepositoryImpl: MovieDetailsRepositoryImpl

    @Inject
    lateinit var favoritesVM: FavoritesVM

    @Inject
    lateinit var popularVM: PopularVM

    @Inject
    lateinit var topRatedVM: TopRatedVM

    @Inject
    lateinit var upcomingVM: UpcomingVM

    @Inject
    lateinit var trailerVM: TrailerVM

    @Inject
    lateinit var cinemaVM: CinemaVM

    private val CHOOSER_MESSAGE = "How would you like to open the link?"
    private lateinit var intentOpenTrailer: Intent
    private lateinit var intentChooser: Intent

    val movieCardClickListener = object : OnMovieCardClickListener {
        override fun onClick(movie: Movie) {
            val intentMovieDetails =
                Intent(this@CategoryScreenActivity, MovieDetailsScreenActivity::class.java)
            intentMovieDetails.putExtra("MOVIE", movie)
            startActivity(intentMovieDetails)
        }
    }
    val openTrailerListener = object : OpenTrailerListener {
        override fun onClick(movieId: Int) {
            getTrailer(movieId)
        }

    }

    private val cinemaCardClickListener = object : CinemaAdapter.onCinemaCardClicked {
        override fun onClick(cinema: CinemaEntity) {
            openCinemaDetails(cinema)
        }

    }

    private val seeInMapListener = object : CinemaAdapter.onCinemaCardClicked {
        override fun onClick(cinema: CinemaEntity) {
            openMapsActivity("CINEMA", cinema)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setTheme()
        super.onCreate(savedInstanceState)
        //init the binding
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hide the search from the toolbar
        binding.homeScreenToolbar.editTextSearch.visibility = EditText.GONE

        initIntentChooser()
        //set the status bar
        setTransparentStatusBar()

        //set the title
        val categoryTitle = intent.getStringExtra("TITLE")
        searchTitle = intent.getStringExtra("SEARCH")
        searchFavorite = intent.getStringExtra("FAVORITE")

        binding.title.text = categoryTitle

        when (categoryTitle) {
            "Popular Movies" -> initPopularList()
            "Top rated" -> initTopRatedList()
            "Cinemas" -> initCinemaList()
            "Favorites" -> initFavoritesList()
            else -> initUpcomingList()
        }
        //display the movie list
        binding.recyclerViewCategory.layoutManager = LinearLayoutManager(baseContext)
        //Toast.makeText(baseContext, categoryTitle, Toast.LENGTH_SHORT).show()
        //categoryTitle?.let { initAdapter(categoryTitle) }
        initAdapter(categoryTitle!!)

        //Setup onClickListeners
        initConnections()
    }

    private fun initAdapter(categoryTitle: String) {
        when (categoryTitle) {
            "Cinemas" -> {
                cinemaAdapter = CinemaAdapter(
                    cinemaList,
                    cinemaCardClickListener,
                    seeInMapListener,
                    R.layout.cardview_cinema_wide_version
                )
                binding.recyclerViewCategory.adapter = cinemaAdapter
            }
            else -> {
                adapter = MoviesAdapter(
                    movieList,
                    R.layout.cardview_movie,
                    movieCardClickListener,
                    openTrailerListener
                )
                binding.recyclerViewCategory.adapter = adapter
            }
        }
    }


    private fun initIntentChooser() {
        intentOpenTrailer = Intent(Intent.ACTION_VIEW)
        intentChooser = Intent.createChooser(intentOpenTrailer, CHOOSER_MESSAGE)
    }

    private fun Activity.setTransparentStatusBar() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
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

    private fun isNetworkAvailable(activity: FragmentActivity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun initCinemaList() {
        cinemaList.clear()
        cinemaVM.cinemaList.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (!searchTitle.isNullOrEmpty()) {
                        it.data?.forEach {
                            if (it.name.contains(searchTitle!!, true)) cinemaList.add(it)
                        }
                        cinemaAdapter.notifyDataSetChanged()

                        if (cinemaList.size == 0) {
                            binding.recyclerViewCategory.visibility = View.GONE
                            binding.noResult.visibility = View.VISIBLE
                        }
                    } else {
                        it.data?.forEach {
                            cinemaList.add(it)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun initFavoritesList() {
        favoritesVM.favoritesList.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let {
                        for (favoriteMovieEntity in it) {
                            if (favoriteMovieEntity.title.contains(searchFavorite!!, true)) {
                                movieList.add(
                                    Movie(
                                        favoriteMovieEntity.id,
                                        favoriteMovieEntity.title,
                                        favoriteMovieEntity.vote_average.toString(),
                                        favoriteMovieEntity.vote_average,
                                        favoriteMovieEntity.genres,
                                        favoriteMovieEntity.runtime,
                                        favoriteMovieEntity.image_url
                                    )
                                )
                            }
                            adapter.notifyDataSetChanged()
                        }
                        if (movieList.size == 0) {
                            binding.recyclerViewCategory.visibility = View.GONE
                            binding.noResult.visibility = View.VISIBLE
                            binding.noResult.text = "No movie found"
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun initPopularList() {
        popularVM.popularLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.data?.forEach {
                        val movieDetails = movieDetailsRepositoryImpl.getMovieDetails(it.popularID)
                        movieDetails.blockingSubscribe { md ->
                            movieList.add(
                                Movie(
                                    it.popularID,
                                    it.title,
                                    it.vote_average.toString(),
                                    it.vote_average,
                                    md.genres,
                                    md.runtime,
                                    it.image_url
                                )
                            )
                        }

                    }
                    adapter.notifyDataSetChanged()
                    it.data?.let { it1 -> popularRepositoryImpl.localDataSource.savePopulars(it1) }

                }
                is Resource.Error -> {
                    Toast.makeText(baseContext, "error: ${it.message}", Toast.LENGTH_LONG)
                        .show()
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun initTopRatedList() {
        topRatedVM.topRatedLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.data?.forEach {
                        val movieDetails =
                            movieDetailsRepositoryImpl.getMovieDetails(it.top_ratedID)
                        movieDetails.blockingSubscribe { md ->
                            movieList.add(
                                Movie(
                                    it.top_ratedID,
                                    it.title,
                                    it.vote_average.toString(),
                                    it.vote_average,
                                    md.genres,
                                    md.runtime,
                                    it.image_url
                                )
                            )
                        }

                    }
                    adapter.notifyDataSetChanged()
                    it.data?.let { it1 -> topRatedRepositoryImpl.localDataSource.saveTopRated(it1) }

                }
                is Resource.Error -> {
                    Toast.makeText(baseContext, "error: ${it.message}", Toast.LENGTH_LONG)
                        .show()
                }
                is Resource.Loading -> {
                }
            }
        }
    }

    private fun initUpcomingList() {
        upcomingVM.upComingLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.data?.forEach {
                        movieList.add(
                            Movie(
                                it.upcomingID,
                                it.title,
                                it.vote_average.toString(),
                                it.vote_average,
                                mutableListOf(),
                                0,
                                it.image_url
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
                    it.data?.let { it1 -> upcomingRepositoryImpl.localDataSource.saveUpcoming(it1) }

                }
                is Resource.Error -> {
                    Toast.makeText(
                        baseContext,
                        "error: ${it.message}",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                is Resource.Loading -> {
                }
            }
        }
    }

    private fun getTrailer(movieId: Int) {
        if (isNetworkAvailable(this)) {
            trailerVM.getTrailer(movieId)
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


    //Setup onClickListeners
    private fun initConnections()
    {
/*        binding.homeScreenToolbar.imageViewBell.setOnClickListener {
            val intent = Intent(this, HomeScreenActivity::class.java)
            intent.putExtra("openNotifications", "categoryScreen")
            startActivity(intent)
        }*/
    }
}
