package com.example.mvvmexample.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvvmexample.model.PhotoData

@Dao
interface PhotosDao {

    @Query("SELECT * FROM Photo")
    fun findAllPhotos(): List<PhotoData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(photo: List<PhotoData>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     fun createPhotosIfNotExists(photos: PhotoData): Long
}