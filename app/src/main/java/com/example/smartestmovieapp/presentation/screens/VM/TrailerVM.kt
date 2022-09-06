package com.example.smartestmovieapp.presentation.screens.VM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartestmovieapp.data.local.model.trailer.TrailerEntity
import com.example.smartestmovieapp.data.repository.trailer.TrailerRepository
import com.example.smartestmovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class TrailerVM @Inject constructor(private val repository: TrailerRepository) : ViewModel() {

    companion object {
        val TAG = TrailerVM::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    val trailerLiveData = MutableLiveData<Resource<TrailerEntity>>()

/*    init {
        getTrailers()
    }*/

    fun getTrailer(id: Int) {
        trailerLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getTrailers(id).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> trailerLiveData.value = Resource.Success(result) },
                    { error -> trailerLiveData.value = Resource.Error(error.message ?: "") }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}