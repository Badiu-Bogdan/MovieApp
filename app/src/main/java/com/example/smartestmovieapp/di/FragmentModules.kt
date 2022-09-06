package com.example.smartestmovieapp.di

import com.example.smartestmovieapp.presentation.screens.authentication.AuthFragment
import com.example.smartestmovieapp.presentation.screens.authentication.LoginScreenFragment
import com.example.smartestmovieapp.presentation.screens.authentication.RegisterScreenFragment
import com.example.smartestmovieapp.presentation.screens.cinema.CinemaScreenFragment
import com.example.smartestmovieapp.presentation.screens.favorites.FavouritesFragment
import com.example.smartestmovieapp.presentation.screens.home.HomeScreenFragment
import com.example.smartestmovieapp.presentation.screens.search_movie.SearchMovieFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModules {

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginScreenFragment

    @ContributesAndroidInjector
    abstract fun contributeRegisterFragment(): RegisterScreenFragment

    @ContributesAndroidInjector
    abstract fun contributeAuthFragment(): AuthFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeScreenFragment(): HomeScreenFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchMovieFragment(): SearchMovieFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoritesFragment(): FavouritesFragment

    @ContributesAndroidInjector
    abstract fun contributeCinemaScreenFragment(): CinemaScreenFragment
}