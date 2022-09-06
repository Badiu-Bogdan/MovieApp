package com.example.smartestmovieapp.di

import com.example.smartestmovieapp.presentation.screens.category.CategoryScreenActivity
import com.example.smartestmovieapp.presentation.screens.cinema_details.CinemaDetailsActivity
import com.example.smartestmovieapp.presentation.screens.home.HomeScreenActivity
import com.example.smartestmovieapp.presentation.screens.map.MapsActivity
import com.example.smartestmovieapp.presentation.screens.movie_details.CheckAvailabilityActivity
import com.example.smartestmovieapp.presentation.screens.movie_details.MovieDetailsScreenActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesModule {

    @ContributesAndroidInjector
    abstract fun contributeCategoryScreenActivity(): CategoryScreenActivity

    @ContributesAndroidInjector
    abstract fun contributeMovieDetailsScreenActivity(): MovieDetailsScreenActivity

    @ContributesAndroidInjector
    abstract fun contributeHomeScreenActivity(): HomeScreenActivity

    @ContributesAndroidInjector
    abstract fun contributeCinemaDetailsActivity(): CinemaDetailsActivity

    @ContributesAndroidInjector
    abstract fun contributeCinemaAvailabilityActivity(): CheckAvailabilityActivity

    @ContributesAndroidInjector
    abstract fun contributeMapsActivity(): MapsActivity
}