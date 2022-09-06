package com.example.smartestmovieapp.data.local.model.similar_movies

data class SimilarMovie(
    val id: Int,
    val title: String,
    val vote_average: Double,
    val image_url: String,
    val genres: List<String>
)