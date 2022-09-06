package com.example.smartestmovieapp.data.network

import com.example.smartestmovieapp.data.responses.BitCoinResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface LocationApi {

    @GET("/trades?since=5")
    fun getTrades(): Observable<BitCoinResponse>
}