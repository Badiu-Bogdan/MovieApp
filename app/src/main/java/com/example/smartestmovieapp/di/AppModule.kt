package com.example.smartestmovieapp.di

import android.content.Context
import com.example.smartestmovieapp.data.local.LocalDataSource
import com.example.smartestmovieapp.data.local.database.AppDatabase
import com.example.smartestmovieapp.data.local.model.cinema.CinemaDAO
import com.example.smartestmovieapp.data.local.model.favorites.FavoriteMovieDAO
import com.example.smartestmovieapp.data.local.model.genres.GenreDAO
import com.example.smartestmovieapp.data.local.model.movie_details.MovieDetailsDAO
import com.example.smartestmovieapp.data.local.model.popular.PopularDAO
import com.example.smartestmovieapp.data.local.model.search_movie.SearchMovieDAO
import com.example.smartestmovieapp.data.local.model.similar_movies.SimilarMoviesDAO
import com.example.smartestmovieapp.data.local.model.top_rated.TopRatedDAO
import com.example.smartestmovieapp.data.local.model.trailer.TrailerDAO
import com.example.smartestmovieapp.data.local.model.upcoming.UpcomingDAO
import com.example.smartestmovieapp.data.network.*
import com.example.smartestmovieapp.data.repository.LocationRepository
import com.example.smartestmovieapp.data.repository.LocationRepositoryImpl
import com.example.smartestmovieapp.data.repository.cinema.CinemaRepository
import com.example.smartestmovieapp.data.repository.cinema.CinemaRepositoryImpl
import com.example.smartestmovieapp.data.repository.favorites.FavoriteMovieRepository
import com.example.smartestmovieapp.data.repository.favorites.FavoriteMovieRepositoryImpl
import com.example.smartestmovieapp.data.repository.genres.GenreRepository
import com.example.smartestmovieapp.data.repository.genres.GenreRepositoryImpl
import com.example.smartestmovieapp.data.repository.movieDetails.MovieDetailsRepository
import com.example.smartestmovieapp.data.repository.movieDetails.MovieDetailsRepositoryImpl
import com.example.smartestmovieapp.data.repository.popular.PopularRepository
import com.example.smartestmovieapp.data.repository.popular.PopularRepositoryImpl
import com.example.smartestmovieapp.data.repository.search_movie.SearchMovieRepository
import com.example.smartestmovieapp.data.repository.search_movie.SearchMovieRepositoryImpl
import com.example.smartestmovieapp.data.repository.similar_movies.SimilarMoviesRepository
import com.example.smartestmovieapp.data.repository.similar_movies.SimilarMoviesRepositoryImpl
import com.example.smartestmovieapp.data.repository.topRated.TopRatedRepository
import com.example.smartestmovieapp.data.repository.topRated.TopRatedRepositoryImpl
import com.example.smartestmovieapp.data.repository.trailer.TrailerRepository
import com.example.smartestmovieapp.data.repository.trailer.TrailerRepositoryImpl
import com.example.smartestmovieapp.data.repository.upcoming.UpcomingRepository
import com.example.smartestmovieapp.data.repository.upcoming.UpcomingRepositoryImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContext() = context

    @Singleton
    @Provides
    fun providesLocationApi(remoteDataSource: RemoteDataSource, context: Context): LocationApi {
        return remoteDataSource.buildApi(LocationApi::class.java, context)
    }

    @Singleton
    @Provides
    fun providesPopularApi(remoteDataSource: RemoteDataSource, context: Context): PopularApi {
        return remoteDataSource.buildApi(PopularApi::class.java, context)
    }

    @Singleton
    @Provides
    fun providesTopRatedApi(remoteDataSource: RemoteDataSource, context: Context): TopRatedApi {
        return remoteDataSource.buildApi(TopRatedApi::class.java, context)
    }

    @Singleton
    @Provides
    fun providesUpComingApi(remoteDataSource: RemoteDataSource, context: Context): UpcomingApi {
        return remoteDataSource.buildApi(UpcomingApi::class.java, context)
    }

    @Singleton
    @Provides
    fun providesTrailerApi(remoteDataSource: RemoteDataSource, context: Context): TrailerApi {
        return remoteDataSource.buildApi(TrailerApi::class.java, context)
    }

    @Singleton
    @Provides
    fun providesSearchMovieApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): SearchMovieApi {
        return remoteDataSource.buildApi(SearchMovieApi::class.java, context)
    }

    @Singleton
    @Provides
    fun provideGenreApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): GenreApi {
        return remoteDataSource.buildApi(GenreApi::class.java, context)
    }

    @Singleton
    @Provides
    fun providesMovieDetailsApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): MovieDetailsApi {
        return remoteDataSource.buildApi(
            MovieDetailsApi::class.java,
            context
        )
    }

    @Singleton
    @Provides
    fun providesSimilarApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): SimilarApi {
        return remoteDataSource.buildApi(
            SimilarApi::class.java,
            context
        )
    }

    @Singleton
    @Provides
    fun providesGson() = Gson()

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.getAppDatabase(context)
    }

    @Provides
    @Singleton
    fun providePopularDAO(appDatabase: AppDatabase): PopularDAO {
        return appDatabase.getPopularDAO()
    }

    @Provides
    @Singleton
    fun provideTopRatedDAO(appDatabase: AppDatabase): TopRatedDAO {
        return appDatabase.getTopRatedDAO()
    }

    @Provides
    @Singleton
    fun provideUpcomingDAO(appDatabase: AppDatabase): UpcomingDAO {
        return appDatabase.getUpcomingDAO()
    }

    @Provides
    @Singleton
    fun provideTrailerDAO(appDatabase: AppDatabase): TrailerDAO {
        return appDatabase.getTrailerDAO()
    }

    @Provides
    @Singleton
    fun provideMovieDetailsDAO(appDatabase: AppDatabase): MovieDetailsDAO {
        return appDatabase.getMovieDetailsDAO()
    }

    @Provides
    @Singleton
    fun provideSearchMovieDAO(appDatabase: AppDatabase): SearchMovieDAO {
        return appDatabase.getSearchMovieDAO()
    }

    @Provides
    @Singleton
    fun providesFavoriteMovieDAO(appDatabase: AppDatabase): FavoriteMovieDAO {
        return appDatabase.getFavoriteMovieDAO()
    }

    @Provides
    @Singleton
    fun provideSimilarMoviesDAO(appDatabase: AppDatabase): SimilarMoviesDAO {
        return appDatabase.getSimilarMoviesDAO()
    }

    @Provides
    @Singleton
    fun provideGenreDAO(appDatabase: AppDatabase): GenreDAO {
        return appDatabase.getGenreDAO()
    }

    @Provides
    @Singleton
    fun providesCinemaDAO(appDatabase: AppDatabase): CinemaDAO {
        return appDatabase.getCinemaDAO()
    }

    @Provides
    @Singleton
    fun provideMovieLocalSource(
        popularDAO: PopularDAO,
        topRatedDAO: TopRatedDAO,
        upcomingDAO: UpcomingDAO,
        trailerDAO: TrailerDAO,
        movieDetailsDAO: MovieDetailsDAO,
        searchMovieDAO: SearchMovieDAO,
        favoriteMovieDAO: FavoriteMovieDAO,
        similarMoviesDAO: SimilarMoviesDAO,
        genreDAO: GenreDAO,
        cinemaDAO: CinemaDAO
    ): LocalDataSource {
        return LocalDataSource(
            popularDAO,
            topRatedDAO,
            upcomingDAO,
            trailerDAO,
            movieDetailsDAO,
            searchMovieDAO,
            favoriteMovieDAO,
            similarMoviesDAO,
            genreDAO,
            cinemaDAO
        )
    }

    @Singleton
    @Provides
    fun providesLocationRepository(locationApi: LocationApi): LocationRepository {
        return LocationRepositoryImpl(locationApi)
    }

    @Singleton
    @Provides
    fun providesPopularRepository(
        popularApi: PopularApi,
        localDataSource: LocalDataSource
    ): PopularRepository {
        return PopularRepositoryImpl(popularApi, localDataSource)
    }

    @Singleton
    @Provides
    fun providesTopRatedRepository(
        topRatedApi: TopRatedApi,
        localDataSource: LocalDataSource
    ): TopRatedRepository {
        return TopRatedRepositoryImpl(topRatedApi, localDataSource)
    }

    @Singleton
    @Provides
    fun providesUpComingRepository(
        upComingApi: UpcomingApi,
        localDataSource: LocalDataSource
    ): UpcomingRepository {
        return UpcomingRepositoryImpl(upComingApi, localDataSource)
    }

    @Singleton
    @Provides
    fun providesTrailerRepository(
        trailerApi: TrailerApi,
        localDataSource: LocalDataSource
    ): TrailerRepository {
        return TrailerRepositoryImpl(trailerApi, localDataSource)
    }

    @Singleton
    @Provides
    fun providesMovieDetailsRepository(
        movieDetailsApi: MovieDetailsApi,
        localDataSource: LocalDataSource
    ): MovieDetailsRepository {
        return MovieDetailsRepositoryImpl(movieDetailsApi, localDataSource)
    }

    @Singleton
    @Provides
    fun providesSearchMovieRepository(
        searchMovieApi: SearchMovieApi,
        localDataSource: LocalDataSource
    ): SearchMovieRepository {
        return SearchMovieRepositoryImpl(searchMovieApi, localDataSource)
    }

    @Singleton
    @Provides
    fun providesFavoritesRepository(
        localDataSource: LocalDataSource
    ): FavoriteMovieRepository {
        return FavoriteMovieRepositoryImpl(localDataSource)
    }

    @Singleton
    @Provides
    fun providesSimilarMoviesRepository(
        similarApi: SimilarApi,
        localDataSource: LocalDataSource
    ): SimilarMoviesRepository {
        return SimilarMoviesRepositoryImpl(similarApi, localDataSource)
    }

    @Singleton
    @Provides
    fun providesGenreRepository(
        genreApi: GenreApi,
        localDataSource: LocalDataSource
    ): GenreRepository {
        return GenreRepositoryImpl(genreApi, localDataSource)
    }

    @Singleton
    @Provides
    fun providesCinemaRepository(
        localDataSource: LocalDataSource
    ): CinemaRepository {
        return CinemaRepositoryImpl(localDataSource)
    }
}