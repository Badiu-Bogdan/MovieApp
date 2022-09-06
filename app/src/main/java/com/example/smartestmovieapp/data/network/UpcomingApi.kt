package com.example.smartestmovieapp.data.network

import com.example.smartestmovieapp.data.responses.upcoming.UpcomingResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface UpcomingApi {

    @GET("movie/upcoming?api_key=ac88baf0a8a22a8c822068dfa1ab0689&language=en-US&page=1")
    fun getUpComing(): Single<UpcomingResponse>
}