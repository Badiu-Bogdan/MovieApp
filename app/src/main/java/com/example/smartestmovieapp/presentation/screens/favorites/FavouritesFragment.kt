package com.example.smartestmovieapp.presentation.screens.favorites

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.local.model.favorites.FavoriteMovieEntity
import com.example.smartestmovieapp.data.repository.favorites.FavoriteMovieRepository
import com.example.smartestmovieapp.data.responses.Resource
import com.example.smartestmovieapp.databinding.FragmentFavoritesBinding
import com.example.smartestmovieapp.presentation.screens.VM.FavoritesVM
import com.example.smartestmovieapp.presentation.screens.VM.TrailerVM
import com.example.smartestmovieapp.presentation.screens.adapter.Movie
import com.example.smartestmovieapp.presentation.screens.adapter.MoviesAdapter
import com.example.smartestmovieapp.presentation.screens.adapter.OpenTrailerListener
import com.example.smartestmovieapp.presentation.screens.category.CategoryScreenActivity
import com.example.smartestmovieapp.presentation.screens.movie_details.MovieDetailsScreenActivity
import com.example.smartestmovieapp.presentation.screens.movie_details.OnMovieCardClickListener
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import javax.inject.Inject


class FavouritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private var movieList = ArrayList<Movie>()
    private lateinit var adapter: MoviesAdapter

    @Inject
    lateinit var repository: FavoriteMovieRepository

    @Inject
    lateinit var favoritesVM: FavoritesVM

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

    val swipeGesture = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val itemPosition = viewHolder.bindingAdapterPosition
            val movie = movieList.get(itemPosition)
            movieList.removeAt(itemPosition)
            adapter.notifyDataSetChanged()
            displaySnackbar(movie, itemPosition)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            var decorator = RecyclerViewSwipeDecorator(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )

            drawBackground(c, viewHolder, decorator)
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

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
        binding = FragmentFavoritesBinding.inflate(layoutInflater, container, false)
        binding.homeScreenToolbar.editTextSearch.hint = "Search favourite movie..."
        initIntentChooser()
        setupButtonConnection()
        searchFavoriteMovie()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()

        binding.recyclerViewCategory.layoutManager = LinearLayoutManager(requireContext())
        adapter = MoviesAdapter(
            movieList,
            R.layout.cardview_movie,
            movieCardClickListener,
            openTrailerListener
        )
        binding.recyclerViewCategory.adapter = adapter

        val itemSwipe = ItemTouchHelper(swipeGesture)
        itemSwipe.attachToRecyclerView(binding.recyclerViewCategory)
    }

    private fun searchFavoriteMovie() {
        binding.homeScreenToolbar.editTextSearch.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                val searchedMovie = binding.homeScreenToolbar.editTextSearch.text.toString()
                val intent = Intent(requireActivity(), CategoryScreenActivity::class.java)
                intent.putExtra("TITLE", "Favorites")
                intent.putExtra("FAVORITE", searchedMovie)
                startActivity(intent)
            }
            false
        }
    }

    private fun initIntentChooser() {
        intentOpenTrailer = Intent(Intent.ACTION_VIEW)
        intentChooser = Intent.createChooser(intentOpenTrailer, CHOOSER_MESSAGE)
    }

    private fun initData() {
        favoritesVM.favoritesList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    movieList.clear()
                    it.data?.forEach { item -> addToList(item) }
                    adapter.notifyDataSetChanged()
                }
                is Resource.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong when retrieving favorites",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun addToList(favoriteMovie: FavoriteMovieEntity) {
        movieList.add(
            Movie(
                favoriteMovie.id,
                favoriteMovie.title,
                favoriteMovie.description,
                favoriteMovie.vote_average,
                favoriteMovie.genres,
                favoriteMovie.runtime,
                favoriteMovie.image_url
            )
        )
    }

    private fun drawBackground(
        canvas: Canvas,
        viewHolder: RecyclerView.ViewHolder,
        decorator: RecyclerViewSwipeDecorator
    ) {
        val cornerRadius = 31f
        val itemView = viewHolder.itemView
        val paint = Paint()
        val swipeLeft = RectF(
            itemView.left.toFloat(),
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat()
        )

        paint.apply {
            val themePrefs =
                requireContext().getSharedPreferences("chosenTheme", Context.MODE_PRIVATE)
            val defaultThemeChosen = themePrefs.getBoolean("Default", true)
            if (defaultThemeChosen) {
                color = ContextCompat.getColor(requireContext(), R.color.light_blue)
            } else {
                color = ContextCompat.getColor(requireContext(), R.color.light_green)
            }

            isAntiAlias = true
        }


        canvas.drawRoundRect(swipeLeft, cornerRadius, cornerRadius, paint)
        decorator.setActionIconId(R.drawable.ic_trash_with_text)
        decorator.decorate()
    }

    private fun displaySnackbar(movie: Movie, position: Int) {

        var actionCompleted = false

        val snackbar = Snackbar.make(
            binding.root,
            "\"${movie.title}\" removed from Favorites",
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction("UNDO") {
            movieList.add(position, movie)
            actionCompleted = true
            adapter.notifyDataSetChanged()
        }
        snackbar.setActionTextColor(Color.WHITE)

        snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onShown(transientBottomBar: Snackbar?) {
                super.onShown(transientBottomBar)
            }

            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (!actionCompleted) {
                    favoritesVM.removeMovieFromfavorites(movie.id)
                }
            }
        })
        snackbar.show()
    }

    private fun isNetworkAvailable(activity: FragmentActivity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun setupButtonConnection() {
        binding.homeScreenToolbar.imageViewBell.setOnClickListener { goToNotificationsScreen() }
    }

    private fun goToNotificationsScreen() {
        val bundle = Bundle()
        bundle.putBoolean("networkConnection", isNetworkAvailable(requireActivity()))
        Navigation.findNavController(binding.root)
            .navigate(R.id.fromFavouritesToNotifications, bundle)
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