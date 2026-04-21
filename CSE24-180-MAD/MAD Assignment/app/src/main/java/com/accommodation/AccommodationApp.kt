package com.accommodation

import android.app.Application
import com.accommodation.data.AppContainer

class AccommodationApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
