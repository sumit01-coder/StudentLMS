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
import com.studentlms.data.models.Subject;
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
public final class SubjectDao_Impl implements SubjectDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Subject> __insertionAdapterOfSubject;

  private final EntityDeletionOrUpdateAdapter<Subject> __deletionAdapterOfSubject;

  private final EntityDeletionOrUpdateAdapter<Subject> __updateAdapterOfSubject;

  public SubjectDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSubject = new EntityInsertionAdapter<Subject>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `subjects` (`id`,`name`,`color`,`priority`,`totalHours`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Subject entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getColor() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getColor());
        }
        statement.bindLong(4, entity.getPriority());
        statement.bindLong(5, entity.getTotalHours());
      }
    };
    this.__deletionAdapterOfSubject = new EntityDeletionOrUpdateAdapter<Subject>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `subjects` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Subject entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfSubject = new EntityDeletionOrUpdateAdapter<Subject>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `subjects` SET `id` = ?,`name` = ?,`color` = ?,`priority` = ?,`totalHours` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Subject entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getColor() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getColor());
        }
        statement.bindLong(4, entity.getPriority());
        statement.bindLong(5, entity.getTotalHours());
        statement.bindLong(6, entity.getId());
      }
    };
  }

  @Override
  public long insert(final Subject subject) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfSubject.insertAndReturnId(subject);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Subject subject) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfSubject.handle(subject);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Subject subject) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfSubject.handle(subject);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<Subject>> getAllSubjects() {
    final String _sql = "SELECT * FROM subjects ORDER BY priority DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"subjects"}, false, new Callable<List<Subject>>() {
      @Override
      @Nullable
      public List<Subject> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfTotalHours = CursorUtil.getColumnIndexOrThrow(_cursor, "totalHours");
          final List<Subject> _result = new ArrayList<Subject>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Subject _item;
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpColor;
            if (_cursor.isNull(_cursorIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = _cursor.getString(_cursorIndexOfColor);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final int _tmpTotalHours;
            _tmpTotalHours = _cursor.getInt(_cursorIndexOfTotalHours);
            _item = new Subject(_tmpName,_tmpColor,_tmpPriority,_tmpTotalHours);
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
  public LiveData<Subject> getSubjectById(final int id) {
    final String _sql = "SELECT * FROM subjects WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return __db.getInvalidationTracker().createLiveData(new String[] {"subjects"}, false, new Callable<Subject>() {
      @Override
      @Nullable
      public Subject call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfTotalHours = CursorUtil.getColumnIndexOrThrow(_cursor, "totalHours");
          final Subject _result;
          if (_cursor.moveToFirst()) {
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpColor;
            if (_cursor.isNull(_cursorIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = _cursor.getString(_cursorIndexOfColor);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final int _tmpTotalHours;
            _tmpTotalHours = _cursor.getInt(_cursorIndexOfTotalHours);
            _result = new Subject(_tmpName,_tmpColor,_tmpPriority,_tmpTotalHours);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _result.setId(_tmpId);
          } else {
            _result = null;
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
