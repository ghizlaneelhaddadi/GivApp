package com.example.test1000.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SocietyLocationDao {
    @Insert
    fun insert(societyLocations: List<SocietyLocation>): List<Long>


    @Query("SELECT * FROM society_locations")
    fun getAllLocations(): List<SocietyLocation?>?


    @Query("DELETE FROM society_locations")
    fun clearAll(): Int

}
