package com.example.mvvmexample.respository

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.mvvmexample.api.ApiService
import com.example.mvvmexample.db.dao.PhotosDao
import com.example.mvvmexample.model.PhotoData
import com.example.mvvmexample.utils.AppResult
import com.example.mvvmexample.utils.NetworkManager.isOnline
import com.example.mvvmexample.utils.Utils.handleApiError
import com.example.mvvmexample.utils.Utils.handleSuccess
import com.example.mvvmexample.utils.noNetworkConnectivityError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotoListRepositoryImpl(
    private val api: ApiService,
    private val context: Context,
    private val dao: PhotosDao
) : PhotosListRepository {

    override suspend fun getAllPhotos(): AppResult<List<PhotoData>> {
        if (isOnline(context)) {
            return try {
                val response = api.getAllPhotos()
                if (response.isSuccessful) {

                    //save the data
                    response.body()?.let {

                        withContext(Dispatchers.IO) { dao.add(it) }
                    }
                    handleSuccess(response)
                } else {
                    Log.d("tstffee","bvgvgv");
                    handleApiError(response)
                }
            } catch (e: Exception) {
                 AppResult.Error(e)
            }
        } else {
            //check in db if the data exists
            val data = getPhotosDataFromCache()
            return if (data.isNotEmpty()) {
                Log.d(TAG, "from db")
                AppResult.Success(data)
            } else
            //no network
                context.noNetworkConnectivityError()
        }
    }

    private suspend fun getPhotosDataFromCache(): List<PhotoData> {
        return withContext(Dispatchers.IO) {
            dao.findAllPhotos()
        }
    }
}