package com.example.bandsintownstreamsample

import android.app.Application

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // We're using dependency injection but this works for the sample
        LivePlayerFeatureConfig(applicationContext)
    }
}
