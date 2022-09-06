package com.example.smartestmovieapp.presentation.screens.VM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartestmovieapp.data.local.model.popular.PopularEntity
import com.example.smartestmovieapp.data.repository.popular.PopularRepository
import com.example.smartestmovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class PopularVM @Inject constructor(
    private val repository: PopularRepository
) : ViewModel() {

    companion object {
        val TAG = PopularVM::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    val popularLiveData = MutableLiveData<Resource<List<PopularEntity>>>()

    init {
        getPopulars()
    }

    fun getPopulars() {
        popularLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getPopulars().observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> popularLiveData.value = Resource.Success(result) },
                    { error -> popularLiveData.value = Resource.Error(error.message ?: "") }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}