package com.example.smartestmovieapp.data.responses.popular

data class PopularResponse (
    val page: Int,
    val results: List<PopularResponseItem>,
    val total_pages: Int,
    val total_results: Int
)