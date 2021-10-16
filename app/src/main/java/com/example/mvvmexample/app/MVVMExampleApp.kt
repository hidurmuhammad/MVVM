package com.example.mvvmexample.app

import android.app.Application
import com.example.mvvmexample.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MVVMExampleApp : Application() {

    override fun onCreate() {
        super.onCreate()


        startKoin {
            // use Android logger - Level.INFO by default
            androidLogger()

            // logger(Level.INFO)

            //inject Android context
            androidContext(this@MVVMExampleApp)
            modules(
                firebaseNotificationModule,
                apiModule,
                repositoryModule,
                viewModelModule,
                networkModule,
                databaseModule
            )
        }
    }

}