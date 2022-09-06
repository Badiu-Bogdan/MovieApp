package com.example.smartestmovieapp.data.repository.search_movie

import com.example.smartestmovieapp.data.local.model.search_movie.SearchMovie
import io.reactivex.rxjava3.core.Single

interface SearchMovieRepository {
    fun getMovies(title: String): Single<List<SearchMovie>>
}