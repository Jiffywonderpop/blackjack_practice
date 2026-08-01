package com.geoffrey.blackjackpractice.data.repository

import com.geoffrey.blackjackpractice.data.local.dao.AdherenceSummary
import com.geoffrey.blackjackpractice.data.local.dao.BankrollPoint
import com.geoffrey.blackjackpractice.data.local.dao.CountAccuracySummary
import com.geoffrey.blackjackpractice.data.local.dao.CountGuessDao
import com.geoffrey.blackjackpractice.data.local.dao.HandLogDao
import com.geoffrey.blackjackpractice.data.local.dao.OutcomeSummary
import kotlinx.coroutines.flow.Flow

class StatsRepository(
    private val handLogDao: HandLogDao,
    private val countGuessDao: CountGuessDao
) {
    fun observeOutcomeSummary(): Flow<List<OutcomeSummary>> = handLogDao.observeOutcomeSummary()

    fun observeAdherenceSummary(): Flow<List<AdherenceSummary>> = handLogDao.observeAdherenceSummary()

    fun observeBankrollPoints(): Flow<List<BankrollPoint>> = handLogDao.observeBankrollPoints()

    fun observeCountAccuracyByMode(): Flow<List<CountAccuracySummary>> = countGuessDao.observeCountAccuracyByMode()
}
