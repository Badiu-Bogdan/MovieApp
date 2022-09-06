package com.example.smartestmovieapp.data.local.model.movie_details

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_details_table")
class MovieDetailsEntity(
    @PrimaryKey(autoGenerate = false)
    val movie_detailsID: Int,
    val title: String,
    val description: String,
    val language: String,
    val runtime: Int,
    val vote_average: Double,
    val genres: List<String>,
    val image_url: String
)