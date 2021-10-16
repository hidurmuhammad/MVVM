package com.example.mvvmexample.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mvvmexample.db.converters.PhotoConverter
import com.example.mvvmexample.db.converters.PostsConverter
import com.example.mvvmexample.db.converters.TodosConverter
import com.example.mvvmexample.db.dao.PhotosDao
import com.example.mvvmexample.db.dao.PostsDao
import com.example.mvvmexample.db.dao.TodosDao
import com.example.mvvmexample.model.PhotoData
import com.example.mvvmexample.model.posts.PostsData
import com.example.mvvmexample.model.todos.TodosData

@Database(
    entities = [PhotoData::class, TodosData::class, PostsData::class], version = 1, exportSchema = false
)

@TypeConverters(
    PhotoConverter::class, TodosConverter::class, PostsConverter::class
)
abstract class MVVMExampleDataBase : RoomDatabase() {
    abstract val photosDao: PhotosDao
    abstract val todosDao: TodosDao
    abstract val postsDao:PostsDao
}
