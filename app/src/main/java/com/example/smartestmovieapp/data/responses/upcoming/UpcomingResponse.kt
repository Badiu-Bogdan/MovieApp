package com.example.smartestmovieapp.data.responses.upcoming

data class UpcomingResponse(
    val dates: Date,
    val page: Int,
    val results: List<UpcomingResponseItem>,
    val total_pages: Int,
    val total_results: Int
)