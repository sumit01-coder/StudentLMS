package com.studentlms.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.studentlms.data.models.LMSAccount;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class LMSAccountDao_Impl implements LMSAccountDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LMSAccount> __insertionAdapterOfLMSAccount;

  private final EntityDeletionOrUpdateAdapter<LMSAccount> __deletionAdapterOfLMSAccount;

  private final EntityDeletionOrUpdateAdapter<LMSAccount> __updateAdapterOfLMSAccount;

  public LMSAccountDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLMSAccount = new EntityInsertionAdapter<LMSAccount>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `lms_accounts` (`id`,`lmsType`,`email`,`accessToken`,`refreshToken`,`lastSyncTime`,`isActive`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final LMSAccount entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getLmsType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getLmsType());
        }
        if (entity.getEmail() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getEmail());
        }
        if (entity.getAccessToken() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getAccessToken());
        }
        if (entity.getRefreshToken() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getRefreshToken());
        }
        statement.bindLong(6, entity.getLastSyncTime());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
    this.__deletionAdapterOfLMSAccount = new EntityDeletionOrUpdateAdapter<LMSAccount>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `lms_accounts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final LMSAccount entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfLMSAccount = new EntityDeletionOrUpdateAdapter<LMSAccount>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `lms_accounts` SET `id` = ?,`lmsType` = ?,`email` = ?,`accessToken` = ?,`refreshToken` = ?,`lastSyncTime` = ?,`isActive` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final LMSAccount entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getLmsType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getLmsType());
        }
        if (entity.getEmail() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getEmail());
        }
        if (entity.getAccessToken() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getAccessToken());
        }
        if (entity.getRefreshToken() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getRefreshToken());
        }
        statement.bindLong(6, entity.getLastSyncTime());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public long insert(final LMSAccount account) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfLMSAccount.insertAndReturnId(account);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final LMSAccount account) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfLMSAccount.handle(account);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final LMSAccount account) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfLMSAccount.handle(account);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<LMSAccount>> getActiveAccounts() {
    final String _sql = "SELECT * FROM lms_accounts WHERE isActive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"lms_accounts"}, false, new Callable<List<LMSAccount>>() {
      @Override
      @Nullable
      public List<LMSAccount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLmsType = CursorUtil.getColumnIndexOrThrow(_cursor, "lmsType");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfAccessToken = CursorUtil.getColumnIndexOrThrow(_cursor, "accessToken");
          final int _cursorIndexOfRefreshToken = CursorUtil.getColumnIndexOrThrow(_cursor, "refreshToken");
          final int _cursorIndexOfLastSyncTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTime");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<LMSAccount> _result = new ArrayList<LMSAccount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LMSAccount _item;
            final String _tmpLmsType;
            if (_cursor.isNull(_cursorIndexOfLmsType)) {
              _tmpLmsType = null;
            } else {
              _tmpLmsType = _cursor.getString(_cursorIndexOfLmsType);
            }
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpAccessToken;
            if (_cursor.isNull(_cursorIndexOfAccessToken)) {
              _tmpAccessToken = null;
            } else {
              _tmpAccessToken = _cursor.getString(_cursorIndexOfAccessToken);
            }
            final String _tmpRefreshToken;
            if (_cursor.isNull(_cursorIndexOfRefreshToken)) {
              _tmpRefreshToken = null;
            } else {
              _tmpRefreshToken = _cursor.getString(_cursorIndexOfRefreshToken);
            }
            final long _tmpLastSyncTime;
            _tmpLastSyncTime = _cursor.getLong(_cursorIndexOfLastSyncTime);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _item = new LMSAccount(_tmpLmsType,_tmpEmail,_tmpAccessToken,_tmpRefreshToken,_tmpLastSyncTime,_tmpIsActive);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
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
  public LiveData<List<LMSAccount>> getAllAccounts() {
    final String _sql = "SELECT * FROM lms_accounts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"lms_accounts"}, false, new Callable<List<LMSAccount>>() {
      @Override
      @Nullable
      public List<LMSAccount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLmsType = CursorUtil.getColumnIndexOrThrow(_cursor, "lmsType");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfAccessToken = CursorUtil.getColumnIndexOrThrow(_cursor, "accessToken");
          final int _cursorIndexOfRefreshToken = CursorUtil.getColumnIndexOrThrow(_cursor, "refreshToken");
          final int _cursorIndexOfLastSyncTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTime");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<LMSAccount> _result = new ArrayList<LMSAccount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LMSAccount _item;
            final String _tmpLmsType;
            if (_cursor.isNull(_cursorIndexOfLmsType)) {
              _tmpLmsType = null;
            } else {
              _tmpLmsType = _cursor.getString(_cursorIndexOfLmsType);
            }
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpAccessToken;
            if (_cursor.isNull(_cursorIndexOfAccessToken)) {
              _tmpAccessToken = null;
            } else {
              _tmpAccessToken = _cursor.getString(_cursorIndexOfAccessToken);
            }
            final String _tmpRefreshToken;
            if (_cursor.isNull(_cursorIndexOfRefreshToken)) {
              _tmpRefreshToken = null;
            } else {
              _tmpRefreshToken = _cursor.getString(_cursorIndexOfRefreshToken);
            }
            final long _tmpLastSyncTime;
            _tmpLastSyncTime = _cursor.getLong(_cursorIndexOfLastSyncTime);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _item = new LMSAccount(_tmpLmsType,_tmpEmail,_tmpAccessToken,_tmpRefreshToken,_tmpLastSyncTime,_tmpIsActive);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
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
  public LMSAccount getActiveAccountByType(final String lmsType) {
    final String _sql = "SELECT * FROM lms_accounts WHERE lmsType = ? AND isActive = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (lmsType == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, lmsType);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfLmsType = CursorUtil.getColumnIndexOrThrow(_cursor, "lmsType");
      final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
      final int _cursorIndexOfAccessToken = CursorUtil.getColumnIndexOrThrow(_cursor, "accessToken");
      final int _cursorIndexOfRefreshToken = CursorUtil.getColumnIndexOrThrow(_cursor, "refreshToken");
      final int _cursorIndexOfLastSyncTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTime");
      final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
      final LMSAccount _result;
      if (_cursor.moveToFirst()) {
        final String _tmpLmsType;
        if (_cursor.isNull(_cursorIndexOfLmsType)) {
          _tmpLmsType = null;
        } else {
          _tmpLmsType = _cursor.getString(_cursorIndexOfLmsType);
        }
        final String _tmpEmail;
        if (_cursor.isNull(_cursorIndexOfEmail)) {
          _tmpEmail = null;
        } else {
          _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
        }
        final String _tmpAccessToken;
        if (_cursor.isNull(_cursorIndexOfAccessToken)) {
          _tmpAccessToken = null;
        } else {
          _tmpAccessToken = _cursor.getString(_cursorIndexOfAccessToken);
        }
        final String _tmpRefreshToken;
        if (_cursor.isNull(_cursorIndexOfRefreshToken)) {
          _tmpRefreshToken = null;
        } else {
          _tmpRefreshToken = _cursor.getString(_cursorIndexOfRefreshToken);
        }
        final long _tmpLastSyncTime;
        _tmpLastSyncTime = _cursor.getLong(_cursorIndexOfLastSyncTime);
        final boolean _tmpIsActive;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsActive);
        _tmpIsActive = _tmp != 0;
        _result = new LMSAccount(_tmpLmsType,_tmpEmail,_tmpAccessToken,_tmpRefreshToken,_tmpLastSyncTime,_tmpIsActive);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
