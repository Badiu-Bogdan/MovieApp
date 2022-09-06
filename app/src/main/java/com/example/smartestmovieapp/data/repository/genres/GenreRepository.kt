package com.example.smartestmovieapp.data.repository.genres

import com.example.smartestmovieapp.data.local.model.genres.GenreEntity
import io.reactivex.rxjava3.core.Single

interface GenreRepository {
    fun getGenres(): Single<List<GenreEntity>>
}