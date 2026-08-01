package com.geoffrey.blackjackpractice.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.geoffrey.blackjackpractice.data.local.dao.CountGuessDao;
import com.geoffrey.blackjackpractice.data.local.dao.CountGuessDao_Impl;
import com.geoffrey.blackjackpractice.data.local.dao.HandLogDao;
import com.geoffrey.blackjackpractice.data.local.dao.HandLogDao_Impl;
import com.geoffrey.blackjackpractice.data.local.dao.SessionDao;
import com.geoffrey.blackjackpractice.data.local.dao.SessionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile SessionDao _sessionDao;

  private volatile HandLogDao _handLogDao;

  private volatile CountGuessDao _countGuessDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `mode` TEXT NOT NULL, `startTimestamp` INTEGER NOT NULL, `endTimestamp` INTEGER, `numDecks` INTEGER NOT NULL, `penetrationPercent` REAL NOT NULL, `dealerHitsSoft17` INTEGER NOT NULL, `doubleAfterSplit` INTEGER NOT NULL, `surrenderAllowed` INTEGER NOT NULL, `blackjackPayout` TEXT NOT NULL, `startingBankroll` REAL NOT NULL, `endingBankroll` REAL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `hand_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` INTEGER NOT NULL, `handIndexInSession` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `playerCards` TEXT NOT NULL, `dealerUpcard` TEXT NOT NULL, `playerHardTotal` INTEGER NOT NULL, `isSoft` INTEGER NOT NULL, `isPair` INTEGER NOT NULL, `runningCountAtDecision` INTEGER NOT NULL, `trueCountAtDecision` REAL NOT NULL, `actionTaken` TEXT NOT NULL, `optimalBasicAction` TEXT NOT NULL, `optimalFinalAction` TEXT NOT NULL, `deviationClassification` TEXT NOT NULL, `betAmount` REAL NOT NULL, `outcome` TEXT, `payout` REAL, `bankrollAfter` REAL, FOREIGN KEY(`sessionId`) REFERENCES `sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_hand_logs_sessionId` ON `hand_logs` (`sessionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `count_guesses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` INTEGER NOT NULL, `mode` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `sequenceNumber` INTEGER NOT NULL, `actualRunningCount` INTEGER NOT NULL, `actualTrueCount` REAL NOT NULL, `guessedRunningCount` INTEGER NOT NULL, `guessedTrueCount` REAL, `elapsedMs` INTEGER, `cardsShownCount` INTEGER, FOREIGN KEY(`sessionId`) REFERENCES `sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_count_guesses_sessionId` ON `count_guesses` (`sessionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c2343a7cd4df8f6854062d5135778123')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `sessions`");
        db.execSQL("DROP TABLE IF EXISTS `hand_logs`");
        db.execSQL("DROP TABLE IF EXISTS `count_guesses`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsSessions = new HashMap<String, TableInfo.Column>(12);
        _columnsSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("mode", new TableInfo.Column("mode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("startTimestamp", new TableInfo.Column("startTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("endTimestamp", new TableInfo.Column("endTimestamp", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("numDecks", new TableInfo.Column("numDecks", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("penetrationPercent", new TableInfo.Column("penetrationPercent", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("dealerHitsSoft17", new TableInfo.Column("dealerHitsSoft17", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("doubleAfterSplit", new TableInfo.Column("doubleAfterSplit", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("surrenderAllowed", new TableInfo.Column("surrenderAllowed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("blackjackPayout", new TableInfo.Column("blackjackPayout", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("startingBankroll", new TableInfo.Column("startingBankroll", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("endingBankroll", new TableInfo.Column("endingBankroll", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSessions = new TableInfo("sessions", _columnsSessions, _foreignKeysSessions, _indicesSessions);
        final TableInfo _existingSessions = TableInfo.read(db, "sessions");
        if (!_infoSessions.equals(_existingSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "sessions(com.geoffrey.blackjackpractice.data.local.entity.SessionEntity).\n"
                  + " Expected:\n" + _infoSessions + "\n"
                  + " Found:\n" + _existingSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsHandLogs = new HashMap<String, TableInfo.Column>(19);
        _columnsHandLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("sessionId", new TableInfo.Column("sessionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("handIndexInSession", new TableInfo.Column("handIndexInSession", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("playerCards", new TableInfo.Column("playerCards", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("dealerUpcard", new TableInfo.Column("dealerUpcard", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("playerHardTotal", new TableInfo.Column("playerHardTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("isSoft", new TableInfo.Column("isSoft", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("isPair", new TableInfo.Column("isPair", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("runningCountAtDecision", new TableInfo.Column("runningCountAtDecision", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("trueCountAtDecision", new TableInfo.Column("trueCountAtDecision", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("actionTaken", new TableInfo.Column("actionTaken", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("optimalBasicAction", new TableInfo.Column("optimalBasicAction", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("optimalFinalAction", new TableInfo.Column("optimalFinalAction", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("deviationClassification", new TableInfo.Column("deviationClassification", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("betAmount", new TableInfo.Column("betAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("outcome", new TableInfo.Column("outcome", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("payout", new TableInfo.Column("payout", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandLogs.put("bankrollAfter", new TableInfo.Column("bankrollAfter", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHandLogs = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysHandLogs.add(new TableInfo.ForeignKey("sessions", "CASCADE", "NO ACTION", Arrays.asList("sessionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesHandLogs = new HashSet<TableInfo.Index>(1);
        _indicesHandLogs.add(new TableInfo.Index("index_hand_logs_sessionId", false, Arrays.asList("sessionId"), Arrays.asList("ASC")));
        final TableInfo _infoHandLogs = new TableInfo("hand_logs", _columnsHandLogs, _foreignKeysHandLogs, _indicesHandLogs);
        final TableInfo _existingHandLogs = TableInfo.read(db, "hand_logs");
        if (!_infoHandLogs.equals(_existingHandLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "hand_logs(com.geoffrey.blackjackpractice.data.local.entity.HandLogEntity).\n"
                  + " Expected:\n" + _infoHandLogs + "\n"
                  + " Found:\n" + _existingHandLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsCountGuesses = new HashMap<String, TableInfo.Column>(11);
        _columnsCountGuesses.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCountGuesses.put("sessionId", new TableInfo.Column("sessionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCountGuesses.put("mode", new TableInfo.Column("mode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCountGuesses.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCountGuesses.put("sequenceNumber", new TableInfo.Column("sequenceNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCountGuesses.put("actualRunningCount", new TableInfo.Column("actualRunningCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCountGuesses.put("actualTrueCount", new TableInfo.Column("actualTrueCount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCountGuesses.put("guessedRunningCount", new TableInfo.Column("guessedRunningCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCountGuesses.put("guessedTrueCount", new TableInfo.Column("guessedTrueCount", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCountGuesses.put("elapsedMs", new TableInfo.Column("elapsedMs", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCountGuesses.put("cardsShownCount", new TableInfo.Column("cardsShownCount", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCountGuesses = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysCountGuesses.add(new TableInfo.ForeignKey("sessions", "CASCADE", "NO ACTION", Arrays.asList("sessionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesCountGuesses = new HashSet<TableInfo.Index>(1);
        _indicesCountGuesses.add(new TableInfo.Index("index_count_guesses_sessionId", false, Arrays.asList("sessionId"), Arrays.asList("ASC")));
        final TableInfo _infoCountGuesses = new TableInfo("count_guesses", _columnsCountGuesses, _foreignKeysCountGuesses, _indicesCountGuesses);
        final TableInfo _existingCountGuesses = TableInfo.read(db, "count_guesses");
        if (!_infoCountGuesses.equals(_existingCountGuesses)) {
          return new RoomOpenHelper.ValidationResult(false, "count_guesses(com.geoffrey.blackjackpractice.data.local.entity.CountGuessEntity).\n"
                  + " Expected:\n" + _infoCountGuesses + "\n"
                  + " Found:\n" + _existingCountGuesses);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "c2343a7cd4df8f6854062d5135778123", "eab4cc9d1c82a6f85c3da579ad7e6612");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "sessions","hand_logs","count_guesses");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `sessions`");
      _db.execSQL("DELETE FROM `hand_logs`");
      _db.execSQL("DELETE FROM `count_guesses`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(SessionDao.class, SessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HandLogDao.class, HandLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CountGuessDao.class, CountGuessDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public SessionDao sessionDao() {
    if (_sessionDao != null) {
      return _sessionDao;
    } else {
      synchronized(this) {
        if(_sessionDao == null) {
          _sessionDao = new SessionDao_Impl(this);
        }
        return _sessionDao;
      }
    }
  }

  @Override
  public HandLogDao handLogDao() {
    if (_handLogDao != null) {
      return _handLogDao;
    } else {
      synchronized(this) {
        if(_handLogDao == null) {
          _handLogDao = new HandLogDao_Impl(this);
        }
        return _handLogDao;
      }
    }
  }

  @Override
  public CountGuessDao countGuessDao() {
    if (_countGuessDao != null) {
      return _countGuessDao;
    } else {
      synchronized(this) {
        if(_countGuessDao == null) {
          _countGuessDao = new CountGuessDao_Impl(this);
        }
        return _countGuessDao;
      }
    }
  }
}
