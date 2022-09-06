package com.example.smartestmovieapp.data.repository.popular

import com.example.smartestmovieapp.data.local.LocalDataSource
import com.example.smartestmovieapp.data.local.model.popular.PopularEntity
import com.example.smartestmovieapp.data.network.PopularApi
import com.example.smartestmovieapp.data.responses.popular.toPopularEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class PopularRepositoryImpl @Inject constructor(
    val popularApi: PopularApi,
    val localDataSource: LocalDataSource
) : PopularRepository {

    override fun getPopulars(): Single<List<PopularEntity>> {
        return popularApi.getPopular().subscribeOn(Schedulers.io())
            .map { popularResponse -> popularResponse.results.map { popularResponseItem -> popularResponseItem.toPopularEntity() } }
            .observeOn(Schedulers.io())
            .doOnSuccess { localDataSource.savePopulars(it) }
            .onErrorResumeNext {
                localDataSource.getAllPopulars()
            }
    }
}