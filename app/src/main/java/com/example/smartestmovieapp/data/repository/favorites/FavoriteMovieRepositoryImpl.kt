package com.example.smartestmovieapp.data.repository.favorites

import com.example.smartestmovieapp.data.local.LocalDataSource
import com.example.smartestmovieapp.data.local.model.favorites.FavoriteMovieEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class FavoriteMovieRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
) : FavoriteMovieRepository {

    override fun getFavorites(): Flowable<List<FavoriteMovieEntity>> {
        return localDataSource.getAllFavorites()
    }

    override fun removeMovieWithId(movieId: Int) {
        localDataSource.removeFavouriteMovieWithId(movieId)
    }

    override fun addMovieToFavorites(movie: FavoriteMovieEntity) {
        localDataSource.saveMovieInFavorites(movie)
    }

    override fun checkMovieExistsInFavorites(movieId: Int): Single<FavoriteMovieEntity> {
        return localDataSource.getFavouriteMovieWithId(movieId)
    }
}