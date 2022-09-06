package com.example.smartestmovieapp.data.responses.genres

import com.example.smartestmovieapp.data.local.model.genres.GenreEntity

data class GenresResponseItem(
    val id: Int,
    val name: String
)

fun GenresResponseItem.toGenreEntity() = GenreEntity(
    genreID = id,
    name = name
)