package com.example.smartestmovieapp.data.repository.upcoming

import com.example.smartestmovieapp.data.local.model.upcoming.UpcomingEntity
import io.reactivex.rxjava3.core.Single

interface UpcomingRepository {

    fun getUpComing(): Single<List<UpcomingEntity>>
}