package com.example.smartestmovieapp.data.local.model.cinema

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface CinemaDAO {

    @Query("SELECT COUNT(*) FROM cinema_table")
    fun getCinemaDatabaseDataCount(): Single<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCinemasData(cinemaList: List<CinemaEntity>)

    @Query("SELECT * FROM cinema_table")
    fun getAllCinemas(): Flowable<List<CinemaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCinema(cinema: CinemaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRunningMovies(runningMovies: List<CinemaMoviesEntity>)

    @Query("SELECT * FROM cinema_running_movies WHERE cinema_id = :cinemaId")
    fun getRunningMoviesFromCinema(cinemaId: Int): Flowable<List<CinemaMoviesEntity>>

    @Query("SELECT * FROM cinema_table WHERE id = :cinemaId")
    fun getCinemaById(cinemaId: Int): CinemaEntity
}