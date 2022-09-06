package com.example.smartestmovieapp.presentation.screens.VM

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartestmovieapp.data.local.model.cinema.CinemaEntity
import com.example.smartestmovieapp.data.local.model.cinema.CinemaMoviesEntity
import com.example.smartestmovieapp.data.repository.cinema.CinemaRepositoryImpl
import com.example.smartestmovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class CinemaVM @Inject constructor(
    private val repository: CinemaRepositoryImpl
) : ViewModel() {

    companion object {
        val TAG = CinemaVM::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    val cinemaList = MutableLiveData<Resource<List<CinemaEntity>>>()
    val cinemaDataCount = MutableLiveData<Resource<Int>>()
    val cinemaRunningMovies = MutableLiveData<Resource<List<CinemaMoviesEntity>>>()

    init {
        getCinemas()
    }

    private fun getCinemas() {
        cinemaList.value = Resource.Loading()
        compositeDisposable.add(
            repository.getAllCinemas().observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> cinemaList.value = Resource.Success(result) }, // onNext
                    { error -> cinemaList.value = Resource.Error(error.message ?: "") },
                    { Log.d(LocationSearchVM.TAG, "Complete") }) // onComplete
        )
    }

    fun insertCinema(cinema: CinemaEntity) {
        repository.insertCinema(cinema)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun getCinemaDatabaseDataCount() {
        cinemaDataCount.value = Resource.Loading()
        compositeDisposable.add(
            repository.getCinemaDatabaseDataCount().observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> cinemaDataCount.value = Resource.Success(result) },
                    { error -> cinemaDataCount.value = Resource.Error(error.message ?: "") })
        )
    }

    fun saveRunningMoviesList(runningMovies: List<CinemaMoviesEntity>) {
        repository.saveRunningMoviesList(runningMovies)
    }

    fun getMoviesRunningAtSpecificCinema(cinemaId: Int) {
        cinemaRunningMovies.value = Resource.Loading()
        compositeDisposable.add(
            repository.getRunningMoviesOfCinema(cinemaId).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> cinemaRunningMovies.value = Resource.Success(result) },
                    { error -> cinemaRunningMovies.value = Resource.Error(error.message ?: "") })
        )
    }

}