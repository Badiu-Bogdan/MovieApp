package com.example.smartestmovieapp.data.local.model.similar_movies

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

@Dao
interface SimilarMoviesDAO {

    @Query(
        "SELECT popularID as id, title, vote_average, image_url, genres FROM popular_table\n" +
                "UNION SELECT top_ratedID as id, title, vote_average, image_url,genres FROM top_rated_table\n" +
                "UNION SELECT upcomingID as id, title, vote_average, image_url, genres FROM upcoming_table"
    )
    fun getAllMovies(): Single<List<SimilarMovie>>
}