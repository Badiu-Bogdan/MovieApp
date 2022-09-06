package com.example.smartestmovieapp.data.network

import com.example.smartestmovieapp.data.responses.popular.PopularResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface PopularApi {
/*    @GET("/&page=1")
    fun getPopular(@Query("api_key") apiKey:String = "ac88baf0a8a22a8c822068dfa1ab0689", @Query("language") language: String="en-US"): Observable<PopularResponse>*/

    @GET("movie/popular?api_key=ac88baf0a8a22a8c822068dfa1ab0689&language=en-US&page=1")
    fun getPopular(): Single<PopularResponse>

}