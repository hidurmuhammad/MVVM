package com.example.mvvmexample.respository

import com.example.mvvmexample.model.PhotoData
import com.example.mvvmexample.utils.AppResult

interface PhotosListRepository {
    suspend fun getAllPhotos() : AppResult<List<PhotoData>>
}