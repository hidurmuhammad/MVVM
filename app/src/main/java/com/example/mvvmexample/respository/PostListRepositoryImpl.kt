package com.example.mvvmexample.respository

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.mvvmexample.api.ApiService
import com.example.mvvmexample.db.dao.PostsDao
import com.example.mvvmexample.model.posts.PostsData
import com.example.mvvmexample.utils.AppResult
import com.example.mvvmexample.utils.NetworkManager
import com.example.mvvmexample.utils.Utils
import com.example.mvvmexample.utils.noNetworkConnectivityError
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response


class PostListRepositoryImpl (
    private val api: ApiService,
    private val context: Context,
    private val dao: PostsDao
        ) : PostListRepository {

    override suspend fun getAllPosts(userId:Int): AppResult<List<PostsData>> {
        if (NetworkManager.isOnline(context)) {
            return try {
                val response = api.getAllPosts(userId)
                if (response.isSuccessful) {
                    //save the data
                    response.body()?.let {
                        withContext(Dispatchers.IO) { dao.add(it) }
                    }
                    Utils.handleSuccess(response)
                } else {
                    Utils.handleApiError(response)
                }
            } catch (e: Exception) {
                AppResult.Error(e)
            }
        } else {
            //check in db if the data exists
            val data = getPostsDataFromCache()
            return if (data.isNotEmpty()) {
                Log.d(ContentValues.TAG, "from db")
                AppResult.Success(data)
            } else
            //no network
                context.noNetworkConnectivityError()
        }
    }

    private suspend fun getPostsDataFromCache(): List<PostsData> {
        return withContext(Dispatchers.IO) {
            dao.findAllPosts()
        }
    }

        }