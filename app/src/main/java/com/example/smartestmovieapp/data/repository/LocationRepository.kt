package com.example.smartestmovieapp.data.repository

import com.example.smartestmovieapp.data.responses.BitCoinResponse
import io.reactivex.rxjava3.core.Observable

interface LocationRepository {

    fun getTrades(): Observable<BitCoinResponse>
}