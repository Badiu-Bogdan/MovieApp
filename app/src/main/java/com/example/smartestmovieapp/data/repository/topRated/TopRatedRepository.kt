package com.example.smartestmovieapp.data.repository.topRated

import com.example.smartestmovieapp.data.local.model.top_rated.TopRatedEntity
import io.reactivex.rxjava3.core.Single

interface TopRatedRepository {

    fun getAllTopRated(): Single<List<TopRatedEntity>>
}