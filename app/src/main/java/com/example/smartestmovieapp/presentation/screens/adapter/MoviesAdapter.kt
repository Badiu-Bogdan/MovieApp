package com.example.smartestmovieapp.presentation.screens.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.presentation.screens.movie_details.OnMovieCardClickListener
import com.willy.ratingbar.ScaleRatingBar

class MoviesAdapter(
    val movieList: List<Movie>,
    val layoutId: Int,
    val movieCardClickListener: OnMovieCardClickListener,
    val openTrailerListener: OpenTrailerListener
) :
    RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movieList.get(position))
    }

    override fun getItemCount() = movieList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var title: TextView
        private var year: TextView? = null
        private lateinit var image: ImageView
        private var rating: ScaleRatingBar? = null
        private var watchTrailer: Button? = null
        private var genres: TextView? = null
        private var runtime: TextView? = null

        fun bind(movieItem: Movie) {

            title = view.findViewById(R.id.title)
            year = view.findViewById(R.id.year)
            image = view.findViewById(R.id.image)
            rating = view.findViewById(R.id.rating)
            watchTrailer = view.findViewById(R.id.watch_trailer_button_home)
            genres = view.findViewById(R.id.genre)
            runtime = view.findViewById(R.id.duration)

            title.text = movieItem.title
            year?.text = movieItem.details
            rating?.apply {
                rating = (movieItem.rating / 2).toFloat()
                this.isClickable = false
            }

            //Genres
            val stringBuilder = StringBuilder()

            genres?.let {
                Log.v("TEST", "SIZE LIST: " + movieItem.genres.size.toString())
                movieItem.genres.forEach {
                    stringBuilder.append(it).append(", ")
                }
                if (stringBuilder.length > 2) {
                    stringBuilder.deleteAt(stringBuilder.lastIndex)
                    stringBuilder.deleteAt(stringBuilder.lastIndex)
                }
                genres?.text = stringBuilder.toString()
            }

            //Duration
            runtime?.let {
                if (movieItem.runtime != 0) {
                    stringBuilder.clear()
                    stringBuilder.append(movieItem.runtime / 60).append("h ")
                        .append(movieItem.runtime % 60).append("m")
                    it.text = stringBuilder.toString()
                } else {
                    it.text = ""
                }
            }

            //Image
            Glide.with(view)
                .load(movieItem.imageUrl)
                .placeholder(R.drawable.default_movie_image)
                .into(image)

            view.setOnClickListener { movieCardClickListener.onClick(movieItem) }

            watchTrailer?.apply {
                setOnClickListener { openTrailerListener.onClick(movieItem.id) }
            }
        }
    }
}
