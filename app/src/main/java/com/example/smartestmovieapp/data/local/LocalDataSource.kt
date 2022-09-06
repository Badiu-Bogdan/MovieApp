package com.example.smartestmovieapp.data.local

import com.example.smartestmovieapp.data.local.model.cinema.CinemaDAO
import com.example.smartestmovieapp.data.local.model.cinema.CinemaEntity
import com.example.smartestmovieapp.data.local.model.cinema.CinemaMoviesEntity
import com.example.smartestmovieapp.data.local.model.favorites.FavoriteMovieDAO
import com.example.smartestmovieapp.data.local.model.favorites.FavoriteMovieEntity
import com.example.smartestmovieapp.data.local.model.genres.GenreDAO
import com.example.smartestmovieapp.data.local.model.genres.GenreEntity
import com.example.smartestmovieapp.data.local.model.movie_details.MovieDetailsDAO
import com.example.smartestmovieapp.data.local.model.movie_details.MovieDetailsEntity
import com.example.smartestmovieapp.data.local.model.popular.PopularDAO
import com.example.smartestmovieapp.data.local.model.popular.PopularEntity
import com.example.smartestmovieapp.data.local.model.search_movie.SearchMovie
import com.example.smartestmovieapp.data.local.model.search_movie.SearchMovieDAO
import com.example.smartestmovieapp.data.local.model.similar_movies.SimilarMovie
import com.example.smartestmovieapp.data.local.model.similar_movies.SimilarMoviesDAO
import com.example.smartestmovieapp.data.local.model.top_rated.TopRatedDAO
import com.example.smartestmovieapp.data.local.model.top_rated.TopRatedEntity
import com.example.smartestmovieapp.data.local.model.trailer.TrailerDAO
import com.example.smartestmovieapp.data.local.model.trailer.TrailerEntity
import com.example.smartestmovieapp.data.local.model.upcoming.UpcomingDAO
import com.example.smartestmovieapp.data.local.model.upcoming.UpcomingEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val popularDAO: PopularDAO,
    private val topRatedDAO: TopRatedDAO,
    private val upcomingDAO: UpcomingDAO,
    private val trailerDAO: TrailerDAO,
    private val movieDetailsDAO: MovieDetailsDAO,
    private val searchMovieDAO: SearchMovieDAO,
    private val favoriteMovieDAO: FavoriteMovieDAO,
    private val similarMoviesDAO: SimilarMoviesDAO,
    private val genreDAO: GenreDAO,
    private val cinemaDAO: CinemaDAO

) {
    fun savePopulars(popularEntitiesList: List<PopularEntity>) {
        return popularDAO.insertPopulars(popularEntitiesList)
    }

    fun getAllPopulars(): Single<List<PopularEntity>> {
        return popularDAO.getAllPopulars()
    }

    fun saveTopRated(topRatedList: List<TopRatedEntity>) {
        return topRatedDAO.insertTopRated(topRatedList)
    }

    fun getAllTopRated(): Single<List<TopRatedEntity>> {
        return topRatedDAO.getAllTopRated()
    }

    fun saveUpcoming(upcomingList: List<UpcomingEntity>) {
        return upcomingDAO.insertUpcoming(upcomingList)
    }

    fun getAllUpcoming(): Single<List<UpcomingEntity>> {
        return upcomingDAO.getAllUpcoming()
    }

    fun saveTrailer(trailerEntity: TrailerEntity) {
        return trailerDAO.insertTrailer(trailerEntity)
    }

    fun getTrailer(id: Int): Single<TrailerEntity> {
        return trailerDAO.getTrailer(id)
    }

    fun saveMovieDetails(movieDetailsEntity: MovieDetailsEntity) {
        movieDetailsDAO.addMovieDetails(movieDetailsEntity)
    }

    fun getMovieDetails(id: Int): Single<MovieDetailsEntity> {
        return movieDetailsDAO.getMovieDetails(id)
    }

    fun getAllSearchedMovies(): Single<List<SearchMovie>> {
        return searchMovieDAO.getAllSearchedMovies()
    }

    fun getAllMovies(): Single<List<SimilarMovie>> {
        return similarMoviesDAO.getAllMovies()
    }

    fun saveAllGenres(genresList: List<GenreEntity>) {
        return genreDAO.insertGenres(genresList)
    }

    fun getAllGenres(): Single<List<GenreEntity>> {
        return genreDAO.getAllGenres()
    }

    //Favorites
    fun saveMovieInFavorites(movie: FavoriteMovieEntity) {
        favoriteMovieDAO.insertMovie(movie)
    }

    fun getFavouriteMovieWithId(movieId: Int): Single<FavoriteMovieEntity> {
        return favoriteMovieDAO.getFavouriteMovieWithId(movieId)
    }

    fun getAllFavorites(): Flowable<List<FavoriteMovieEntity>> {
        return favoriteMovieDAO.getAllFavorites()
    }

    fun removeFavouriteMovieWithId(movieId: Int) {
        favoriteMovieDAO.removeFavouriteMovieWithId(movieId)
    }

    //Cinema
    fun saveAllCinemas(cinemaList: List<CinemaEntity>) {
        cinemaDAO.insertCinemasData(cinemaList)
    }

    fun getAllCinemas(): Flowable<List<CinemaEntity>> {
        return cinemaDAO.getAllCinemas()
    }

    fun insertCinema(cinema: CinemaEntity) {
        cinemaDAO.insertCinema(cinema)
    }

    fun getCinemaDatabaseDataCount(): Single<Int> = cinemaDAO.getCinemaDatabaseDataCount()

    fun insertRunningMoviesList(runningMovies: List<CinemaMoviesEntity>) {
        cinemaDAO.insertRunningMovies(runningMovies)
    }

    fun getRunningMoviesFromCinema(cinemaId: Int): Flowable<List<CinemaMoviesEntity>> {
        return cinemaDAO.getRunningMoviesFromCinema(cinemaId)
    }

    fun getCinemaById(cinemaId: Int): CinemaEntity {
        return cinemaDAO.getCinemaById(cinemaId)
    }
}