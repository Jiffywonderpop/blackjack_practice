package com.geoffrey.blackjackpractice.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.geoffrey.blackjackpractice.data.local.Converters;
import com.geoffrey.blackjackpractice.data.local.entity.CountGuessEntity;
import com.geoffrey.blackjackpractice.domain.model.PracticeMode;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CountGuessDao_Impl implements CountGuessDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CountGuessEntity> __insertionAdapterOfCountGuessEntity;

  private final Converters __converters = new Converters();

  public CountGuessDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCountGuessEntity = new EntityInsertionAdapter<CountGuessEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `count_guesses` (`id`,`sessionId`,`mode`,`timestamp`,`sequenceNumber`,`actualRunningCount`,`actualTrueCount`,`guessedRunningCount`,`guessedTrueCount`,`elapsedMs`,`cardsShownCount`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CountGuessEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSessionId());
        final String _tmp = __converters.fromPracticeMode(entity.getMode());
        statement.bindString(3, _tmp);
        statement.bindLong(4, entity.getTimestamp());
        statement.bindLong(5, entity.getSequenceNumber());
        statement.bindLong(6, entity.getActualRunningCount());
        statement.bindDouble(7, entity.getActualTrueCount());
        statement.bindLong(8, entity.getGuessedRunningCount());
        if (entity.getGuessedTrueCount() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getGuessedTrueCount());
        }
        if (entity.getElapsedMs() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getElapsedMs());
        }
        if (entity.getCardsShownCount() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getCardsShownCount());
        }
      }
    };
  }

  @Override
  public Object insert(final CountGuessEntity guess, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCountGuessEntity.insertAndReturnId(guess);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CountGuessEntity>> observeForSession(final long sessionId) {
    final String _sql = "SELECT * FROM count_guesses WHERE sessionId = ? ORDER BY sequenceNumber ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sessionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"count_guesses"}, new Callable<List<CountGuessEntity>>() {
      @Override
      @NonNull
      public List<CountGuessEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSequenceNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "sequenceNumber");
          final int _cursorIndexOfActualRunningCount = CursorUtil.getColumnIndexOrThrow(_cursor, "actualRunningCount");
          final int _cursorIndexOfActualTrueCount = CursorUtil.getColumnIndexOrThrow(_cursor, "actualTrueCount");
          final int _cursorIndexOfGuessedRunningCount = CursorUtil.getColumnIndexOrThrow(_cursor, "guessedRunningCount");
          final int _cursorIndexOfGuessedTrueCount = CursorUtil.getColumnIndexOrThrow(_cursor, "guessedTrueCount");
          final int _cursorIndexOfElapsedMs = CursorUtil.getColumnIndexOrThrow(_cursor, "elapsedMs");
          final int _cursorIndexOfCardsShownCount = CursorUtil.getColumnIndexOrThrow(_cursor, "cardsShownCount");
          final List<CountGuessEntity> _result = new ArrayList<CountGuessEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CountGuessEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final PracticeMode _tmpMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMode);
            _tmpMode = __converters.toPracticeMode(_tmp);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpSequenceNumber;
            _tmpSequenceNumber = _cursor.getInt(_cursorIndexOfSequenceNumber);
            final int _tmpActualRunningCount;
            _tmpActualRunningCount = _cursor.getInt(_cursorIndexOfActualRunningCount);
            final double _tmpActualTrueCount;
            _tmpActualTrueCount = _cursor.getDouble(_cursorIndexOfActualTrueCount);
            final int _tmpGuessedRunningCount;
            _tmpGuessedRunningCount = _cursor.getInt(_cursorIndexOfGuessedRunningCount);
            final Double _tmpGuessedTrueCount;
            if (_cursor.isNull(_cursorIndexOfGuessedTrueCount)) {
              _tmpGuessedTrueCount = null;
            } else {
              _tmpGuessedTrueCount = _cursor.getDouble(_cursorIndexOfGuessedTrueCount);
            }
            final Long _tmpElapsedMs;
            if (_cursor.isNull(_cursorIndexOfElapsedMs)) {
              _tmpElapsedMs = null;
            } else {
              _tmpElapsedMs = _cursor.getLong(_cursorIndexOfElapsedMs);
            }
            final Integer _tmpCardsShownCount;
            if (_cursor.isNull(_cursorIndexOfCardsShownCount)) {
              _tmpCardsShownCount = null;
            } else {
              _tmpCardsShownCount = _cursor.getInt(_cursorIndexOfCardsShownCount);
            }
            _item = new CountGuessEntity(_tmpId,_tmpSessionId,_tmpMode,_tmpTimestamp,_tmpSequenceNumber,_tmpActualRunningCount,_tmpActualTrueCount,_tmpGuessedRunningCount,_tmpGuessedTrueCount,_tmpElapsedMs,_tmpCardsShownCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CountAccuracySummary>> observeCountAccuracyByMode() {
    final String _sql = "\n"
            + "        SELECT mode AS mode,\n"
            + "               COUNT(*) AS totalGuesses,\n"
            + "               SUM(CASE WHEN guessedRunningCount = actualRunningCount THEN 1 ELSE 0 END) AS exactMatches,\n"
            + "               AVG(ABS(guessedRunningCount - actualRunningCount)) AS avgAbsError\n"
            + "        FROM count_guesses\n"
            + "        GROUP BY mode\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"count_guesses"}, new Callable<List<CountAccuracySummary>>() {
      @Override
      @NonNull
      public List<CountAccuracySummary> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMode = 0;
          final int _cursorIndexOfTotalGuesses = 1;
          final int _cursorIndexOfExactMatches = 2;
          final int _cursorIndexOfAvgAbsError = 3;
          final List<CountAccuracySummary> _result = new ArrayList<CountAccuracySummary>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CountAccuracySummary _item;
            final PracticeMode _tmpMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMode);
            _tmpMode = __converters.toPracticeMode(_tmp);
            final int _tmpTotalGuesses;
            _tmpTotalGuesses = _cursor.getInt(_cursorIndexOfTotalGuesses);
            final int _tmpExactMatches;
            _tmpExactMatches = _cursor.getInt(_cursorIndexOfExactMatches);
            final double _tmpAvgAbsError;
            _tmpAvgAbsError = _cursor.getDouble(_cursorIndexOfAvgAbsError);
            _item = new CountAccuracySummary(_tmpMode,_tmpTotalGuesses,_tmpExactMatches,_tmpAvgAbsError);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
