package com.example.smartestmovieapp.presentation.screens.VM

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartestmovieapp.data.local.model.favorites.FavoriteMovieEntity
import com.example.smartestmovieapp.data.repository.favorites.FavoriteMovieRepositoryImpl
import com.example.smartestmovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class FavoritesVM @Inject constructor(
    private val repository: FavoriteMovieRepositoryImpl
) : ViewModel() {

    companion object {
        val TAG = FavoritesVM::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    val favoritesList = MutableLiveData<Resource<List<FavoriteMovieEntity>>>()
    val alreadyInFavorites = MutableLiveData<Resource<FavoriteMovieEntity>>()

    init {
        getFavorites()
    }

    fun getFavorites() {
        favoritesList.value = Resource.Loading()
        compositeDisposable.add(
            repository.getFavorites().observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> favoritesList.value = Resource.Success(result) }, // onNext
                    { error -> favoritesList.value = Resource.Error(error.message ?: "") },
                    { Log.d(LocationSearchVM.TAG, "Complete") }) // onComplete
        )
    }


    fun removeMovieFromfavorites(movieId: Int) {
        repository.removeMovieWithId(movieId)
    }


    fun addMovieToFavorites(movie: FavoriteMovieEntity) {
        repository.addMovieToFavorites(movie)
    }

    fun checkMovieAddedToFavorites(movieId: Int) {
        alreadyInFavorites.value = Resource.Loading()
        compositeDisposable.add(
            repository.checkMovieExistsInFavorites(movieId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    alreadyInFavorites.value = Resource.Success(result)
                }, // onNext
                    { error -> alreadyInFavorites.value = Resource.Error(error.message ?: "") }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}