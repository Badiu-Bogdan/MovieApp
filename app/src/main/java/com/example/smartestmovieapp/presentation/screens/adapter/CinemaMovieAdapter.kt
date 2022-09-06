package com.example.smartestmovieapp.presentation.screens.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.local.model.cinema.RunningMovieDetails
import com.example.smartestmovieapp.presentation.screens.movie_details.OnMovieCardClickListener
import com.willy.ratingbar.ScaleRatingBar

class CinemaMovieAdapter(
    var movieList: ArrayList<RunningMovieDetails>,
    val movieCardClickListener: OnMovieCardClickListener
) :
    RecyclerView.Adapter<CinemaMovieAdapter.ViewHolder>() {

    fun addMovie(movie: RunningMovieDetails) {
        movieList.add(movie)
        notifyDataSetChanged()
    }
/*    fun updateAdapterList(newList : List<RunningMovieDetails>){
        movieList = newList
        this.notifyDataSetChanged()
    }*/

    fun clearList() {
        movieList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CinemaMovieAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cardview_movie_cinema, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CinemaMovieAdapter.ViewHolder, position: Int) {
        holder.bind(movieList[position])
    }

    override fun getItemCount(): Int = movieList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var movieName: TextView
        private lateinit var movieGenre: TextView
        private lateinit var movieDuration: TextView
        private lateinit var movieRating: ScaleRatingBar
        private lateinit var movieImage: ImageView
        private lateinit var runOne: Button
        private lateinit var runTwo: Button
        private lateinit var runThree: Button

        fun bind(movie: RunningMovieDetails) {
            movieName = view.findViewById(R.id.title)
            movieGenre = view.findViewById(R.id.genre)
            movieDuration = view.findViewById(R.id.duration)
            movieRating = view.findViewById(R.id.rating)
            movieImage = view.findViewById(R.id.image)
            runOne = view.findViewById(R.id.runOne)
            runTwo = view.findViewById(R.id.runTwo)
            runThree = view.findViewById(R.id.runThree)

            movieName.text = movie.title

            val stringBuilder = StringBuilder()
            movie.genres.forEach {
                stringBuilder.append(it).append(", ")
            }
            stringBuilder.deleteAt(stringBuilder.lastIndex)
            stringBuilder.deleteAt(stringBuilder.lastIndex)
            movieGenre.text = stringBuilder.toString()

            stringBuilder.clear()
            stringBuilder.append(movie.runtime / 60).append("h ")
                .append(movie.runtime % 60).append("m")

            movieDuration.text = stringBuilder.toString()
            movieRating.rating = (movie.vote_average / 2).toFloat()

            Glide.with(view)
                .load(movie.image_url)
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.default_movie_image)
//                .onlyRetrieveFromCache(true)
                //.error(R.drawable.default_movie_image)
                .into(movieImage)

            runOne.text = movie.runningHours.get(0)
            runTwo.text = movie.runningHours.get(1)
            runThree.text = movie.runningHours.get(2)
            view.setOnClickListener {
                movieCardClickListener.onClick(
                    Movie(
                        movie.movie_detailsID,
                        movie.title,
                        "",
                        movie.vote_average,
                        movie.genres,
                        movie.runtime,
                        movie.image_url
                    )
                )
            }
        }
    }
}