package com.geoffrey.blackjackpractice.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.geoffrey.blackjackpractice.domain.model.BlackjackPayout
import com.geoffrey.blackjackpractice.domain.model.PracticeMode

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: PracticeMode,
    val startTimestamp: Long,
    val endTimestamp: Long? = null,
    val numDecks: Int,
    val penetrationPercent: Float,
    val dealerHitsSoft17: Boolean,
    val doubleAfterSplit: Boolean,
    val surrenderAllowed: Boolean,
    val blackjackPayout: BlackjackPayout,
    val startingBankroll: Double,
    val endingBankroll: Double? = null
)
