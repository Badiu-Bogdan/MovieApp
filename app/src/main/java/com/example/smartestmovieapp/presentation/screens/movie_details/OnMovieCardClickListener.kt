package com.example.smartestmovieapp.presentation.screens.movie_details

import com.example.smartestmovieapp.presentation.screens.adapter.Movie

interface OnMovieCardClickListener {
    fun onClick(movie: Movie)
}