package com.example.smartestmovieapp.data.network

import com.example.smartestmovieapp.data.responses.movie_details.MovieDetailsResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieDetailsApi {

    @GET("movie/{movie_id}?api_key=ac88baf0a8a22a8c822068dfa1ab0689&language=en-US")
    fun getMovieDetails(@Path("movie_id") movie_id: Int): Single<MovieDetailsResponse>

}