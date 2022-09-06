package com.example.smartestmovieapp.data.local.model.trailer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trailer_table")
class TrailerEntity(
    @PrimaryKey(autoGenerate = false)
    val trailerID: Int,
    val video_url: String
)