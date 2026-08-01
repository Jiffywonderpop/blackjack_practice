package com.geoffrey.blackjackpractice.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.geoffrey.blackjackpractice.domain.model.BetSpreadMode
import com.geoffrey.blackjackpractice.domain.model.BlackjackPayout
import com.geoffrey.blackjackpractice.domain.model.TableRules
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

data class AppSettings(
    val tableRules: TableRules = TableRules(),
    val startingBankroll: Double = 1000.0,
    val betSpreadMode: BetSpreadMode = BetSpreadMode.FLAT,
    val flatBetAmount: Double = 10.0,
    val showLiveCount: Boolean = true,
    val countGuessPromptIntervalHands: Int = 5
)

class RulesSettingsRepository(private val context: Context) {

    private object Keys {
        val NUM_DECKS = intPreferencesKey("num_decks")
        val PENETRATION = floatPreferencesKey("penetration_percent")
        val DEALER_HITS_SOFT_17 = booleanPreferencesKey("dealer_hits_soft_17")
        val DOUBLE_AFTER_SPLIT = booleanPreferencesKey("double_after_split")
        val SURRENDER_ALLOWED = booleanPreferencesKey("surrender_allowed")
        val RESPLIT_ACES_ALLOWED = booleanPreferencesKey("resplit_aces_allowed")
        val BLACKJACK_PAYOUT = stringPreferencesKey("blackjack_payout")
        val MAX_SPLIT_HANDS = intPreferencesKey("max_split_hands")
        val STARTING_BANKROLL = doublePreferencesKey("starting_bankroll")
        val BET_SPREAD_MODE = stringPreferencesKey("bet_spread_mode")
        val FLAT_BET_AMOUNT = doublePreferencesKey("flat_bet_amount")
        val SHOW_LIVE_COUNT = booleanPreferencesKey("show_live_count")
        val COUNT_GUESS_INTERVAL = intPreferencesKey("count_guess_interval")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            tableRules = TableRules(
                numDecks = prefs[Keys.NUM_DECKS] ?: 6,
                penetrationPercent = prefs[Keys.PENETRATION] ?: 0.75f,
                dealerHitsSoft17 = prefs[Keys.DEALER_HITS_SOFT_17] ?: false,
                doubleAfterSplit = prefs[Keys.DOUBLE_AFTER_SPLIT] ?: true,
                surrenderAllowed = prefs[Keys.SURRENDER_ALLOWED] ?: true,
                resplitAcesAllowed = prefs[Keys.RESPLIT_ACES_ALLOWED] ?: false,
                blackjackPayout = prefs[Keys.BLACKJACK_PAYOUT]?.let { BlackjackPayout.valueOf(it) }
                    ?: BlackjackPayout.THREE_TO_TWO,
                maxSplitHands = prefs[Keys.MAX_SPLIT_HANDS] ?: 4
            ),
            startingBankroll = prefs[Keys.STARTING_BANKROLL] ?: 1000.0,
            betSpreadMode = prefs[Keys.BET_SPREAD_MODE]?.let { BetSpreadMode.valueOf(it) } ?: BetSpreadMode.FLAT,
            flatBetAmount = prefs[Keys.FLAT_BET_AMOUNT] ?: 10.0,
            showLiveCount = prefs[Keys.SHOW_LIVE_COUNT] ?: true,
            countGuessPromptIntervalHands = prefs[Keys.COUNT_GUESS_INTERVAL] ?: 5
        )
    }

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        val current = settingsFlow.first()
        writeSettings(transform(current))
    }

    suspend fun writeSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NUM_DECKS] = settings.tableRules.numDecks
            prefs[Keys.PENETRATION] = settings.tableRules.penetrationPercent
            prefs[Keys.DEALER_HITS_SOFT_17] = settings.tableRules.dealerHitsSoft17
            prefs[Keys.DOUBLE_AFTER_SPLIT] = settings.tableRules.doubleAfterSplit
            prefs[Keys.SURRENDER_ALLOWED] = settings.tableRules.surrenderAllowed
            prefs[Keys.RESPLIT_ACES_ALLOWED] = settings.tableRules.resplitAcesAllowed
            prefs[Keys.BLACKJACK_PAYOUT] = settings.tableRules.blackjackPayout.name
            prefs[Keys.MAX_SPLIT_HANDS] = settings.tableRules.maxSplitHands
            prefs[Keys.STARTING_BANKROLL] = settings.startingBankroll
            prefs[Keys.BET_SPREAD_MODE] = settings.betSpreadMode.name
            prefs[Keys.FLAT_BET_AMOUNT] = settings.flatBetAmount
            prefs[Keys.SHOW_LIVE_COUNT] = settings.showLiveCount
            prefs[Keys.COUNT_GUESS_INTERVAL] = settings.countGuessPromptIntervalHands
        }
    }
}
