package com.example.smartestmovieapp.data.repository.search_movie

import com.example.smartestmovieapp.data.local.LocalDataSource
import com.example.smartestmovieapp.data.local.model.search_movie.SearchMovie
import com.example.smartestmovieapp.data.network.SearchMovieApi
import com.example.smartestmovieapp.data.responses.search_movie.toSearchMovie
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class SearchMovieRepositoryImpl @Inject constructor(
    val searchMovieApi: SearchMovieApi,
    val localDataSource: LocalDataSource
) : SearchMovieRepository {
    override fun getMovies(title: String): Single<List<SearchMovie>> {
        return searchMovieApi.getMovies(title).subscribeOn(Schedulers.io())
            .map { searchResponse -> searchResponse.results.map { searchMovieResponseItem -> searchMovieResponseItem.toSearchMovie() } }
            .observeOn(Schedulers.io())
            .onErrorResumeNext {
                localDataSource.getAllSearchedMovies()
            }
    }
}