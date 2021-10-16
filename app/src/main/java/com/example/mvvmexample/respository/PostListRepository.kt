package com.example.mvvmexample.respository

import com.example.mvvmexample.model.posts.PostsData
import com.example.mvvmexample.utils.AppResult

interface PostListRepository {
    suspend fun getAllPosts(userId: Int): AppResult<List<PostsData>>

}