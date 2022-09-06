package com.example.smartestmovieapp.data.responses.movie_details

import com.example.smartestmovieapp.data.local.model.movie_details.MovieDetailsEntity
import com.example.smartestmovieapp.data.responses.genres.GenresResponseItem

data class MovieDetailsResponse(
    val adult: Boolean,
    val backdrop_path: String,
    val belongs_to_collection: Collection,
    val budget: Int,
    val genres: List<GenresResponseItem>,
    val homepage: String,
    val id: Int,
    val imdb_id: String,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val production_companies: List<ProductionCompanies>,
    val production_countries: List<ProductionCountries>,
    val release_date: String,
    val revenue: Int,
    val runtime: Int,
    val spoken_languages: List<SpokenLanguages>,
    val status: String,
    val tagline: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
) {
    fun getGenresNamesList(genresList: List<GenresResponseItem>): List<String> {
        val namesList: MutableList<String> = mutableListOf()
        for (genre in genresList)
            namesList.add(genre.name)
        return namesList.toList()
    }
}

fun MovieDetailsResponse.toMovieDetailsEntity() = MovieDetailsEntity(
    movie_detailsID = id,
    title = original_title,
    description = overview,
    language = original_language,
    runtime = runtime,
    vote_average = vote_average,
    genres = getGenresNamesList(genres),
    image_url = "https://image.tmdb.org/t/p/w500${poster_path}"
)
