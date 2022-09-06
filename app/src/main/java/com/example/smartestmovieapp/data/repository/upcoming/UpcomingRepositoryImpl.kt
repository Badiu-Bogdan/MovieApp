package com.example.smartestmovieapp.data.repository.upcoming

import com.example.smartestmovieapp.data.local.LocalDataSource
import com.example.smartestmovieapp.data.local.model.upcoming.UpcomingEntity
import com.example.smartestmovieapp.data.network.UpcomingApi
import com.example.smartestmovieapp.data.responses.upcoming.toUpcomingEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class UpcomingRepositoryImpl @Inject constructor(
    val upComingApi: UpcomingApi,
    val localDataSource: LocalDataSource
) : UpcomingRepository {

    override fun getUpComing(): Single<List<UpcomingEntity>> {
        return upComingApi.getUpComing().subscribeOn(Schedulers.io())
            .map { upcomingResponse -> upcomingResponse.results.map { upcomingResponseItem -> upcomingResponseItem.toUpcomingEntity() } }
            .observeOn(Schedulers.io())
            .doOnSuccess { localDataSource.saveUpcoming(it) }
            .onErrorResumeNext {
                localDataSource.getAllUpcoming()
            }
    }
}