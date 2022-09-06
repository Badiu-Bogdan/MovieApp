package com.example.smartestmovieapp.data.network

import com.example.smartestmovieapp.data.responses.similar_movies.SimilarMovieResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface SimilarApi {

    @GET("https://api.themoviedb.org/3/movie/{movie_id}/similar?api_key=ac88baf0a8a22a8c822068dfa1ab0689&language=en-US&page=1")
    fun getSimilarMovies(@Path("movie_id") movie_id: Int): Single<SimilarMovieResponse>
}