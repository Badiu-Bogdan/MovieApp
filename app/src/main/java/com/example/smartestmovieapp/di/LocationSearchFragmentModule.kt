package com.example.smartestmovieapp.di

import com.example.smartestmovieapp.presentation.LocationSearchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LocationSearchFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeLocationSearchFragment(): LocationSearchFragment

}