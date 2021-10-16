package com.example.mvvmexample.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvvmexample.model.PhotoData
import com.example.mvvmexample.model.posts.PostsData

@Dao
interface PostsDao {
    @Query("SELECT * FROM Posts")
    fun findAllPosts(): List<PostsData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(posts: List<PostsData>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun createPostsIfNotExists(photos: PostsData): Long
}