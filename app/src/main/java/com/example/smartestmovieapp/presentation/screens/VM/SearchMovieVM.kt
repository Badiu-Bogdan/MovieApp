package com.example.smartestmovieapp.presentation.screens.VM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartestmovieapp.data.local.model.search_movie.SearchMovie
import com.example.smartestmovieapp.data.repository.search_movie.SearchMovieRepository
import com.example.smartestmovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class SearchMovieVM @Inject constructor(private val repository: SearchMovieRepository) :
    ViewModel() {

    companion object {
        val TAG = SearchMovieVM::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()
    val movieLiveData = MutableLiveData<Resource<List<SearchMovie>>>()

    fun getMovies(title: String) {
        movieLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getMovies(title).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> movieLiveData.value = Resource.Success(result) },
                    { error -> movieLiveData.value = Resource.Error(error.message ?: "") }
                )
        )
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
