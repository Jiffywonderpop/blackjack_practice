package com.geoffrey.blackjackpractice.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.geoffrey.blackjackpractice.domain.model.DeviationClassification
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.HandOutcome

@Entity(
    tableName = "hand_logs",
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
data class HandLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val handIndexInSession: Int,
    val timestamp: Long,
    val playerCards: String,
    val dealerUpcard: String,
    val playerHardTotal: Int,
    val isSoft: Boolean,
    val isPair: Boolean,
    val runningCountAtDecision: Int,
    val trueCountAtDecision: Double,
    val actionTaken: HandAction,
    val optimalBasicAction: HandAction,
    val optimalFinalAction: HandAction,
    val deviationClassification: DeviationClassification,
    val betAmount: Double,
    val outcome: HandOutcome?,
    val payout: Double?,
    val bankrollAfter: Double?
)
