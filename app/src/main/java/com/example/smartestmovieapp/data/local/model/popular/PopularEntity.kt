package com.example.smartestmovieapp.data.local.model.popular

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "popular_table")
class PopularEntity(
    @PrimaryKey(autoGenerate = false)
    val popularID: Int,
    val title: String,
    val vote_average: Double,
    val image_url: String,
    val genres: List<String>
)