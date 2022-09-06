package com.example.smartestmovieapp.data.local.model.cinema

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cinema_running_movies",
    foreignKeys = [
        ForeignKey(
            entity = CinemaEntity::class,
            parentColumns = ["id"], childColumns = ["cinema_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CinemaMoviesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val movie_id: Int,
    @ColumnInfo(name = "running_hours")
    val runningHours: List<String>,
    @ColumnInfo(name = "cinema_id")
    val cinemaId: Int
)
