package org.navigator.app

import android.app.Application
import org.navigator.navigator.RouteNavigationContainer

class App: Application() {
    val router = RouteNavigationContainer

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object {
        lateinit var INSTANCE: App
    }
}