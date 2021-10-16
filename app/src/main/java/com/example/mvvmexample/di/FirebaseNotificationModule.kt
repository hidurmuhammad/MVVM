package com.example.mvvmexample.di

import com.example.mvvmexample.firebaseservice.fcm.FBMessagingService
import org.koin.dsl.module

val firebaseNotificationModule = module {
    single {
        FBMessagingService()
    }

}