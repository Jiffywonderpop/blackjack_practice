package com.geoffrey.blackjackpractice.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.geoffrey.blackjackpractice.data.local.entity.HandLogEntity
import com.geoffrey.blackjackpractice.domain.model.DeviationClassification
import com.geoffrey.blackjackpractice.domain.model.HandOutcome
import com.geoffrey.blackjackpractice.domain.review.DecisionRecord
import kotlinx.coroutines.flow.Flow
data class OutcomeSummary(
    val outcome: HandOutcome,
    val count: Int,
    val totalPayout: Double?
)

data class AdherenceSummary(
    val deviationClassification: DeviationClassification,
    val count: Int
)

data class BankrollPoint(
    val handLogId: Long,
    val timestamp: Long,
    val bankrollAfter: Double
)

@Dao
interface HandLogDao {
    @Insert
    suspend fun insert(log: HandLogEntity): Long

    @Query(
        """
        UPDATE hand_logs
        SET outcome = :outcome, payout = :payout, bankrollAfter = :bankrollAfter
        WHERE id = :id
        """
    )
    suspend fun updateOutcome(id: Long, outcome: HandOutcome, payout: Double, bankrollAfter: Double)

    @Query("SELECT * FROM hand_logs WHERE sessionId = :sessionId ORDER BY handIndexInSession ASC")
    fun observeForSession(sessionId: Long): Flow<List<HandLogEntity>>

    @Query(
        """
        SELECT outcome AS outcome, COUNT(*) AS count, SUM(payout) AS totalPayout
        FROM hand_logs
        WHERE outcome IS NOT NULL
        GROUP BY outcome
        """
    )
    fun observeOutcomeSummary(): Flow<List<OutcomeSummary>>

    @Query(
        """
        SELECT deviationClassification AS deviationClassification, COUNT(*) AS count
        FROM hand_logs
        WHERE isDecision = 1
        GROUP BY deviationClassification
        """
    )
    fun observeAdherenceSummary(): Flow<List<AdherenceSummary>>

    @Query(
        """
        SELECT id AS handLogId, timestamp AS timestamp, bankrollAfter AS bankrollAfter
        FROM hand_logs
        WHERE bankrollAfter IS NOT NULL
        ORDER BY timestamp ASC
        """
    )
    fun observeBankrollPoints(): Flow<List<BankrollPoint>>

    @Query(
        """
        SELECT s.mode AS mode,
               h.timestamp AS timestamp,
               h.playerCards AS playerCards,
               h.dealerUpcard AS dealerUpcard,
               h.playerHardTotal AS playerHardTotal,
               h.isSoft AS isSoft,
               h.isPair AS isPair,
               h.trueCountAtDecision AS trueCount,
               h.actionTaken AS actionTaken,
               h.optimalBasicAction AS optimalBasicAction,
               h.optimalFinalAction AS optimalFinalAction,
               h.deviationClassification AS classification
        FROM hand_logs h
        INNER JOIN sessions s ON s.id = h.sessionId
        WHERE h.isDecision = 1
        ORDER BY h.timestamp DESC
        """
    )
    fun observeDecisionRecords(): Flow<List<DecisionRecord>>
}
