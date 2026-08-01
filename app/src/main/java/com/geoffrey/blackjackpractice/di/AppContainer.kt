package com.geoffrey.blackjackpractice.di

import android.content.Context
import androidx.room.Room
import com.geoffrey.blackjackpractice.data.local.AppDatabase
import com.geoffrey.blackjackpractice.data.repository.SessionRepository
import com.geoffrey.blackjackpractice.data.repository.StatsRepository
import com.geoffrey.blackjackpractice.data.settings.RulesSettingsRepository

/** Hand-written DI container: appropriate at this app's scale (single module, ~6 ViewModels). */
class AppContainer(context: Context) {

    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME
    ).build()

    val settingsRepository: RulesSettingsRepository = RulesSettingsRepository(context.applicationContext)

    val sessionRepository: SessionRepository = SessionRepository(
        sessionDao = database.sessionDao(),
        handLogDao = database.handLogDao(),
        countGuessDao = database.countGuessDao()
    )

    val statsRepository: StatsRepository = StatsRepository(
        handLogDao = database.handLogDao(),
        countGuessDao = database.countGuessDao()
    )
}
