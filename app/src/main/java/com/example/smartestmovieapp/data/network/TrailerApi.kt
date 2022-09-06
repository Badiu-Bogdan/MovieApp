package com.example.smartestmovieapp.data.network

import com.example.smartestmovieapp.data.responses.trailer.TrailerResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface TrailerApi {

    @GET("movie/{movie_id}/videos?api_key=ac88baf0a8a22a8c822068dfa1ab0689&language=en-US")
    fun getTrailer(@Path("movie_id") movie_id: Int): Single<TrailerResponse>
}