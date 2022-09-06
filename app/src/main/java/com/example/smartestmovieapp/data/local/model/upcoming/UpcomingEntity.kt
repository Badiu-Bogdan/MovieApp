package com.example.smartestmovieapp.data.local.model.upcoming

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upcoming_table")
class UpcomingEntity(
    @PrimaryKey(autoGenerate = false)
    val upcomingID: Int,
    val title: String,
    val vote_average: Double,
    val image_url: String,
    val genres: List<String>
)