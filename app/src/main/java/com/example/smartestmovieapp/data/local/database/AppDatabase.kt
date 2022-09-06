package com.example.smartestmovieapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.smartestmovieapp.data.local.model.cinema.CinemaDAO
import com.example.smartestmovieapp.data.local.model.cinema.CinemaEntity
import com.example.smartestmovieapp.data.local.model.cinema.CinemaMoviesEntity
import com.example.smartestmovieapp.data.local.model.favorites.FavoriteMovieDAO
import com.example.smartestmovieapp.data.local.model.favorites.FavoriteMovieEntity
import com.example.smartestmovieapp.data.local.model.genres.GenreDAO
import com.example.smartestmovieapp.data.local.model.genres.GenreEntity
import com.example.smartestmovieapp.data.local.model.movie_details.ListTypeConverter
import com.example.smartestmovieapp.data.local.model.movie_details.MovieDetailsDAO
import com.example.smartestmovieapp.data.local.model.movie_details.MovieDetailsEntity
import com.example.smartestmovieapp.data.local.model.popular.PopularDAO
import com.example.smartestmovieapp.data.local.model.popular.PopularEntity
import com.example.smartestmovieapp.data.local.model.search_movie.SearchMovieDAO
import com.example.smartestmovieapp.data.local.model.similar_movies.SimilarMoviesDAO
import com.example.smartestmovieapp.data.local.model.top_rated.TopRatedDAO
import com.example.smartestmovieapp.data.local.model.top_rated.TopRatedEntity
import com.example.smartestmovieapp.data.local.model.trailer.TrailerDAO
import com.example.smartestmovieapp.data.local.model.trailer.TrailerEntity
import com.example.smartestmovieapp.data.local.model.upcoming.UpcomingDAO
import com.example.smartestmovieapp.data.local.model.upcoming.UpcomingEntity

@Database(
    entities = [PopularEntity::class,
        TopRatedEntity::class,
        UpcomingEntity::class,
        TrailerEntity::class,
        MovieDetailsEntity::class,
        FavoriteMovieEntity::class,
        GenreEntity::class,
        CinemaEntity::class,
        CinemaMoviesEntity::class],
    version = 13
)
@TypeConverters(ListTypeConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getPopularDAO(): PopularDAO
    abstract fun getTopRatedDAO(): TopRatedDAO
    abstract fun getUpcomingDAO(): UpcomingDAO
    abstract fun getTrailerDAO(): TrailerDAO
    abstract fun getMovieDetailsDAO(): MovieDetailsDAO
    abstract fun getSearchMovieDAO(): SearchMovieDAO
    abstract fun getFavoriteMovieDAO(): FavoriteMovieDAO
    abstract fun getSimilarMoviesDAO(): SimilarMoviesDAO
    abstract fun getGenreDAO(): GenreDAO
    abstract fun getCinemaDAO(): CinemaDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Database"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE!!
        }
    }
}