package com.example.smartestmovieapp.data.local.model.genres

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genre_table")
data class GenreEntity(
    @PrimaryKey(autoGenerate = false)
    val genreID: Int,
    val name: String
)