package com.example.smartestmovieapp.presentation.screens.VM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartestmovieapp.data.local.model.top_rated.TopRatedEntity
import com.example.smartestmovieapp.data.repository.topRated.TopRatedRepository
import com.example.smartestmovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class TopRatedVM @Inject constructor(
    private val repository: TopRatedRepository
) : ViewModel() {

    companion object {
        val TAG = TopRatedVM::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    val topRatedLiveData = MutableLiveData<Resource<List<TopRatedEntity>>>()

    init {
        getTopRated()
    }

    fun getTopRated() {
        topRatedLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getAllTopRated().observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> topRatedLiveData.value = Resource.Success(result) },
                    { error -> topRatedLiveData.value = Resource.Error(error.message ?: "") }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}