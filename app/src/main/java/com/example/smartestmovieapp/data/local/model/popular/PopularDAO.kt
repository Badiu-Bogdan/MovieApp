package com.example.smartestmovieapp.data.local.model.popular

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

@Dao
interface PopularDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPopulars(popularsList: List<PopularEntity>)

    @Query("SELECT * FROM popular_table")
    fun getAllPopulars(): Single<List<PopularEntity>>
}