package com.geoffrey.blackjackpractice.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.geoffrey.blackjackpractice.data.local.Converters;
import com.geoffrey.blackjackpractice.data.local.entity.HandLogEntity;
import com.geoffrey.blackjackpractice.domain.model.DeviationClassification;
import com.geoffrey.blackjackpractice.domain.model.HandAction;
import com.geoffrey.blackjackpractice.domain.model.HandOutcome;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.IllegalStateException;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class HandLogDao_Impl implements HandLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<HandLogEntity> __insertionAdapterOfHandLogEntity;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfUpdateOutcome;

  public HandLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHandLogEntity = new EntityInsertionAdapter<HandLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `hand_logs` (`id`,`sessionId`,`handIndexInSession`,`timestamp`,`playerCards`,`dealerUpcard`,`playerHardTotal`,`isSoft`,`isPair`,`runningCountAtDecision`,`trueCountAtDecision`,`actionTaken`,`optimalBasicAction`,`optimalFinalAction`,`deviationClassification`,`betAmount`,`outcome`,`payout`,`bankrollAfter`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HandLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSessionId());
        statement.bindLong(3, entity.getHandIndexInSession());
        statement.bindLong(4, entity.getTimestamp());
        statement.bindString(5, entity.getPlayerCards());
        statement.bindString(6, entity.getDealerUpcard());
        statement.bindLong(7, entity.getPlayerHardTotal());
        final int _tmp = entity.isSoft() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isPair() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        statement.bindLong(10, entity.getRunningCountAtDecision());
        statement.bindDouble(11, entity.getTrueCountAtDecision());
        final String _tmp_2 = __converters.fromHandAction(entity.getActionTaken());
        if (_tmp_2 == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, _tmp_2);
        }
        final String _tmp_3 = __converters.fromHandAction(entity.getOptimalBasicAction());
        if (_tmp_3 == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, _tmp_3);
        }
        final String _tmp_4 = __converters.fromHandAction(entity.getOptimalFinalAction());
        if (_tmp_4 == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, _tmp_4);
        }
        final String _tmp_5 = __converters.fromDeviationClassification(entity.getDeviationClassification());
        statement.bindString(15, _tmp_5);
        statement.bindDouble(16, entity.getBetAmount());
        final String _tmp_6 = __converters.fromHandOutcome(entity.getOutcome());
        if (_tmp_6 == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, _tmp_6);
        }
        if (entity.getPayout() == null) {
          statement.bindNull(18);
        } else {
          statement.bindDouble(18, entity.getPayout());
        }
        if (entity.getBankrollAfter() == null) {
          statement.bindNull(19);
        } else {
          statement.bindDouble(19, entity.getBankrollAfter());
        }
      }
    };
    this.__preparedStmtOfUpdateOutcome = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE hand_logs\n"
                + "        SET outcome = ?, payout = ?, bankrollAfter = ?\n"
                + "        WHERE id = ?\n"
                + "        ";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final HandLogEntity log, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfHandLogEntity.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateOutcome(final long id, final HandOutcome outcome, final double payout,
      final double bankrollAfter, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateOutcome.acquire();
        int _argIndex = 1;
        final String _tmp = __converters.fromHandOutcome(outcome);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _tmp);
        }
        _argIndex = 2;
        _stmt.bindDouble(_argIndex, payout);
        _argIndex = 3;
        _stmt.bindDouble(_argIndex, bankrollAfter);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateOutcome.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<HandLogEntity>> observeForSession(final long sessionId) {
    final String _sql = "SELECT * FROM hand_logs WHERE sessionId = ? ORDER BY handIndexInSession ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sessionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"hand_logs"}, new Callable<List<HandLogEntity>>() {
      @Override
      @NonNull
      public List<HandLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfHandIndexInSession = CursorUtil.getColumnIndexOrThrow(_cursor, "handIndexInSession");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPlayerCards = CursorUtil.getColumnIndexOrThrow(_cursor, "playerCards");
          final int _cursorIndexOfDealerUpcard = CursorUtil.getColumnIndexOrThrow(_cursor, "dealerUpcard");
          final int _cursorIndexOfPlayerHardTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "playerHardTotal");
          final int _cursorIndexOfIsSoft = CursorUtil.getColumnIndexOrThrow(_cursor, "isSoft");
          final int _cursorIndexOfIsPair = CursorUtil.getColumnIndexOrThrow(_cursor, "isPair");
          final int _cursorIndexOfRunningCountAtDecision = CursorUtil.getColumnIndexOrThrow(_cursor, "runningCountAtDecision");
          final int _cursorIndexOfTrueCountAtDecision = CursorUtil.getColumnIndexOrThrow(_cursor, "trueCountAtDecision");
          final int _cursorIndexOfActionTaken = CursorUtil.getColumnIndexOrThrow(_cursor, "actionTaken");
          final int _cursorIndexOfOptimalBasicAction = CursorUtil.getColumnIndexOrThrow(_cursor, "optimalBasicAction");
          final int _cursorIndexOfOptimalFinalAction = CursorUtil.getColumnIndexOrThrow(_cursor, "optimalFinalAction");
          final int _cursorIndexOfDeviationClassification = CursorUtil.getColumnIndexOrThrow(_cursor, "deviationClassification");
          final int _cursorIndexOfBetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "betAmount");
          final int _cursorIndexOfOutcome = CursorUtil.getColumnIndexOrThrow(_cursor, "outcome");
          final int _cursorIndexOfPayout = CursorUtil.getColumnIndexOrThrow(_cursor, "payout");
          final int _cursorIndexOfBankrollAfter = CursorUtil.getColumnIndexOrThrow(_cursor, "bankrollAfter");
          final List<HandLogEntity> _result = new ArrayList<HandLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HandLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final int _tmpHandIndexInSession;
            _tmpHandIndexInSession = _cursor.getInt(_cursorIndexOfHandIndexInSession);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPlayerCards;
            _tmpPlayerCards = _cursor.getString(_cursorIndexOfPlayerCards);
            final String _tmpDealerUpcard;
            _tmpDealerUpcard = _cursor.getString(_cursorIndexOfDealerUpcard);
            final int _tmpPlayerHardTotal;
            _tmpPlayerHardTotal = _cursor.getInt(_cursorIndexOfPlayerHardTotal);
            final boolean _tmpIsSoft;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSoft);
            _tmpIsSoft = _tmp != 0;
            final boolean _tmpIsPair;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPair);
            _tmpIsPair = _tmp_1 != 0;
            final int _tmpRunningCountAtDecision;
            _tmpRunningCountAtDecision = _cursor.getInt(_cursorIndexOfRunningCountAtDecision);
            final double _tmpTrueCountAtDecision;
            _tmpTrueCountAtDecision = _cursor.getDouble(_cursorIndexOfTrueCountAtDecision);
            final HandAction _tmpActionTaken;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfActionTaken)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfActionTaken);
            }
            final HandAction _tmp_3 = __converters.toHandAction(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.geoffrey.blackjackpractice.domain.model.HandAction', but it was NULL.");
            } else {
              _tmpActionTaken = _tmp_3;
            }
            final HandAction _tmpOptimalBasicAction;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfOptimalBasicAction)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfOptimalBasicAction);
            }
            final HandAction _tmp_5 = __converters.toHandAction(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.geoffrey.blackjackpractice.domain.model.HandAction', but it was NULL.");
            } else {
              _tmpOptimalBasicAction = _tmp_5;
            }
            final HandAction _tmpOptimalFinalAction;
            final String _tmp_6;
            if (_cursor.isNull(_cursorIndexOfOptimalFinalAction)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getString(_cursorIndexOfOptimalFinalAction);
            }
            final HandAction _tmp_7 = __converters.toHandAction(_tmp_6);
            if (_tmp_7 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.geoffrey.blackjackpractice.domain.model.HandAction', but it was NULL.");
            } else {
              _tmpOptimalFinalAction = _tmp_7;
            }
            final DeviationClassification _tmpDeviationClassification;
            final String _tmp_8;
            _tmp_8 = _cursor.getString(_cursorIndexOfDeviationClassification);
            _tmpDeviationClassification = __converters.toDeviationClassification(_tmp_8);
            final double _tmpBetAmount;
            _tmpBetAmount = _cursor.getDouble(_cursorIndexOfBetAmount);
            final HandOutcome _tmpOutcome;
            final String _tmp_9;
            if (_cursor.isNull(_cursorIndexOfOutcome)) {
              _tmp_9 = null;
            } else {
              _tmp_9 = _cursor.getString(_cursorIndexOfOutcome);
            }
            _tmpOutcome = __converters.toHandOutcome(_tmp_9);
            final Double _tmpPayout;
            if (_cursor.isNull(_cursorIndexOfPayout)) {
              _tmpPayout = null;
            } else {
              _tmpPayout = _cursor.getDouble(_cursorIndexOfPayout);
            }
            final Double _tmpBankrollAfter;
            if (_cursor.isNull(_cursorIndexOfBankrollAfter)) {
              _tmpBankrollAfter = null;
            } else {
              _tmpBankrollAfter = _cursor.getDouble(_cursorIndexOfBankrollAfter);
            }
            _item = new HandLogEntity(_tmpId,_tmpSessionId,_tmpHandIndexInSession,_tmpTimestamp,_tmpPlayerCards,_tmpDealerUpcard,_tmpPlayerHardTotal,_tmpIsSoft,_tmpIsPair,_tmpRunningCountAtDecision,_tmpTrueCountAtDecision,_tmpActionTaken,_tmpOptimalBasicAction,_tmpOptimalFinalAction,_tmpDeviationClassification,_tmpBetAmount,_tmpOutcome,_tmpPayout,_tmpBankrollAfter);
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
  public Flow<List<OutcomeSummary>> observeOutcomeSummary() {
    final String _sql = "\n"
            + "        SELECT outcome AS outcome, COUNT(*) AS count, SUM(payout) AS totalPayout\n"
            + "        FROM hand_logs\n"
            + "        WHERE outcome IS NOT NULL\n"
            + "        GROUP BY outcome\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"hand_logs"}, new Callable<List<OutcomeSummary>>() {
      @Override
      @NonNull
      public List<OutcomeSummary> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfOutcome = 0;
          final int _cursorIndexOfCount = 1;
          final int _cursorIndexOfTotalPayout = 2;
          final List<OutcomeSummary> _result = new ArrayList<OutcomeSummary>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OutcomeSummary _item;
            final HandOutcome _tmpOutcome;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfOutcome)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfOutcome);
            }
            final HandOutcome _tmp_1 = __converters.toHandOutcome(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.geoffrey.blackjackpractice.domain.model.HandOutcome', but it was NULL.");
            } else {
              _tmpOutcome = _tmp_1;
            }
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            final Double _tmpTotalPayout;
            if (_cursor.isNull(_cursorIndexOfTotalPayout)) {
              _tmpTotalPayout = null;
            } else {
              _tmpTotalPayout = _cursor.getDouble(_cursorIndexOfTotalPayout);
            }
            _item = new OutcomeSummary(_tmpOutcome,_tmpCount,_tmpTotalPayout);
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
  public Flow<List<AdherenceSummary>> observeAdherenceSummary() {
    final String _sql = "\n"
            + "        SELECT deviationClassification AS deviationClassification, COUNT(*) AS count\n"
            + "        FROM hand_logs\n"
            + "        GROUP BY deviationClassification\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"hand_logs"}, new Callable<List<AdherenceSummary>>() {
      @Override
      @NonNull
      public List<AdherenceSummary> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDeviationClassification = 0;
          final int _cursorIndexOfCount = 1;
          final List<AdherenceSummary> _result = new ArrayList<AdherenceSummary>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AdherenceSummary _item;
            final DeviationClassification _tmpDeviationClassification;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDeviationClassification);
            _tmpDeviationClassification = __converters.toDeviationClassification(_tmp);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item = new AdherenceSummary(_tmpDeviationClassification,_tmpCount);
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
  public Flow<List<BankrollPoint>> observeBankrollPoints() {
    final String _sql = "\n"
            + "        SELECT id AS handLogId, timestamp AS timestamp, bankrollAfter AS bankrollAfter\n"
            + "        FROM hand_logs\n"
            + "        WHERE bankrollAfter IS NOT NULL\n"
            + "        ORDER BY timestamp ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"hand_logs"}, new Callable<List<BankrollPoint>>() {
      @Override
      @NonNull
      public List<BankrollPoint> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfHandLogId = 0;
          final int _cursorIndexOfTimestamp = 1;
          final int _cursorIndexOfBankrollAfter = 2;
          final List<BankrollPoint> _result = new ArrayList<BankrollPoint>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BankrollPoint _item;
            final long _tmpHandLogId;
            _tmpHandLogId = _cursor.getLong(_cursorIndexOfHandLogId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final double _tmpBankrollAfter;
            _tmpBankrollAfter = _cursor.getDouble(_cursorIndexOfBankrollAfter);
            _item = new BankrollPoint(_tmpHandLogId,_tmpTimestamp,_tmpBankrollAfter);
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
