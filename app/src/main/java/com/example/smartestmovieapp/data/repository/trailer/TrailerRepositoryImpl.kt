package com.example.smartestmovieapp.data.repository.trailer

import com.example.smartestmovieapp.data.local.LocalDataSource
import com.example.smartestmovieapp.data.local.model.trailer.TrailerEntity
import com.example.smartestmovieapp.data.network.TrailerApi
import com.example.smartestmovieapp.data.responses.trailer.toTrailerEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class TrailerRepositoryImpl @Inject constructor(
    val trailerApi: TrailerApi,
    val localDataSource: LocalDataSource
) : TrailerRepository {

    companion object {
        val TAG = TrailerRepository::class.java.simpleName
    }

    override fun getTrailers(id: Int): Single<TrailerEntity> {
        return trailerApi.getTrailer(id).subscribeOn(Schedulers.io())
            .map { trailerResponse -> trailerResponse.toTrailerEntity() }
            .observeOn(Schedulers.io())
//            .doOnSuccess { localDataSource.saveTrailer(it)}
            .onErrorResumeNext {
                localDataSource.getTrailer(id)
            }

        //De facut mai incolo pt in Error
    }
}