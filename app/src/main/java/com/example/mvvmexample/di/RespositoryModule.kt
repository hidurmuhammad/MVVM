package com.example.mvvmexample.di

import android.content.Context
import com.example.mvvmexample.api.ApiService
import com.example.mvvmexample.db.dao.PhotosDao
import com.example.mvvmexample.db.dao.PostsDao
import com.example.mvvmexample.db.dao.TodosDao
import com.example.mvvmexample.respository.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    fun providePhotoListRepository(api: ApiService, context: Context, dao : PhotosDao): PhotosListRepository {
        return PhotoListRepositoryImpl(api, context, dao)
    }
    single { providePhotoListRepository(get(), androidContext(), get()) }

    fun provideTodosListRepository(api: ApiService, context: Context, dao : TodosDao): TodosListRepository {
        return TodoListRepositoryImpl(api, context, dao)
    }
    single { provideTodosListRepository(get(), androidContext(), get()) }

    fun providePostsListRepository(api: ApiService, context: Context, dao : PostsDao): PostListRepository {
        return PostListRepositoryImpl(api, context, dao)
    }
    single { providePostsListRepository(get(), androidContext(), get()) }


}