package com.geoffrey.blackjackpractice.data.local

import androidx.room.TypeConverter
import com.geoffrey.blackjackpractice.domain.model.BlackjackPayout
import com.geoffrey.blackjackpractice.domain.model.DeviationClassification
import com.geoffrey.blackjackpractice.domain.model.HandAction
import com.geoffrey.blackjackpractice.domain.model.HandOutcome
import com.geoffrey.blackjackpractice.domain.model.PracticeMode

class Converters {

    @TypeConverter
    fun fromPracticeMode(value: PracticeMode): String = value.name

    @TypeConverter
    fun toPracticeMode(value: String): PracticeMode = PracticeMode.valueOf(value)

    @TypeConverter
    fun fromHandAction(value: HandAction?): String? = value?.name

    @TypeConverter
    fun toHandAction(value: String?): HandAction? = value?.let { HandAction.valueOf(it) }

    @TypeConverter
    fun fromDeviationClassification(value: DeviationClassification): String = value.name

    @TypeConverter
    fun toDeviationClassification(value: String): DeviationClassification = DeviationClassification.valueOf(value)

    @TypeConverter
    fun fromHandOutcome(value: HandOutcome?): String? = value?.name

    @TypeConverter
    fun toHandOutcome(value: String?): HandOutcome? = value?.let { HandOutcome.valueOf(it) }

    @TypeConverter
    fun fromBlackjackPayout(value: BlackjackPayout): String = value.name

    @TypeConverter
    fun toBlackjackPayout(value: String): BlackjackPayout = BlackjackPayout.valueOf(value)
}
