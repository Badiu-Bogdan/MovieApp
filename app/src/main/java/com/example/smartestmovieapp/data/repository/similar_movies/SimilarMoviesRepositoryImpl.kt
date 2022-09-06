package com.example.smartestmovieapp.data.repository.similar_movies

import com.example.smartestmovieapp.data.local.LocalDataSource
import com.example.smartestmovieapp.data.local.model.similar_movies.SimilarMovie
import com.example.smartestmovieapp.data.network.SimilarApi
import com.example.smartestmovieapp.data.responses.similar_movies.toSimilarEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class SimilarMoviesRepositoryImpl @Inject constructor(
    val similarApi: SimilarApi,
    val localDataSource: LocalDataSource
) : SimilarMoviesRepository {

    override fun getMovies(id: Int): Single<List<SimilarMovie>> {
        return similarApi.getSimilarMovies(id).subscribeOn(Schedulers.io())
            .map { similarResponse -> similarResponse.results.map { similarMovieResponseItem -> similarMovieResponseItem.toSimilarEntity() } }
            .observeOn(Schedulers.io())
            .onErrorResumeNext {
                localDataSource.getAllMovies()
            }
    }
}