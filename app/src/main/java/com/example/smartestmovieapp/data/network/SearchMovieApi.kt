package com.example.smartestmovieapp.data.network

import com.example.smartestmovieapp.data.responses.search_movie.SearchMovieResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchMovieApi {
    @GET("search/movie?api_key=ac88baf0a8a22a8c822068dfa1ab0689&language=en-US&query=&page=1&include_adult=false")
    fun getMovies(@Query("query") title: String): Single<SearchMovieResponse>
}