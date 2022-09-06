package com.example.smartestmovieapp.data.network

import com.example.smartestmovieapp.data.responses.genres.GenresResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface GenreApi {
    @GET("genre/movie/list?language=en-US&api_key=ac88baf0a8a22a8c822068dfa1ab0689")
    fun getGenres(): Single<GenresResponse>
}