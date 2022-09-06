package com.example.smartestmovieapp.di

import com.example.smartestmovieapp.presentation.LocationSearchActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LocationSearchActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeLocationSearchActivity(): LocationSearchActivity
}