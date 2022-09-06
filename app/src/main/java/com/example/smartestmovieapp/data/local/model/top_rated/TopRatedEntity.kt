package com.example.smartestmovieapp.data.local.model.top_rated

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "top_rated_table")
class TopRatedEntity(
    @PrimaryKey(autoGenerate = false)
    val top_ratedID: Int,
    val title: String,
    val vote_average: Double,
    val image_url: String,
    val genres: List<String>
)