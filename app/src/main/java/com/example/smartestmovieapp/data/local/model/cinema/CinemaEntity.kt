package com.example.smartestmovieapp.data.local.model.cinema

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cinema_table")
data class CinemaEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val imageUrl: String
) : Serializable
