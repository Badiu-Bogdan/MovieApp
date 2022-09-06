package com.example.smartestmovieapp.data.local.model.genres

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

@Dao
interface GenreDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertGenres(genresList: List<GenreEntity>)

    @Query("SELECT * FROM genre_table")
    fun getAllGenres(): Single<List<GenreEntity>>
}