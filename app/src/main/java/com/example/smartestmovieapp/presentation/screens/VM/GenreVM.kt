package com.example.smartestmovieapp.presentation.screens.VM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartestmovieapp.data.local.model.genres.GenreEntity
import com.example.smartestmovieapp.data.repository.genres.GenreRepository
import com.example.smartestmovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class GenreVM @Inject constructor(
    private val repository: GenreRepository
) : ViewModel() {

    companion object {
        val TAG = GenreVM::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    val genreLiveData = MutableLiveData<Resource<List<GenreEntity>>>()

    init {
        getGenres()
    }

    fun getGenres() {
        genreLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getGenres().observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> genreLiveData.value = Resource.Success(result) },
                    { error -> genreLiveData.value = Resource.Error(error.message ?: "") }
                )
        )
    }
}