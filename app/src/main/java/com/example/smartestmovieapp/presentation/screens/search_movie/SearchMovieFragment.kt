package com.example.smartestmovieapp.presentation.screens.search_movie

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.repository.movieDetails.MovieDetailsRepositoryImpl
import com.example.smartestmovieapp.data.repository.search_movie.SearchMovieRepositoryImpl
import com.example.smartestmovieapp.data.responses.Resource
import com.example.smartestmovieapp.databinding.FragmentSearchMovieBinding
import com.example.smartestmovieapp.presentation.screens.VM.SearchMovieVM
import com.example.smartestmovieapp.presentation.screens.VM.TrailerVM
import com.example.smartestmovieapp.presentation.screens.adapter.Movie
import com.example.smartestmovieapp.presentation.screens.adapter.MoviesAdapter
import com.example.smartestmovieapp.presentation.screens.adapter.OpenTrailerListener
import com.example.smartestmovieapp.presentation.screens.movie_details.MovieDetailsScreenActivity
import com.example.smartestmovieapp.presentation.screens.movie_details.OnMovieCardClickListener
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SearchMovieFragment : Fragment() {
    private lateinit var binding: FragmentSearchMovieBinding
    private lateinit var adapter: MoviesAdapter
    private var movieList = ArrayList<Movie>()

    @Inject
    lateinit var movieDetailsRepositoryImpl: MovieDetailsRepositoryImpl

    @Inject
    lateinit var searchMovieRepository: SearchMovieRepositoryImpl

    @Inject
    lateinit var searchMovieVM: SearchMovieVM

    @Inject
    lateinit var trailerVM: TrailerVM

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

    val openTrailerListener = object : OpenTrailerListener {
        override fun onClick(movieId: Int) {
            getTrailer(movieId)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val action = SearchMovieFragmentDirections.goBackToHomeScreen()
                    Navigation.findNavController(requireView()).navigate(action)
                }
            })

        initIntentChooser()
        binding = FragmentSearchMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchMovies()

        binding.recyclerViewCategory.layoutManager = LinearLayoutManager(context)
        adapter = MoviesAdapter(
            movieList,
            R.layout.cardview_movie,
            movieCardClickListener,
            openTrailerListener
        )
        binding.recyclerViewCategory.adapter = adapter

    }

    private fun isNetworkAvailable(activity: FragmentActivity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun initIntentChooser() {
        intentOpenTrailer = Intent(Intent.ACTION_VIEW)
        intentChooser = Intent.createChooser(intentOpenTrailer, CHOOSER_MESSAGE)
    }

    private fun getTrailer(movie: Movie) {
        trailerVM.getTrailer(movie.id)
        trailerVM.trailerLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(it.data?.video_url)
                    startActivity(intent)
                }
                is Resource.Error -> {
                    Toast.makeText(requireActivity(), "error: ${it.message}", Toast.LENGTH_LONG)
                        .show()
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun searchMovies() {

        val searchedTitle = arguments?.getString("title") ?: ""
        if (searchedTitle.isNotEmpty()) {
            searchMovieVM.getMovies(searchedTitle)
            searchMovieVM.movieLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        if (it.data?.size!! > 0) {
                            it.data.forEach { movieSearched ->
                                if (movieSearched.title.contains(searchedTitle)) {
                                    val movieDetails =
                                        movieDetailsRepositoryImpl.getMovieDetails(movieSearched.id)
                                    movieDetails.blockingSubscribe {
                                        movieList.add(
                                            Movie(
                                                movieSearched.id,
                                                movieSearched.title,
                                                movieSearched.vote_average.toString(),
                                                movieSearched.vote_average,
                                                it.genres,
                                                it.runtime,
                                                movieSearched.image_url

                                            )
                                        )
                                    }
                                }
                            }
                        }
                        if (movieList.size == 0)
                            binding.moviesNotFound.visibility = View.VISIBLE

                        adapter.notifyDataSetChanged()
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