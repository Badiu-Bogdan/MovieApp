package com.example.smartestmovieapp.presentation.screens.movie_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.presentation.screens.adapter.Movie


class MoviesAdapterWithListener(
    val movieList: List<Movie>,
    val layoutId: Int,
    val movieCardClickListener: OnMovieCardClickListener
) :
    RecyclerView.Adapter<MoviesAdapterWithListener.MovieListenerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MoviesAdapterWithListener.MovieListenerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)
        return MovieListenerViewHolder(view)
    }

    override fun getItemCount(): Int = movieList.size

    override fun onBindViewHolder(holder: MovieListenerViewHolder, position: Int) {
        holder.bind(movieList.get(position))
    }

    inner class MovieListenerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var title: TextView
        private var year: TextView? = null
        private lateinit var image: ImageView

        fun bind(movieItem: Movie) {
            title = view.findViewById(R.id.title)
            year = view.findViewById(R.id.year)
            image = view.findViewById(R.id.image)

            title.text = movieItem.title
            year?.text = movieItem.details
            Glide.with(view)
                .load(movieItem.imageUrl)
                .placeholder(R.drawable.default_movie_image)
                .into(image)
            view.setOnClickListener { movieCardClickListener.onClick(movieItem) }
        }
    }

}



