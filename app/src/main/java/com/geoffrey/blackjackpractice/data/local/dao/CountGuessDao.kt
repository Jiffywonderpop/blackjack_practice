package com.geoffrey.blackjackpractice.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.geoffrey.blackjackpractice.data.local.entity.CountGuessEntity
import com.geoffrey.blackjackpractice.domain.model.PracticeMode
import kotlinx.coroutines.flow.Flow

data class CountAccuracySummary(
    val mode: PracticeMode,
    val totalGuesses: Int,
    val exactMatches: Int,
    val avgAbsError: Double
)

@Dao
interface CountGuessDao {
    @Insert
    suspend fun insert(guess: CountGuessEntity): Long

    @Query("SELECT * FROM count_guesses WHERE sessionId = :sessionId ORDER BY sequenceNumber ASC")
    fun observeForSession(sessionId: Long): Flow<List<CountGuessEntity>>

    @Query(
        """
        SELECT mode AS mode,
               COUNT(*) AS totalGuesses,
               SUM(CASE WHEN guessedRunningCount = actualRunningCount THEN 1 ELSE 0 END) AS exactMatches,
               AVG(ABS(guessedRunningCount - actualRunningCount)) AS avgAbsError
        FROM count_guesses
        GROUP BY mode
        """
    )
    fun observeCountAccuracyByMode(): Flow<List<CountAccuracySummary>>
}
