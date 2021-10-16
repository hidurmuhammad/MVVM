package com.example.mvvmexample.di

import android.util.Log
import com.example.mvvmexample.api.ApiService
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule= module {
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    single { provideApiService(get()) }

}