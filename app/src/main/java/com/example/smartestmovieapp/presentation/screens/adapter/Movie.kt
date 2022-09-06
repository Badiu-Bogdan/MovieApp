package com.example.smartestmovieapp.presentation.screens.adapter

import java.io.Serializable

data class Movie(
    val id: Int,
    var title: String,
    var details: String,
    var rating: Double,
    var genres: List<String>,
    var runtime: Int,
    var imageUrl: String
) : Serializable