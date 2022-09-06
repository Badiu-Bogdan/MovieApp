package com.example.smartestmovieapp.data.repository.topRated

import com.example.smartestmovieapp.data.local.LocalDataSource
import com.example.smartestmovieapp.data.local.model.top_rated.TopRatedEntity
import com.example.smartestmovieapp.data.network.TopRatedApi
import com.example.smartestmovieapp.data.responses.topRated.toTopRatedEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


class TopRatedRepositoryImpl @Inject constructor(
    val topRatedApi: TopRatedApi,
    val localDataSource: LocalDataSource
) : TopRatedRepository {

    override fun getAllTopRated(): Single<List<TopRatedEntity>> {
        return topRatedApi.getTopRated().subscribeOn(Schedulers.io())
            .map { topRatedResponse ->
                topRatedResponse.results.map { topRatedResponseItem -> topRatedResponseItem.toTopRatedEntity() }
            }
                    .observeOn(Schedulers.io())
                    .doOnSuccess { localDataSource.saveTopRated(it) }
                    .onErrorResumeNext {
                        localDataSource.getAllTopRated()
                    }
            }
    }