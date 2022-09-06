package com.example.smartestmovieapp.presentation.screens.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.local.model.search_movie.SearchMovie
import com.example.smartestmovieapp.data.repository.genres.GenreRepositoryImpl
import com.example.smartestmovieapp.data.repository.movieDetails.MovieDetailsRepositoryImpl
import com.example.smartestmovieapp.data.repository.popular.PopularRepositoryImpl
import com.example.smartestmovieapp.data.repository.search_movie.SearchMovieRepositoryImpl
import com.example.smartestmovieapp.data.repository.topRated.TopRatedRepositoryImpl
import com.example.smartestmovieapp.data.repository.upcoming.UpcomingRepositoryImpl
import com.example.smartestmovieapp.data.responses.Resource
import com.example.smartestmovieapp.databinding.FragmentHomeScreenBinding
import com.example.smartestmovieapp.presentation.screens.VM.*
import com.example.smartestmovieapp.presentation.screens.adapter.Movie
import com.example.smartestmovieapp.presentation.screens.adapter.MoviesAdapter
import com.example.smartestmovieapp.presentation.screens.adapter.OpenTrailerListener
import com.example.smartestmovieapp.presentation.screens.category.CategoryScreenActivity
import com.example.smartestmovieapp.presentation.screens.movie_details.MovieDetailsScreenActivity
import com.example.smartestmovieapp.presentation.screens.movie_details.OnMovieCardClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class HomeScreenFragment: Fragment() {
    private var trailerList = ArrayList<Movie>()
    private var popularList = ArrayList<Movie>()
    private var topRatedList = ArrayList<Movie>()
    private var upcomingList = ArrayList<Movie>()
    private var allMoviesList: List<SearchMovie> = mutableListOf()
    private lateinit var trailerAdapter: MoviesAdapter
    private lateinit var popularAdapter: MoviesAdapter
    private lateinit var topRatedAdapter: MoviesAdapter
    private lateinit var upcomingAdapter: MoviesAdapter
    private lateinit var binding: FragmentHomeScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var popularRepositoryImpl: PopularRepositoryImpl

    @Inject
    lateinit var topRatedRepositoryImpl: TopRatedRepositoryImpl

    @Inject
    lateinit var upcomingRepositoryImpl: UpcomingRepositoryImpl

    @Inject
    lateinit var genreRepositoryImpl: GenreRepositoryImpl

    @Inject
    lateinit var searchRepositoryImpl: SearchMovieRepositoryImpl

    @Inject
    lateinit var movieDetailsRepositoryImpl: MovieDetailsRepositoryImpl

    @Inject
    lateinit var popularVM: PopularVM

    @Inject
    lateinit var topRatedVM: TopRatedVM

    @Inject
    lateinit var upcomingVM: UpcomingVM

    @Inject
    lateinit var trailerVM: TrailerVM

    @Inject
    lateinit var genreVM: GenreVM

    @Inject
    lateinit var movieDetailsVM: MovieDetailsVM

    private val CHOOSER_MESSAGE = "How would you like to open the link?"
    private lateinit var intentOpenTrailer: Intent
    private lateinit var intentChooser: Intent

    val movieCardClickListener = object : OnMovieCardClickListener {
        override fun onClick(movie: Movie) {
            val intentMovieDetails =
                Intent(requireActivity(), MovieDetailsScreenActivity::class.java)
            intentMovieDetails.putExtra("MOVIE", movie)
            startActivity(intentMovieDetails)
        }
    }

    val watchTrailerClickListener = object : OnMovieCardClickListener {
        override fun onClick(movie: Movie) {
            getTrailer(movie)
        }
    }

    val openTrailerListener = object : OpenTrailerListener {
        override fun onClick(movieId: Int) {
            getTrailer(movieId)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = Firebase.auth
        activity?.window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.transparent)
            WindowCompat.setDecorFitsSystemWindows(this, false)
        }
        //Initialize the intent
        initIntentChooser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("activeCategories", Context.MODE_PRIVATE)
        initScreen(prefs)
        initMoviesDetails()
        //     Toast.makeText(context, firebaseAuth.currentUser?.email, Toast.LENGTH_SHORT).show()
//        binding.signOut.setOnClickListener { signOut() }

    }

    private fun initScreen(prefs: SharedPreferences)
    {
        val trailers = prefs.getBoolean("Trailers", true)
        val popular = prefs.getBoolean("Popular", true)
        val topRated = prefs.getBoolean("Top Rated", true)
        val upComing = prefs.getBoolean("Upcoming", true)

        if (popular) {
            binding.constraintLayoutPopular.isGone = false
            initPopularList()
        } else {
            binding.constraintLayoutPopular.isGone = true
        }

        if (topRated) {
            binding.constraintLayoutTopRated.isGone = false
            initTopRatedList()
        } else {
            binding.constraintLayoutTopRated.isGone = true
        }

        if (upComing) {
            binding.constraintLayoutUpComing.isGone = false
            initUpcomingList()
        } else {
            binding.constraintLayoutUpComing.isGone = true
        }

        if(trailers)
        {
            binding.constraintLayoutTrailers.isGone = false
            initTrailersList()
        }
        else
            binding.constraintLayoutTrailers.isGone = true
        searchMovies()
        initAdapters()
    }

    private fun searchMovies() {

        binding.homeScreenToolbar.editTextSearch.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                val searchedTitle = binding.homeScreenToolbar.editTextSearch.text.toString()
                val bundle = Bundle()
                bundle.putString("title", searchedTitle)
                Navigation.findNavController(binding.root)
                    .navigate(R.id.go_from_home_screen_to_searchMovieFragment, bundle)
            }
            false
        }
    }

    private fun goToCategoryScreen(title: String) {
        val intent = Intent(requireActivity(), CategoryScreenActivity::class.java)
        intent.putExtra("TITLE", title)
        startActivity(intent)
    }

    private fun initRecyclerViews(recyclerView: RecyclerView, adapter: MoviesAdapter) {
        recyclerView.layoutManager =
            LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
        recyclerView.adapter = adapter
    }

    private fun initPopularList() {
        popularVM.popularLiveData.observe(viewLifecycleOwner) { it ->
            when (it) {
                is Resource.Success -> {
                    it.data?.forEach {
                        popularList.add(
                            Movie(
                                it.popularID,
                                it.title,
                                it.vote_average.toString(),
                                it.vote_average,
                                mutableListOf(),
                                0,
                                it.image_url
                            )
                        )
                    }
                    popularAdapter.notifyDataSetChanged()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "error: ${it.message}", Toast.LENGTH_LONG)
                        .show()
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun initIntentChooser() {
        intentOpenTrailer = Intent(Intent.ACTION_VIEW)
        intentChooser = Intent.createChooser(intentOpenTrailer, CHOOSER_MESSAGE)
    }

    private fun initTopRatedList() {
        topRatedVM.topRatedLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.forEach {
                        topRatedList.add(
                            Movie(
                                it.top_ratedID,
                                it.title,
                                it.vote_average.toString(),
                                it.vote_average,
                                mutableListOf(),
                                0,
                                it.image_url
                            )
                        )

                    }
                    topRatedAdapter.notifyDataSetChanged()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "error: ${it.message}", Toast.LENGTH_LONG)
                        .show()
                }
                is Resource.Loading -> {
                }
            }
        }
    }

    private fun initUpcomingList() {
        upcomingVM.upComingLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.forEach {
                        upcomingList.add(
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
                    upcomingAdapter.notifyDataSetChanged()
                }
                is Resource.Error -> {
                    Toast.makeText(
                        requireContext(),
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

    private fun initTrailersList() {
        upcomingVM.upComingLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.forEach {
                        trailerList.add(
                            Movie(
                                it.upcomingID,
                                it.title,
                                "",
                                it.vote_average,
                                mutableListOf(),
                                0,
                                it.image_url
                            )
                        )

                    }
                    trailerAdapter.notifyDataSetChanged()
                }
                is Resource.Error -> {
                    Toast.makeText(
                        requireContext(),
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

    private fun initMoviesDetails() {
        val allMoviesList =
            searchRepositoryImpl.localDataSource.getAllSearchedMovies().blockingGet()
        allMoviesList.forEach { movieEntity ->
            movieDetailsVM.getMovieDetails(movieEntity.id)
        }

    }

    private fun isNetworkAvailable(activity: FragmentActivity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun initAdapters() {

        val internetConnection = isNetworkAvailable(requireActivity())
        binding.homeScreenToolbar.editTextSearch.hint = "Search movie..."

        //trailers part
        trailerAdapter =
            MoviesAdapter(
                trailerList,
                R.layout.cardview_wide_version,
                watchTrailerClickListener,
                openTrailerListener
            )
        initRecyclerViews(binding.recyclerViewTrailers, trailerAdapter)

        //popular part
        popularAdapter =
            MoviesAdapter(
                popularList,
                R.layout.cardview_small_version,
                movieCardClickListener,
                openTrailerListener
            )
        initRecyclerViews(binding.recyclerViewPopular, popularAdapter)

        //top rated part
        topRatedAdapter =
            MoviesAdapter(
                topRatedList,
                R.layout.cardview_small_version,
                movieCardClickListener,
                openTrailerListener
            )
        initRecyclerViews(binding.recyclerViewTopRated, topRatedAdapter)

        //upcoming part
        upcomingAdapter =
            MoviesAdapter(
                upcomingList,
                R.layout.cardview_small_version,
                movieCardClickListener,
                openTrailerListener
            )
        initRecyclerViews(binding.recyclerViewUpcoming, upcomingAdapter)

        binding.viewAllTrailers.setOnClickListener { goToCategoryScreen("Trailers") }
        binding.viewAllPopular.setOnClickListener { goToCategoryScreen("Popular Movies") }
        binding.viewAllUpcoming.setOnClickListener { goToCategoryScreen("Upcoming") }
        binding.viewAllTopRated.setOnClickListener { goToCategoryScreen("Top rated") }
        binding.homeScreenToolbar.imageViewBell.setOnClickListener { goToNotificationsScreen() }

        val favouriteMovie = Movie(
            438631,
            "Dune",
            "4.5",
            4.5,
            mutableListOf(),
            0,
            "https://filmreviews7.files.wordpress.com/2021/08/dune_ver16.jpg?w=509"
        )

        binding.cardFeaturedMovie.setOnClickListener {
            movieCardClickListener.onClick(
                favouriteMovie
            )
        }
        binding.cardviewMovie.watchTrailerButtonHome.setOnClickListener {
            getTrailer(favouriteMovie)
        }

        binding.cardviewMovie.rating.rating = 4.0.toFloat()
    }

    private fun goToNotificationsScreen() {
        val bundle = Bundle()
        bundle.putBoolean("networkConnection", isNetworkAvailable(requireActivity()))
        Navigation.findNavController(binding.root)
            .navigate(R.id.fromHomeScreenToNotifications, bundle)
    }

    private fun getTrailer(movie: Movie) {
        if (isNetworkAvailable(requireActivity())) {
            trailerVM.getTrailer(movie.id)
            trailerVM.trailerLiveData.observeOnce(viewLifecycleOwner) {
                if (it is Resource.Success) {
                    intentOpenTrailer.data = Uri.parse(it.data?.video_url)
                    startActivity(intentChooser)
                }

            }
        } else {
            Toast.makeText(
                requireContext(),
                "No internet connection! Please connect to internet through WIFI or mobile data",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getTrailer(movieId: Int) {
        if (isNetworkAvailable(requireActivity())) {
            trailerVM.getTrailer(movieId)
            trailerVM.trailerLiveData.observeOnce(this) {
                if (it is Resource.Success) {
                    intentOpenTrailer.data = Uri.parse(it.data?.video_url)
                    startActivity(intentChooser)
                }
            }
        } else {
            Toast.makeText(
                requireContext(),
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
}

