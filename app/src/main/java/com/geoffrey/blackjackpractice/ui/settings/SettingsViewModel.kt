package com.geoffrey.blackjackpractice.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geoffrey.blackjackpractice.data.settings.AppSettings
import com.geoffrey.blackjackpractice.data.settings.RulesSettingsRepository
import com.geoffrey.blackjackpractice.domain.model.BetSpreadMode
import com.geoffrey.blackjackpractice.domain.model.BlackjackPayout
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: RulesSettingsRepository) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    fun setNumDecks(numDecks: Int) = update { it.copy(tableRules = it.tableRules.copy(numDecks = numDecks)) }

    fun setPenetration(penetrationPercent: Float) =
        update { it.copy(tableRules = it.tableRules.copy(penetrationPercent = penetrationPercent)) }

    fun setDealerHitsSoft17(value: Boolean) = update { it.copy(tableRules = it.tableRules.copy(dealerHitsSoft17 = value)) }

    fun setDoubleAfterSplit(value: Boolean) = update { it.copy(tableRules = it.tableRules.copy(doubleAfterSplit = value)) }

    fun setSurrenderAllowed(value: Boolean) = update { it.copy(tableRules = it.tableRules.copy(surrenderAllowed = value)) }

    fun setResplitAcesAllowed(value: Boolean) = update { it.copy(tableRules = it.tableRules.copy(resplitAcesAllowed = value)) }

    fun setBlackjackPayout(payout: BlackjackPayout) = update { it.copy(tableRules = it.tableRules.copy(blackjackPayout = payout)) }

    fun setStartingBankroll(amount: Double) = update { it.copy(startingBankroll = amount) }

    fun setBetSpreadMode(mode: BetSpreadMode) = update { it.copy(betSpreadMode = mode) }

    fun setFlatBetAmount(amount: Double) = update { it.copy(flatBetAmount = amount) }

    fun setShowLiveCount(value: Boolean) = update { it.copy(showLiveCount = value) }

    fun setCountGuessInterval(hands: Int) = update { it.copy(countGuessPromptIntervalHands = hands) }

    private fun update(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch { repository.update(transform) }
    }
}
