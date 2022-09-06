package com.example.smartestmovieapp.data.local.model.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface FavoriteMovieDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: FavoriteMovieEntity)


    @Query("SELECT * FROM favorite_movies_table")
    fun getAllFavorites(): Flowable<List<FavoriteMovieEntity>>

    @Query("SELECT * FROM favorite_movies_table WHERE id = :id")
    fun getFavouriteMovieWithId(id: Int): Single<FavoriteMovieEntity>


    @Query("DELETE FROM favorite_movies_table WHERE id = :id")
    fun removeFavouriteMovieWithId(id: Int)

}