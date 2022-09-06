package com.example.smartestmovieapp.data.responses.topRated

import com.example.smartestmovieapp.data.local.model.top_rated.TopRatedEntity

data class TopRatedResponseItem(
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
{
    fun getGenresIDList(genresList: ArrayList<Int>): List<String> {
        val idList: MutableList<String> = mutableListOf()
        for (genre in genresList)
            idList.add(genre.toString())
        return idList.toList()
    }
}

fun TopRatedResponseItem.toTopRatedEntity() = TopRatedEntity(
    top_ratedID = id,
    title = original_title,
    vote_average = vote_average,
    image_url = "https://image.tmdb.org/t/p/w500${poster_path}",
    genres = getGenresIDList(genre_ids)
)