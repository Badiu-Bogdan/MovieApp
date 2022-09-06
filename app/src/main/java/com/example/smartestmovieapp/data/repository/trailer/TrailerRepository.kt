package com.example.smartestmovieapp.data.repository.trailer

import com.example.smartestmovieapp.data.local.model.trailer.TrailerEntity
import io.reactivex.rxjava3.core.Single

interface TrailerRepository {

    fun getTrailers(id: Int): Single<TrailerEntity>
}