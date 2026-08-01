package com.geoffrey.blackjackpractice

import android.app.Application
import com.geoffrey.blackjackpractice.di.AppContainer

class BlackjackApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
