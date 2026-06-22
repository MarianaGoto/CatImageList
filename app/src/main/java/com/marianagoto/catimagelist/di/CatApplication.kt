package com.marianagoto.catimagelist.di
import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CatApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CatApplication)
            modules(appModule)
        }
    }
}