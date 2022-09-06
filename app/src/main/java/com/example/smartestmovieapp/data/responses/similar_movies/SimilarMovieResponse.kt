package com.example.smartestmovieapp.data.responses.similar_movies

class SimilarMovieResponse(
    val page: Int,
    val results: List<SimilarMovieResponseItem>,
    val total_pages: Int,
    val total_results: Int
)