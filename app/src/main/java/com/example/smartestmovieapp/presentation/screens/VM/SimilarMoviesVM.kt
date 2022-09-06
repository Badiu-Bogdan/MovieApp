package com.example.smartestmovieapp.presentation.screens.VM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartestmovieapp.data.local.model.similar_movies.SimilarMovie
import com.example.smartestmovieapp.data.repository.similar_movies.SimilarMoviesRepository
import com.example.smartestmovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class SimilarMoviesVM @Inject constructor(private val repository: SimilarMoviesRepository) :
    ViewModel() {

    companion object {
        val TAG = SimilarMoviesVM::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()
    val similarMoviesLiveData = MutableLiveData<Resource<List<SimilarMovie>>>()

    fun getMovies(id: Int) {
        similarMoviesLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getMovies(id).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> similarMoviesLiveData.value = Resource.Success(result) },
                    { error -> similarMoviesLiveData.value = Resource.Error(error.message ?: "") }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}