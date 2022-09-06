package com.example.smartestmovieapp.data.repository.movieDetails

import com.example.smartestmovieapp.data.local.model.movie_details.MovieDetailsEntity
import io.reactivex.rxjava3.core.Single

interface MovieDetailsRepository {

    fun getMovieDetails(id: Int): Single<MovieDetailsEntity>

}