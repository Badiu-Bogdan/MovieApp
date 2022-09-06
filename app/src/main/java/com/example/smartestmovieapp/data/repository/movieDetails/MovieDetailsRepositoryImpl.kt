package com.example.smartestmovieapp.data.repository.movieDetails

import com.example.smartestmovieapp.data.local.LocalDataSource
import com.example.smartestmovieapp.data.local.model.movie_details.MovieDetailsEntity
import com.example.smartestmovieapp.data.network.MovieDetailsApi
import com.example.smartestmovieapp.data.responses.movie_details.toMovieDetailsEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MovieDetailsRepositoryImpl @Inject constructor(
    val movieDetailsApi: MovieDetailsApi,
    val localDataSource: LocalDataSource
) : MovieDetailsRepository {

    companion object {
        val TAG = MovieDetailsRepository::class.java.simpleName
    }

    override fun getMovieDetails(id: Int): Single<MovieDetailsEntity> {
        return movieDetailsApi.getMovieDetails(id).subscribeOn(Schedulers.io())
            .map { movieDetailsResponse -> movieDetailsResponse.toMovieDetailsEntity() }
            .observeOn(Schedulers.io())
            .doOnSuccess { localDataSource.saveMovieDetails(it) }
            .onErrorResumeNext {
                localDataSource.getMovieDetails(id)
            }

    }
}