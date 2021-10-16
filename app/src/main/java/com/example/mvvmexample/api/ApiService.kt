package com.example.mvvmexample.api

import com.example.mvvmexample.model.PhotoData
import com.example.mvvmexample.model.posts.CreatePost
import com.example.mvvmexample.model.posts.PostsData
import com.example.mvvmexample.model.todos.TodosData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {



    @GET("/photos")
    suspend fun getAllPhotos(): Response<List<PhotoData>>
    @GET("/todos")
    suspend fun getAllTodos():Response<List<TodosData>>

    @GET("/posts/?userId=")
    suspend fun getAllPosts(@Query("userId")userId:Int): Response<List<PostsData>>

    @POST("posts")
    fun createPost(@Body postModel: CreatePost):Call<CreatePost>

    @DELETE("posts/{id}")
    fun deletePost(@Path("id") id:Int):Call<String>

    @GET("posts")
    fun fetchAllPosts(): Call<List<CreatePost>>

}