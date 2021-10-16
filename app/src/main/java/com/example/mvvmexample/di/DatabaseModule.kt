 package com.example.mvvmexample.di

import android.app.Application
import androidx.room.Room
import com.example.mvvmexample.db.MVVMExampleDataBase
import com.example.mvvmexample.db.dao.PhotosDao
import com.example.mvvmexample.db.dao.PostsDao
import com.example.mvvmexample.db.dao.TodosDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    fun provideDatabase(application: Application): MVVMExampleDataBase {
        return Room.databaseBuilder(application, MVVMExampleDataBase::class.java, "MVVM_Example_Db")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun providePhotosDao(database: MVVMExampleDataBase): PhotosDao {
        return  database.photosDao
    }

    fun provideTodosDao(database: MVVMExampleDataBase): TodosDao {
        return  database.todosDao
    }

    fun providePostDao(database: MVVMExampleDataBase): PostsDao {
        return  database.postsDao
    }

    single { provideDatabase(androidApplication()) }
    single { providePhotosDao(get()) }
    single { provideTodosDao(get()) }
    single { providePostDao(get()) }
}