package com.example.smartestmovieapp.data.local.model.search_movie

import com.example.smartestmovieapp.presentation.screens.adapter.Movie

data class SearchMovie(
    val id: Int,
    val title: String,
    val vote_average: Double,
    val image_url: String
)

fun SearchMovie.toMovie() = Movie(
    id = id,
    title = title,
    details = vote_average.toString(),
    rating = vote_average,
    mutableListOf(),
    0,
    imageUrl = image_url
)