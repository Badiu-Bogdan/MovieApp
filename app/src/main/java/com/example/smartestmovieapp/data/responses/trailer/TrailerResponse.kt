package com.example.smartestmovieapp.data.responses.trailer

import com.example.smartestmovieapp.data.local.model.trailer.TrailerEntity

data class TrailerResponse(
    val id: Int,
    val results: List<TrailerResponseItem>
)

fun TrailerResponse.toTrailerEntity() = TrailerEntity(
    trailerID = id,
    video_url = "https://www.youtube.com/watch?v=${results[0].key}" // get just the first trailer; we don't need a list of trailers
)