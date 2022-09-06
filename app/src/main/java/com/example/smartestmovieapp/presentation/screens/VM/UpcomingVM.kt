package com.example.smartestmovieapp.presentation.screens.VM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartestmovieapp.data.local.model.upcoming.UpcomingEntity
import com.example.smartestmovieapp.data.repository.upcoming.UpcomingRepository
import com.example.smartestmovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class UpcomingVM @Inject constructor(
    private val repository: UpcomingRepository
) : ViewModel() {
    companion object {
        val TAG = UpcomingVM::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    val upComingLiveData = MutableLiveData<Resource<List<UpcomingEntity>>>()

    init {
        getUpcoming()
    }

    fun getUpcoming() {
        upComingLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getUpComing().observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> upComingLiveData.value = Resource.Success(result) },
                    { error -> upComingLiveData.value = Resource.Error(error.message ?: "") }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}