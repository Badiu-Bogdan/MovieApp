package com.example.smartestmovieapp.di

import com.example.smartestmovieapp.SmartestMovieApp
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Component(
    modules = [
        LocationSearchActivityModule::class,
        LocationSearchFragmentModule::class,
        AndroidInjectionModule::class,
        AppModule::class,
        FragmentModules::class,
        ActivitiesModule::class
    ]
)
@Singleton
interface AppComponent {
    fun inject(application: SmartestMovieApp)
}