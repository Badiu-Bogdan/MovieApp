package com.example.smartestmovieapp.data.repository.cinema

import com.example.smartestmovieapp.data.local.LocalDataSource
import com.example.smartestmovieapp.data.local.model.cinema.CinemaEntity
import com.example.smartestmovieapp.data.local.model.cinema.CinemaMoviesEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CinemaRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
) : CinemaRepository {
    fun getCinemaById(cinemaId: Int): CinemaEntity {
        return localDataSource.getCinemaById(cinemaId)
    }

    override fun initializeCinemaData(cinemaList: List<CinemaEntity>) {
        localDataSource.saveAllCinemas(cinemaList)
    }

    override fun getAllCinemas(): Flowable<List<CinemaEntity>> {
        return localDataSource.getAllCinemas()
    }

    override fun insertCinema(cinema: CinemaEntity) {
        localDataSource.insertCinema(cinema)
    }

    override fun getCinemaDatabaseDataCount(): Single<Int> =
        localDataSource.getCinemaDatabaseDataCount()

    override fun saveRunningMoviesList(runningMovies: List<CinemaMoviesEntity>) {
        localDataSource.insertRunningMoviesList(runningMovies)
    }

    override fun getRunningMoviesOfCinema(cinemaId: Int): Flowable<List<CinemaMoviesEntity>> {
        return localDataSource.getRunningMoviesFromCinema(cinemaId)
    }

}