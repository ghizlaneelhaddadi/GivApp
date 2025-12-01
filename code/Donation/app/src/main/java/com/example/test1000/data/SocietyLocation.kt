package com.example.test1000.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "society_locations")
data class SocietyLocation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double
)
