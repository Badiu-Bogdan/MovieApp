package com.example.smartestmovieapp.data.repository.favorites

import com.example.smartestmovieapp.data.local.model.favorites.FavoriteMovieEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface FavoriteMovieRepository {

    fun getFavorites(): Flowable<List<FavoriteMovieEntity>>

    fun removeMovieWithId(movieId: Int)

    fun addMovieToFavorites(movie: FavoriteMovieEntity)

    fun checkMovieExistsInFavorites(movieId: Int): Single<FavoriteMovieEntity>
}