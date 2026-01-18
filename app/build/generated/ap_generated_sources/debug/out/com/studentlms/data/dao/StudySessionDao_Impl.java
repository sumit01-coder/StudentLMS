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
import com.studentlms.data.models.StudySession;
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
public final class StudySessionDao_Impl implements StudySessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<StudySession> __insertionAdapterOfStudySession;

  private final EntityDeletionOrUpdateAdapter<StudySession> __deletionAdapterOfStudySession;

  private final EntityDeletionOrUpdateAdapter<StudySession> __updateAdapterOfStudySession;

  public StudySessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStudySession = new EntityInsertionAdapter<StudySession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `study_sessions` (`id`,`subjectId`,`startTime`,`endTime`,`notes`,`completed`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final StudySession entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSubjectId());
        statement.bindLong(3, entity.getStartTime());
        statement.bindLong(4, entity.getEndTime());
        if (entity.getNotes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getNotes());
        }
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__deletionAdapterOfStudySession = new EntityDeletionOrUpdateAdapter<StudySession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `study_sessions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final StudySession entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfStudySession = new EntityDeletionOrUpdateAdapter<StudySession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `study_sessions` SET `id` = ?,`subjectId` = ?,`startTime` = ?,`endTime` = ?,`notes` = ?,`completed` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final StudySession entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSubjectId());
        statement.bindLong(3, entity.getStartTime());
        statement.bindLong(4, entity.getEndTime());
        if (entity.getNotes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getNotes());
        }
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public long insert(final StudySession studySession) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfStudySession.insertAndReturnId(studySession);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final StudySession studySession) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfStudySession.handle(studySession);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final StudySession studySession) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfStudySession.handle(studySession);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<StudySession>> getAllSessions() {
    final String _sql = "SELECT * FROM study_sessions ORDER BY startTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"study_sessions"}, false, new Callable<List<StudySession>>() {
      @Override
      @Nullable
      public List<StudySession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSubjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "subjectId");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final List<StudySession> _result = new ArrayList<StudySession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StudySession _item;
            final int _tmpSubjectId;
            _tmpSubjectId = _cursor.getInt(_cursorIndexOfSubjectId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            _item = new StudySession(_tmpSubjectId,_tmpStartTime,_tmpEndTime,_tmpNotes,_tmpCompleted);
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
  public LiveData<List<StudySession>> getSessionsBySubject(final int subjectId) {
    final String _sql = "SELECT * FROM study_sessions WHERE subjectId = ? ORDER BY startTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, subjectId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"study_sessions"}, false, new Callable<List<StudySession>>() {
      @Override
      @Nullable
      public List<StudySession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSubjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "subjectId");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final List<StudySession> _result = new ArrayList<StudySession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StudySession _item;
            final int _tmpSubjectId;
            _tmpSubjectId = _cursor.getInt(_cursorIndexOfSubjectId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            _item = new StudySession(_tmpSubjectId,_tmpStartTime,_tmpEndTime,_tmpNotes,_tmpCompleted);
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
  public LiveData<List<StudySession>> getUpcomingSessions(final long currentTime) {
    final String _sql = "SELECT * FROM study_sessions WHERE startTime > ? ORDER BY startTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, currentTime);
    return __db.getInvalidationTracker().createLiveData(new String[] {"study_sessions"}, false, new Callable<List<StudySession>>() {
      @Override
      @Nullable
      public List<StudySession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSubjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "subjectId");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final List<StudySession> _result = new ArrayList<StudySession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StudySession _item;
            final int _tmpSubjectId;
            _tmpSubjectId = _cursor.getInt(_cursorIndexOfSubjectId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            _item = new StudySession(_tmpSubjectId,_tmpStartTime,_tmpEndTime,_tmpNotes,_tmpCompleted);
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
  public LiveData<List<StudySession>> getCompletedSessions() {
    final String _sql = "SELECT * FROM study_sessions WHERE completed = 1 ORDER BY startTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"study_sessions"}, false, new Callable<List<StudySession>>() {
      @Override
      @Nullable
      public List<StudySession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSubjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "subjectId");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final List<StudySession> _result = new ArrayList<StudySession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StudySession _item;
            final int _tmpSubjectId;
            _tmpSubjectId = _cursor.getInt(_cursorIndexOfSubjectId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            _item = new StudySession(_tmpSubjectId,_tmpStartTime,_tmpEndTime,_tmpNotes,_tmpCompleted);
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
  public LiveData<List<StudySession>> getSessionsInRange(final long startTime, final long endTime) {
    final String _sql = "SELECT * FROM study_sessions WHERE startTime BETWEEN ? AND ? ORDER BY startTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    return __db.getInvalidationTracker().createLiveData(new String[] {"study_sessions"}, false, new Callable<List<StudySession>>() {
      @Override
      @Nullable
      public List<StudySession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSubjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "subjectId");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final List<StudySession> _result = new ArrayList<StudySession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StudySession _item;
            final int _tmpSubjectId;
            _tmpSubjectId = _cursor.getInt(_cursorIndexOfSubjectId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            _item = new StudySession(_tmpSubjectId,_tmpStartTime,_tmpEndTime,_tmpNotes,_tmpCompleted);
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
