package com.geoffrey.blackjackpractice.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.geoffrey.blackjackpractice.data.local.dao.CountGuessDao
import com.geoffrey.blackjackpractice.data.local.dao.HandLogDao
import com.geoffrey.blackjackpractice.data.local.dao.SessionDao
import com.geoffrey.blackjackpractice.data.local.entity.CountGuessEntity
import com.geoffrey.blackjackpractice.data.local.entity.HandLogEntity
import com.geoffrey.blackjackpractice.data.local.entity.SessionEntity

@Database(
    entities = [SessionEntity::class, HandLogEntity::class, CountGuessEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun handLogDao(): HandLogDao
    abstract fun countGuessDao(): CountGuessDao

    companion object {
        const val DATABASE_NAME = "blackjack_practice.db"
    }
}
