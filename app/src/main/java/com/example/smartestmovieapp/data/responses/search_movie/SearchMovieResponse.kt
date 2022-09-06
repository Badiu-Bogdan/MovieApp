package com.example.smartestmovieapp.data.responses.search_movie

data class SearchMovieResponse(
    val page: Int,
    val results: List<SearchMovieResponseItem>,
    val total_pages: Int,
    val total_results: Int
)