package com.geoffrey.blackjackpractice.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.geoffrey.blackjackpractice.data.local.Converters;
import com.geoffrey.blackjackpractice.data.local.entity.SessionEntity;
import com.geoffrey.blackjackpractice.domain.model.BlackjackPayout;
import com.geoffrey.blackjackpractice.domain.model.PracticeMode;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
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
public final class SessionDao_Impl implements SessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SessionEntity> __insertionAdapterOfSessionEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<SessionEntity> __updateAdapterOfSessionEntity;

  public SessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSessionEntity = new EntityInsertionAdapter<SessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sessions` (`id`,`mode`,`startTimestamp`,`endTimestamp`,`numDecks`,`penetrationPercent`,`dealerHitsSoft17`,`doubleAfterSplit`,`surrenderAllowed`,`blackjackPayout`,`startingBankroll`,`endingBankroll`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SessionEntity entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromPracticeMode(entity.getMode());
        statement.bindString(2, _tmp);
        statement.bindLong(3, entity.getStartTimestamp());
        if (entity.getEndTimestamp() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getEndTimestamp());
        }
        statement.bindLong(5, entity.getNumDecks());
        statement.bindDouble(6, entity.getPenetrationPercent());
        final int _tmp_1 = entity.getDealerHitsSoft17() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        final int _tmp_2 = entity.getDoubleAfterSplit() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
        final int _tmp_3 = entity.getSurrenderAllowed() ? 1 : 0;
        statement.bindLong(9, _tmp_3);
        final String _tmp_4 = __converters.fromBlackjackPayout(entity.getBlackjackPayout());
        statement.bindString(10, _tmp_4);
        statement.bindDouble(11, entity.getStartingBankroll());
        if (entity.getEndingBankroll() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getEndingBankroll());
        }
      }
    };
    this.__updateAdapterOfSessionEntity = new EntityDeletionOrUpdateAdapter<SessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sessions` SET `id` = ?,`mode` = ?,`startTimestamp` = ?,`endTimestamp` = ?,`numDecks` = ?,`penetrationPercent` = ?,`dealerHitsSoft17` = ?,`doubleAfterSplit` = ?,`surrenderAllowed` = ?,`blackjackPayout` = ?,`startingBankroll` = ?,`endingBankroll` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SessionEntity entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromPracticeMode(entity.getMode());
        statement.bindString(2, _tmp);
        statement.bindLong(3, entity.getStartTimestamp());
        if (entity.getEndTimestamp() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getEndTimestamp());
        }
        statement.bindLong(5, entity.getNumDecks());
        statement.bindDouble(6, entity.getPenetrationPercent());
        final int _tmp_1 = entity.getDealerHitsSoft17() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        final int _tmp_2 = entity.getDoubleAfterSplit() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
        final int _tmp_3 = entity.getSurrenderAllowed() ? 1 : 0;
        statement.bindLong(9, _tmp_3);
        final String _tmp_4 = __converters.fromBlackjackPayout(entity.getBlackjackPayout());
        statement.bindString(10, _tmp_4);
        statement.bindDouble(11, entity.getStartingBankroll());
        if (entity.getEndingBankroll() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getEndingBankroll());
        }
        statement.bindLong(13, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final SessionEntity session, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSessionEntity.insertAndReturnId(session);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final SessionEntity session, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSessionEntity.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SessionEntity>> observeAll() {
    final String _sql = "SELECT * FROM sessions ORDER BY startTimestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sessions"}, new Callable<List<SessionEntity>>() {
      @Override
      @NonNull
      public List<SessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfStartTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "startTimestamp");
          final int _cursorIndexOfEndTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "endTimestamp");
          final int _cursorIndexOfNumDecks = CursorUtil.getColumnIndexOrThrow(_cursor, "numDecks");
          final int _cursorIndexOfPenetrationPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "penetrationPercent");
          final int _cursorIndexOfDealerHitsSoft17 = CursorUtil.getColumnIndexOrThrow(_cursor, "dealerHitsSoft17");
          final int _cursorIndexOfDoubleAfterSplit = CursorUtil.getColumnIndexOrThrow(_cursor, "doubleAfterSplit");
          final int _cursorIndexOfSurrenderAllowed = CursorUtil.getColumnIndexOrThrow(_cursor, "surrenderAllowed");
          final int _cursorIndexOfBlackjackPayout = CursorUtil.getColumnIndexOrThrow(_cursor, "blackjackPayout");
          final int _cursorIndexOfStartingBankroll = CursorUtil.getColumnIndexOrThrow(_cursor, "startingBankroll");
          final int _cursorIndexOfEndingBankroll = CursorUtil.getColumnIndexOrThrow(_cursor, "endingBankroll");
          final List<SessionEntity> _result = new ArrayList<SessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SessionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final PracticeMode _tmpMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMode);
            _tmpMode = __converters.toPracticeMode(_tmp);
            final long _tmpStartTimestamp;
            _tmpStartTimestamp = _cursor.getLong(_cursorIndexOfStartTimestamp);
            final Long _tmpEndTimestamp;
            if (_cursor.isNull(_cursorIndexOfEndTimestamp)) {
              _tmpEndTimestamp = null;
            } else {
              _tmpEndTimestamp = _cursor.getLong(_cursorIndexOfEndTimestamp);
            }
            final int _tmpNumDecks;
            _tmpNumDecks = _cursor.getInt(_cursorIndexOfNumDecks);
            final float _tmpPenetrationPercent;
            _tmpPenetrationPercent = _cursor.getFloat(_cursorIndexOfPenetrationPercent);
            final boolean _tmpDealerHitsSoft17;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfDealerHitsSoft17);
            _tmpDealerHitsSoft17 = _tmp_1 != 0;
            final boolean _tmpDoubleAfterSplit;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDoubleAfterSplit);
            _tmpDoubleAfterSplit = _tmp_2 != 0;
            final boolean _tmpSurrenderAllowed;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfSurrenderAllowed);
            _tmpSurrenderAllowed = _tmp_3 != 0;
            final BlackjackPayout _tmpBlackjackPayout;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfBlackjackPayout);
            _tmpBlackjackPayout = __converters.toBlackjackPayout(_tmp_4);
            final double _tmpStartingBankroll;
            _tmpStartingBankroll = _cursor.getDouble(_cursorIndexOfStartingBankroll);
            final Double _tmpEndingBankroll;
            if (_cursor.isNull(_cursorIndexOfEndingBankroll)) {
              _tmpEndingBankroll = null;
            } else {
              _tmpEndingBankroll = _cursor.getDouble(_cursorIndexOfEndingBankroll);
            }
            _item = new SessionEntity(_tmpId,_tmpMode,_tmpStartTimestamp,_tmpEndTimestamp,_tmpNumDecks,_tmpPenetrationPercent,_tmpDealerHitsSoft17,_tmpDoubleAfterSplit,_tmpSurrenderAllowed,_tmpBlackjackPayout,_tmpStartingBankroll,_tmpEndingBankroll);
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
  public Object getById(final long id, final Continuation<? super SessionEntity> $completion) {
    final String _sql = "SELECT * FROM sessions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SessionEntity>() {
      @Override
      @Nullable
      public SessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfStartTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "startTimestamp");
          final int _cursorIndexOfEndTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "endTimestamp");
          final int _cursorIndexOfNumDecks = CursorUtil.getColumnIndexOrThrow(_cursor, "numDecks");
          final int _cursorIndexOfPenetrationPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "penetrationPercent");
          final int _cursorIndexOfDealerHitsSoft17 = CursorUtil.getColumnIndexOrThrow(_cursor, "dealerHitsSoft17");
          final int _cursorIndexOfDoubleAfterSplit = CursorUtil.getColumnIndexOrThrow(_cursor, "doubleAfterSplit");
          final int _cursorIndexOfSurrenderAllowed = CursorUtil.getColumnIndexOrThrow(_cursor, "surrenderAllowed");
          final int _cursorIndexOfBlackjackPayout = CursorUtil.getColumnIndexOrThrow(_cursor, "blackjackPayout");
          final int _cursorIndexOfStartingBankroll = CursorUtil.getColumnIndexOrThrow(_cursor, "startingBankroll");
          final int _cursorIndexOfEndingBankroll = CursorUtil.getColumnIndexOrThrow(_cursor, "endingBankroll");
          final SessionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final PracticeMode _tmpMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMode);
            _tmpMode = __converters.toPracticeMode(_tmp);
            final long _tmpStartTimestamp;
            _tmpStartTimestamp = _cursor.getLong(_cursorIndexOfStartTimestamp);
            final Long _tmpEndTimestamp;
            if (_cursor.isNull(_cursorIndexOfEndTimestamp)) {
              _tmpEndTimestamp = null;
            } else {
              _tmpEndTimestamp = _cursor.getLong(_cursorIndexOfEndTimestamp);
            }
            final int _tmpNumDecks;
            _tmpNumDecks = _cursor.getInt(_cursorIndexOfNumDecks);
            final float _tmpPenetrationPercent;
            _tmpPenetrationPercent = _cursor.getFloat(_cursorIndexOfPenetrationPercent);
            final boolean _tmpDealerHitsSoft17;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfDealerHitsSoft17);
            _tmpDealerHitsSoft17 = _tmp_1 != 0;
            final boolean _tmpDoubleAfterSplit;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDoubleAfterSplit);
            _tmpDoubleAfterSplit = _tmp_2 != 0;
            final boolean _tmpSurrenderAllowed;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfSurrenderAllowed);
            _tmpSurrenderAllowed = _tmp_3 != 0;
            final BlackjackPayout _tmpBlackjackPayout;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfBlackjackPayout);
            _tmpBlackjackPayout = __converters.toBlackjackPayout(_tmp_4);
            final double _tmpStartingBankroll;
            _tmpStartingBankroll = _cursor.getDouble(_cursorIndexOfStartingBankroll);
            final Double _tmpEndingBankroll;
            if (_cursor.isNull(_cursorIndexOfEndingBankroll)) {
              _tmpEndingBankroll = null;
            } else {
              _tmpEndingBankroll = _cursor.getDouble(_cursorIndexOfEndingBankroll);
            }
            _result = new SessionEntity(_tmpId,_tmpMode,_tmpStartTimestamp,_tmpEndTimestamp,_tmpNumDecks,_tmpPenetrationPercent,_tmpDealerHitsSoft17,_tmpDoubleAfterSplit,_tmpSurrenderAllowed,_tmpBlackjackPayout,_tmpStartingBankroll,_tmpEndingBankroll);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
