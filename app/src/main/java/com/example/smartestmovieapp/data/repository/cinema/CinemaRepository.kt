package com.example.smartestmovieapp.data.repository.cinema

import com.example.smartestmovieapp.data.local.model.cinema.CinemaEntity
import com.example.smartestmovieapp.data.local.model.cinema.CinemaMoviesEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface CinemaRepository {

    fun initializeCinemaData(cinemaList: List<CinemaEntity>)

    fun getAllCinemas(): Flowable<List<CinemaEntity>>

    fun insertCinema(cinema: CinemaEntity)

    fun getCinemaDatabaseDataCount(): Single<Int>

    fun saveRunningMoviesList(runningMovies: List<CinemaMoviesEntity>)

    fun getRunningMoviesOfCinema(cinemaId: Int): Flowable<List<CinemaMoviesEntity>>

}