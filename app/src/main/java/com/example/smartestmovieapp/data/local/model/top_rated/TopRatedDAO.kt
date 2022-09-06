package com.example.smartestmovieapp.data.local.model.top_rated

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

@Dao
interface TopRatedDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopRated(topRatedList: List<TopRatedEntity>)

    @Query("SELECT * FROM top_rated_table")
    fun getAllTopRated(): Single<List<TopRatedEntity>>

}