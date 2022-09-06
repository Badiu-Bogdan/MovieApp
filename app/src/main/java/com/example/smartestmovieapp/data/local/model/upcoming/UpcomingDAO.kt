package com.example.smartestmovieapp.data.local.model.upcoming

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

@Dao
interface UpcomingDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpcoming(upcomingList: List<UpcomingEntity>)

    @Query("SELECT * FROM UPCOMING_TABLE")
    fun getAllUpcoming(): Single<List<UpcomingEntity>>
}