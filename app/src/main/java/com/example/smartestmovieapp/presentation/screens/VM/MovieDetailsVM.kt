package com.example.smartestmovieapp.presentation.screens.VM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartestmovieapp.data.local.model.movie_details.MovieDetailsEntity
import com.example.smartestmovieapp.data.repository.movieDetails.MovieDetailsRepository
import com.example.smartestmovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class MovieDetailsVM @Inject constructor(
    private val repository: MovieDetailsRepository
) : ViewModel() {

    companion object {
        val TAG = MovieDetailsVM::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    val movieDetailsLiveData = MutableLiveData<Resource<MovieDetailsEntity>>()

    fun getMovieDetails(id: Int) {
        movieDetailsLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getMovieDetails(id).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> movieDetailsLiveData.value = Resource.Success(result) },
                    { error -> movieDetailsLiveData.value = Resource.Error(error.message ?: "") }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}