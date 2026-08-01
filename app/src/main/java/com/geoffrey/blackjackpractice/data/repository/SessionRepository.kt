package com.geoffrey.blackjackpractice.data.repository

import com.geoffrey.blackjackpractice.data.local.dao.CountGuessDao
import com.geoffrey.blackjackpractice.data.local.dao.HandLogDao
import com.geoffrey.blackjackpractice.data.local.dao.SessionDao
import com.geoffrey.blackjackpractice.data.local.entity.CountGuessEntity
import com.geoffrey.blackjackpractice.data.local.entity.HandLogEntity
import com.geoffrey.blackjackpractice.data.local.entity.SessionEntity
import com.geoffrey.blackjackpractice.domain.model.HandOutcome
import kotlinx.coroutines.flow.Flow

class SessionRepository(
    private val sessionDao: SessionDao,
    private val handLogDao: HandLogDao,
    private val countGuessDao: CountGuessDao
) {
    suspend fun startSession(session: SessionEntity): Long = sessionDao.insert(session)

    suspend fun endSession(session: SessionEntity) = sessionDao.update(session)

    fun observeAllSessions(): Flow<List<SessionEntity>> = sessionDao.observeAll()

    suspend fun getSession(id: Long): SessionEntity? = sessionDao.getById(id)

    suspend fun logHand(log: HandLogEntity): Long = handLogDao.insert(log)

    suspend fun updateHandOutcome(id: Long, outcome: HandOutcome, payout: Double, bankrollAfter: Double) =
        handLogDao.updateOutcome(id, outcome, payout, bankrollAfter)

    fun observeHandLogsForSession(sessionId: Long): Flow<List<HandLogEntity>> =
        handLogDao.observeForSession(sessionId)

    suspend fun logCountGuess(guess: CountGuessEntity): Long = countGuessDao.insert(guess)

    fun observeCountGuessesForSession(sessionId: Long): Flow<List<CountGuessEntity>> =
        countGuessDao.observeForSession(sessionId)
}
