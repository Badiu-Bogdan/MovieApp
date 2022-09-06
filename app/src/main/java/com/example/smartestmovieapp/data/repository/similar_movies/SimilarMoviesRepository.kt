package com.example.smartestmovieapp.data.repository.similar_movies

import com.example.smartestmovieapp.data.local.model.similar_movies.SimilarMovie
import io.reactivex.rxjava3.core.Single

interface SimilarMoviesRepository {

    fun getMovies(id: Int): Single<List<SimilarMovie>>
}