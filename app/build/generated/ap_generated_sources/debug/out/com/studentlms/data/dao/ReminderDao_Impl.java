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
import com.studentlms.data.models.Reminder;
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
public final class ReminderDao_Impl implements ReminderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Reminder> __insertionAdapterOfReminder;

  private final EntityDeletionOrUpdateAdapter<Reminder> __deletionAdapterOfReminder;

  private final EntityDeletionOrUpdateAdapter<Reminder> __updateAdapterOfReminder;

  public ReminderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReminder = new EntityInsertionAdapter<Reminder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `reminders` (`id`,`title`,`description`,`dateTime`,`type`,`isRecurring`,`relatedId`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Reminder entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDescription());
        }
        statement.bindLong(4, entity.getDateTime());
        if (entity.getType() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getType());
        }
        final int _tmp = entity.isRecurring() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getRelatedId());
      }
    };
    this.__deletionAdapterOfReminder = new EntityDeletionOrUpdateAdapter<Reminder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `reminders` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Reminder entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfReminder = new EntityDeletionOrUpdateAdapter<Reminder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `reminders` SET `id` = ?,`title` = ?,`description` = ?,`dateTime` = ?,`type` = ?,`isRecurring` = ?,`relatedId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Reminder entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDescription());
        }
        statement.bindLong(4, entity.getDateTime());
        if (entity.getType() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getType());
        }
        final int _tmp = entity.isRecurring() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getRelatedId());
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public long insert(final Reminder reminder) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfReminder.insertAndReturnId(reminder);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Reminder reminder) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfReminder.handle(reminder);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Reminder reminder) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfReminder.handle(reminder);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<Reminder>> getUpcomingReminders(final long currentTime) {
    final String _sql = "SELECT * FROM reminders WHERE dateTime > ? ORDER BY dateTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, currentTime);
    return __db.getInvalidationTracker().createLiveData(new String[] {"reminders"}, false, new Callable<List<Reminder>>() {
      @Override
      @Nullable
      public List<Reminder> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTime");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "isRecurring");
          final int _cursorIndexOfRelatedId = CursorUtil.getColumnIndexOrThrow(_cursor, "relatedId");
          final List<Reminder> _result = new ArrayList<Reminder>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reminder _item;
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final long _tmpDateTime;
            _tmpDateTime = _cursor.getLong(_cursorIndexOfDateTime);
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final boolean _tmpIsRecurring;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp != 0;
            final int _tmpRelatedId;
            _tmpRelatedId = _cursor.getInt(_cursorIndexOfRelatedId);
            _item = new Reminder(_tmpTitle,_tmpDescription,_tmpDateTime,_tmpType,_tmpIsRecurring,_tmpRelatedId);
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
  public LiveData<List<Reminder>> getAllReminders() {
    final String _sql = "SELECT * FROM reminders ORDER BY dateTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"reminders"}, false, new Callable<List<Reminder>>() {
      @Override
      @Nullable
      public List<Reminder> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTime");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "isRecurring");
          final int _cursorIndexOfRelatedId = CursorUtil.getColumnIndexOrThrow(_cursor, "relatedId");
          final List<Reminder> _result = new ArrayList<Reminder>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reminder _item;
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final long _tmpDateTime;
            _tmpDateTime = _cursor.getLong(_cursorIndexOfDateTime);
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final boolean _tmpIsRecurring;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp != 0;
            final int _tmpRelatedId;
            _tmpRelatedId = _cursor.getInt(_cursorIndexOfRelatedId);
            _item = new Reminder(_tmpTitle,_tmpDescription,_tmpDateTime,_tmpType,_tmpIsRecurring,_tmpRelatedId);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
