package com.example.smartestmovieapp.data.repository.genres

import com.example.smartestmovieapp.data.local.LocalDataSource
import com.example.smartestmovieapp.data.local.model.genres.GenreEntity
import com.example.smartestmovieapp.data.network.GenreApi
import com.example.smartestmovieapp.data.responses.genres.toGenreEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(
    val genreApi: GenreApi,
    val localDataSource: LocalDataSource
) : GenreRepository {

    override fun getGenres(): Single<List<GenreEntity>> {
        return genreApi.getGenres().subscribeOn(Schedulers.io())
            .map { genreResponse -> genreResponse.genres.map { genresResponseItem -> genresResponseItem.toGenreEntity() } }
            .observeOn(Schedulers.io())
            .doOnSuccess { localDataSource.saveAllGenres(it) }
            .onErrorResumeNext {
                localDataSource.getAllGenres()
            }
    }

}