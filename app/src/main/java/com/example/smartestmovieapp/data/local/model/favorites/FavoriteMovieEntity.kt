package com.example.smartestmovieapp.data.local.model.favorites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies_table")
class FavoriteMovieEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    val description: String,
    val language: String,
    val runtime: Int,
    val vote_average: Double,
    val genres: List<String>,
    val image_url: String
)