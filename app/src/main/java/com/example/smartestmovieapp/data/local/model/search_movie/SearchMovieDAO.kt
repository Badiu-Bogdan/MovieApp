package com.example.smartestmovieapp.data.local.model.search_movie

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

@Dao
interface SearchMovieDAO {

    @Query(
        "SELECT popularID as id, title, vote_average, image_url FROM popular_table\n" +
                "UNION SELECT top_ratedID as id, title, vote_average, image_url FROM top_rated_table\n" +
                "UNION SELECT upcomingID as id, title, vote_average, image_url FROM upcoming_table"
    )
    fun getAllSearchedMovies(): Single<List<SearchMovie>>
}