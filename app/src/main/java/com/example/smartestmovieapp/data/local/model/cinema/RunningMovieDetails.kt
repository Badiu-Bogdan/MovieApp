package com.example.smartestmovieapp.data.local.model.cinema

data class RunningMovieDetails(
    val movie_detailsID: Int,
    val title: String,
    val runtime: Int,
    val vote_average: Double,
    val genres: List<String>,
    val image_url: String,
    val runningHours: List<String>
)
