package com.example.smartestmovieapp.data.repository.popular

import com.example.smartestmovieapp.data.local.model.popular.PopularEntity
import io.reactivex.rxjava3.core.Single

interface PopularRepository {

    fun getPopulars(): Single<List<PopularEntity>>
}