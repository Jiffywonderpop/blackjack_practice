package com.geoffrey.blackjackpractice.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.geoffrey.blackjackpractice.domain.model.PracticeMode

@Entity(
    tableName = "count_guesses",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class CountGuessEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val mode: PracticeMode,
    val timestamp: Long,
    val sequenceNumber: Int,
    val actualRunningCount: Int,
    val actualTrueCount: Double,
    val guessedRunningCount: Int,
    val guessedTrueCount: Double? = null,
    val elapsedMs: Long? = null,
    val cardsShownCount: Int? = null
)
