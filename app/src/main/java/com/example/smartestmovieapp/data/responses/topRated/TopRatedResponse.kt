package com.example.smartestmovieapp.data.responses.topRated

data class TopRatedResponse(
    val page: Int,
    val results: List<TopRatedResponseItem>,
    val total_pages: Int,
    val total_results: Int
)