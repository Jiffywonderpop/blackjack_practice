package com.geoffrey.blackjackpractice.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geoffrey.blackjackpractice.data.local.entity.HandLogEntity
import com.geoffrey.blackjackpractice.data.local.entity.SessionEntity
import com.geoffrey.blackjackpractice.data.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SessionHistoryViewModel(private val repository: SessionRepository) : ViewModel() {

    val sessions: StateFlow<List<SessionEntity>> = repository.observeAllSessions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun observeHandLogs(sessionId: Long): Flow<List<HandLogEntity>> = repository.observeHandLogsForSession(sessionId)
}
