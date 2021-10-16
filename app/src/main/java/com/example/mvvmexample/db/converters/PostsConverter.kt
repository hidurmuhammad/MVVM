package com.example.mvvmexample.db.converters

import androidx.room.TypeConverter
import com.example.mvvmexample.model.posts.PostsData
import com.example.mvvmexample.model.todos.TodosData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PostsConverter {
    /*@TypeConverter
    fun fromStringPosts(value: String): List<PostsData> {
        val listType = object : TypeToken<List<PostsData>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayListToStringPosts(list: List<PostsData>): String {
        val gson = Gson()
        return gson.toJson(list)
    }*/
    @TypeConverter
    fun fromPostList(postList: List<PostsData?>?): String? {
        val type = object : TypeToken<List<PostsData>>() {}.type
        return Gson().toJson(postList, type)
    }
    @TypeConverter
    fun toPostList(postListString: String?): List<PostsData>? {
        val type = object : TypeToken<List<PostsData>>() {}.type
        return Gson().fromJson<List<PostsData>>(postListString, type)
    }
}