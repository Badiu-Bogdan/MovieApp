package com.example.smartestmovieapp.data.local.model.movie_details

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

@Dao
interface MovieDetailsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMovieDetails(movieDetailsEntity: MovieDetailsEntity)

    @Query("SELECT * FROM movie_details_table WHERE movie_detailsID= :id")
    fun getMovieDetails(id: Int): Single<MovieDetailsEntity>
}