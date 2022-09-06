package com.example.smartestmovieapp.data.local.model.trailer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

@Dao
interface TrailerDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrailer(trailerEntity: TrailerEntity)

    @Query("Select * from trailer_table WHERE trailerID= :id")
    fun getTrailer(id: Int): Single<TrailerEntity>
}