package com.geoffrey.blackjackpractice.domain.review

import com.geoffrey.blackjackpractice.domain.model.CardCodec
import com.geoffrey.blackjackpractice.domain.model.DeviationClassification
import com.geoffrey.blackjackpractice.domain.model.Hand
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.strategy.BasicStrategyTables
import com.geoffrey.blackjackpractice.domain.strategy.IndexPlay
import com.geoffrey.blackjackpractice.domain.strategy.Illustrious18Table
import com.geoffrey.blackjackpractice.domain.strategy.ThresholdDirection

enum class HandCategory { HARD, SOFT, PAIR }

/**
 * A strategy-chart cell: hand shape vs dealer upcard. [playerValue] is the hand total for
 * HARD/SOFT, or the paired card's value (2..11, ace = 11) for PAIR. [dealerValue] is 2..11.
 */
data class Situation(val category: HandCategory, val playerValue: Int, val dealerValue: Int) {
    val label: String
        get() {
            val dealer = valueLabel(dealerValue)
            return when (category) {
                HandCategory.HARD -> "Hard $playerValue vs $dealer"
                HandCategory.SOFT -> "Soft $playerValue vs $dealer"
                HandCategory.PAIR -> valueLabel(playerValue).let { "$it,$it vs $dealer" }
            }
        }

    private fun valueLabel(v: Int) = if (v == 11) "A" else v.toString()
}

/**
 * A recurring decision the player faces: a [Situation], split by whether the count called for
 * an index-play deviation. "16 vs 10 when the count says stand" and "16 vs 10 when it says hit"
 * are separate cases because they're separate things to get wrong.
 */
data class CaseKey(val situation: Situation, val countIndicatedDeviation: Boolean)

data class MistakeCase(
    val key: CaseKey,
    val indexPlay: IndexPlay?,
    val attempts: Int,
    val mistakes: Int,
    /** The count-adjusted optimal action (most common one if rule changes varied it). */
    val correctAction: HandAction,
    /** Wrong actions taken, most frequent first. */
    val wrongActions: List<Pair<HandAction, Int>>,
    val missedDeviations: Int,
    val incorrectDeviations: Int,
    val basicStrategyErrors: Int,
    val mistakeTrueCountRange: ClosedFloatingPointRange<Double>?,
    val lastMistakeTimestamp: Long?,
    /** Most recent mistakes first. */
    val recentMistakes: List<DecisionRecord>,
    /** Every record in the case, used to seed the review drill. */
    val records: List<DecisionRecord>
) {
    val errorRate: Double get() = if (attempts == 0) 0.0 else mistakes.toDouble() / attempts
    val isCountCase: Boolean get() = indexPlay != null

    /** Human-readable statement of what the count says in this case. */
    val countGuidance: String
        get() {
            val play = indexPlay ?: return "Basic strategy (no index play)"
            val threshold = when (play.direction) {
                ThresholdDirection.AT_OR_ABOVE -> "TC ≥ ${signed(play.threshold)}"
                ThresholdDirection.BELOW -> "TC < ${signed(play.threshold)}"
            }
            val deviation = play.deviationAction.displayName()
            return if (key.countIndicatedDeviation) {
                "Count deviation: $deviation at $threshold"
            } else {
                "Count below index: play basic strategy ($deviation only at $threshold)"
            }
        }

    private fun signed(v: Int) = if (v > 0) "+$v" else v.toString()
}

data class MistakeReport(
    val totalDecisions: Int,
    val correctDecisions: Int,
    val basicStrategyErrors: Int,
    val missedDeviations: Int,
    val incorrectDeviations: Int,
    /** Cases with at least one mistake, worst first. */
    val cases: List<MistakeCase>
) {
    val accuracy: Double get() = if (totalDecisions == 0) 0.0 else correctDecisions.toDouble() / totalDecisions
    val totalMistakes: Int get() = totalDecisions - correctDecisions

    companion object {
        val EMPTY = MistakeReport(0, 0, 0, 0, 0, emptyList())
    }
}

fun HandAction.displayName(): String = name.lowercase().replaceFirstChar { it.uppercase() }

/**
 * Groups logged decisions into recurring cases and ranks the ones the player most often gets
 * wrong relative to count-adjusted optimal play.
 */
object MistakeAnalyzer {

    private const val MAX_RECENT_MISTAKES = 5

    fun situationOf(record: DecisionRecord): Situation? {
        val dealerCard = CardCodec.decode(record.dealerUpcard) ?: return null
        val dealerValue = dealerCard.rank.strategyColumn
        return when {
            record.isPair -> {
                val first = CardCodec.decodeAll(record.playerCards)?.firstOrNull() ?: return null
                Situation(HandCategory.PAIR, first.rank.strategyColumn, dealerValue)
            }
            record.isSoft -> {
                // isSoft means exactly one ace is counted as 11 on top of the all-aces-as-1 total.
                val total = record.playerHardTotal + 10
                if (total >= 21) null else Situation(HandCategory.SOFT, total, dealerValue)
            }
            else -> {
                if (record.playerHardTotal >= 21) null
                else Situation(HandCategory.HARD, record.playerHardTotal, dealerValue)
            }
        }
    }

    fun caseKeyOf(record: DecisionRecord): CaseKey? =
        situationOf(record)?.let { CaseKey(it, record.countIndicatedDeviation) }

    fun analyze(records: List<DecisionRecord>): MistakeReport {
        if (records.isEmpty()) return MistakeReport.EMPTY

        val grouped = LinkedHashMap<CaseKey, MutableList<DecisionRecord>>()
        var total = 0
        var correct = 0
        var basicErrors = 0
        var missed = 0
        var incorrectDev = 0

        for (record in records) {
            val key = caseKeyOf(record) ?: continue
            grouped.getOrPut(key) { mutableListOf() }.add(record)
            total++
            if (record.isCorrect) correct++
            when (record.classification) {
                DeviationClassification.INCORRECT_PLAY -> basicErrors++
                DeviationClassification.MISSED_DEVIATION -> missed++
                DeviationClassification.INCORRECT_DEVIATION -> incorrectDev++
                else -> Unit
            }
        }

        val cases = grouped.mapNotNull { (key, group) -> buildCase(key, group) }
            .sortedWith(
                compareByDescending<MistakeCase> { it.mistakes }
                    .thenByDescending { it.errorRate }
                    .thenByDescending { it.lastMistakeTimestamp ?: 0L }
            )

        return MistakeReport(total, correct, basicErrors, missed, incorrectDev, cases)
    }

    private fun buildCase(key: CaseKey, group: List<DecisionRecord>): MistakeCase? {
        val mistakes = group.filterNot { it.isCorrect }.sortedByDescending { it.timestamp }
        if (mistakes.isEmpty()) return null

        val correctAction = group.groupingBy { it.optimalFinalAction }.eachCount().maxBy { it.value }.key
        val wrongActions = mistakes.groupingBy { it.actionTaken }.eachCount()
            .entries.sortedByDescending { it.value }
            .map { it.key to it.value }
        val tcs = mistakes.map { it.trueCount }

        return MistakeCase(
            key = key,
            indexPlay = indexPlayFor(group.first()),
            attempts = group.size,
            mistakes = mistakes.size,
            correctAction = correctAction,
            wrongActions = wrongActions,
            missedDeviations = mistakes.count { it.classification == DeviationClassification.MISSED_DEVIATION },
            incorrectDeviations = mistakes.count { it.classification == DeviationClassification.INCORRECT_DEVIATION },
            basicStrategyErrors = mistakes.count { it.classification == DeviationClassification.INCORRECT_PLAY },
            mistakeTrueCountRange = tcs.min()..tcs.max(),
            lastMistakeTimestamp = mistakes.first().timestamp,
            recentMistakes = mistakes.take(MAX_RECENT_MISTAKES),
            records = group
        )
    }

    /** Every record in a case shares the same hand shape and upcard, so any one identifies the index play. */
    private fun indexPlayFor(sample: DecisionRecord): IndexPlay? {
        val cards = CardCodec.decodeAll(sample.playerCards) ?: return null
        val dealer = CardCodec.decode(sample.dealerUpcard) ?: return null
        return Illustrious18Table.findIndexPlay(Hand(cards), BasicStrategyTables.dealerColumnIndex(dealer.rank))
    }
}
