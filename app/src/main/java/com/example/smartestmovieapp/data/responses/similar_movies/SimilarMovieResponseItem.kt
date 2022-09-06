package com.example.smartestmovieapp.data.responses.similar_movies

import com.example.smartestmovieapp.data.local.model.similar_movies.SimilarMovie

data class SimilarMovieResponseItem(
    val adult: Boolean,
    val backdrop_path: String,
    val genre_ids: ArrayList<Int>,
    val id: Int,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)

fun SimilarMovieResponseItem.toSimilarEntity() = SimilarMovie(
    id = id,
    title = original_title,
    vote_average = vote_average,
    image_url = "https://image.tmdb.org/t/p/w500${poster_path}",
    genres = listOf()
)