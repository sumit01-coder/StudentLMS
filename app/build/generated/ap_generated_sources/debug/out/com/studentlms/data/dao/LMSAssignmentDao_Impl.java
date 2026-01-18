package com.studentlms.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.studentlms.data.models.LMSAssignment;
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
public final class LMSAssignmentDao_Impl implements LMSAssignmentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LMSAssignment> __insertionAdapterOfLMSAssignment;

  private final EntityDeletionOrUpdateAdapter<LMSAssignment> __deletionAdapterOfLMSAssignment;

  private final EntityDeletionOrUpdateAdapter<LMSAssignment> __updateAdapterOfLMSAssignment;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByLMSType;

  public LMSAssignmentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLMSAssignment = new EntityInsertionAdapter<LMSAssignment>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `lms_assignments` (`id`,`lmsType`,`lmsId`,`courseId`,`courseName`,`title`,`description`,`dueDate`,`submissionUrl`,`isSubmitted`,`syncedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final LMSAssignment entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getLmsType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getLmsType());
        }
        if (entity.getLmsId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getLmsId());
        }
        if (entity.getCourseId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCourseId());
        }
        if (entity.getCourseName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCourseName());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getTitle());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDescription());
        }
        statement.bindLong(8, entity.getDueDate());
        if (entity.getSubmissionUrl() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getSubmissionUrl());
        }
        final int _tmp = entity.isSubmitted() ? 1 : 0;
        statement.bindLong(10, _tmp);
        statement.bindLong(11, entity.getSyncedAt());
      }
    };
    this.__deletionAdapterOfLMSAssignment = new EntityDeletionOrUpdateAdapter<LMSAssignment>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `lms_assignments` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final LMSAssignment entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfLMSAssignment = new EntityDeletionOrUpdateAdapter<LMSAssignment>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `lms_assignments` SET `id` = ?,`lmsType` = ?,`lmsId` = ?,`courseId` = ?,`courseName` = ?,`title` = ?,`description` = ?,`dueDate` = ?,`submissionUrl` = ?,`isSubmitted` = ?,`syncedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final LMSAssignment entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getLmsType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getLmsType());
        }
        if (entity.getLmsId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getLmsId());
        }
        if (entity.getCourseId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCourseId());
        }
        if (entity.getCourseName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCourseName());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getTitle());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDescription());
        }
        statement.bindLong(8, entity.getDueDate());
        if (entity.getSubmissionUrl() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getSubmissionUrl());
        }
        final int _tmp = entity.isSubmitted() ? 1 : 0;
        statement.bindLong(10, _tmp);
        statement.bindLong(11, entity.getSyncedAt());
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteByLMSType = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM lms_assignments WHERE lmsType = ?";
        return _query;
      }
    };
  }

  @Override
  public long insert(final LMSAssignment assignment) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfLMSAssignment.insertAndReturnId(assignment);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final LMSAssignment assignment) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfLMSAssignment.handle(assignment);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final LMSAssignment assignment) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfLMSAssignment.handle(assignment);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteByLMSType(final String lmsType) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByLMSType.acquire();
    int _argIndex = 1;
    if (lmsType == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, lmsType);
    }
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteByLMSType.release(_stmt);
    }
  }

  @Override
  public LiveData<List<LMSAssignment>> getUpcomingAssignments(final long currentTime) {
    final String _sql = "SELECT * FROM lms_assignments WHERE dueDate > ? ORDER BY dueDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, currentTime);
    return __db.getInvalidationTracker().createLiveData(new String[] {"lms_assignments"}, false, new Callable<List<LMSAssignment>>() {
      @Override
      @Nullable
      public List<LMSAssignment> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLmsType = CursorUtil.getColumnIndexOrThrow(_cursor, "lmsType");
          final int _cursorIndexOfLmsId = CursorUtil.getColumnIndexOrThrow(_cursor, "lmsId");
          final int _cursorIndexOfCourseId = CursorUtil.getColumnIndexOrThrow(_cursor, "courseId");
          final int _cursorIndexOfCourseName = CursorUtil.getColumnIndexOrThrow(_cursor, "courseName");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfSubmissionUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "submissionUrl");
          final int _cursorIndexOfIsSubmitted = CursorUtil.getColumnIndexOrThrow(_cursor, "isSubmitted");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final List<LMSAssignment> _result = new ArrayList<LMSAssignment>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LMSAssignment _item;
            final String _tmpLmsType;
            if (_cursor.isNull(_cursorIndexOfLmsType)) {
              _tmpLmsType = null;
            } else {
              _tmpLmsType = _cursor.getString(_cursorIndexOfLmsType);
            }
            final String _tmpLmsId;
            if (_cursor.isNull(_cursorIndexOfLmsId)) {
              _tmpLmsId = null;
            } else {
              _tmpLmsId = _cursor.getString(_cursorIndexOfLmsId);
            }
            final String _tmpCourseId;
            if (_cursor.isNull(_cursorIndexOfCourseId)) {
              _tmpCourseId = null;
            } else {
              _tmpCourseId = _cursor.getString(_cursorIndexOfCourseId);
            }
            final String _tmpCourseName;
            if (_cursor.isNull(_cursorIndexOfCourseName)) {
              _tmpCourseName = null;
            } else {
              _tmpCourseName = _cursor.getString(_cursorIndexOfCourseName);
            }
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
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final String _tmpSubmissionUrl;
            if (_cursor.isNull(_cursorIndexOfSubmissionUrl)) {
              _tmpSubmissionUrl = null;
            } else {
              _tmpSubmissionUrl = _cursor.getString(_cursorIndexOfSubmissionUrl);
            }
            final boolean _tmpIsSubmitted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSubmitted);
            _tmpIsSubmitted = _tmp != 0;
            final long _tmpSyncedAt;
            _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            _item = new LMSAssignment(_tmpLmsType,_tmpLmsId,_tmpCourseId,_tmpCourseName,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpSubmissionUrl,_tmpIsSubmitted,_tmpSyncedAt);
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
  public LiveData<List<LMSAssignment>> getAssignmentsByLMS(final String lmsType) {
    final String _sql = "SELECT * FROM lms_assignments WHERE lmsType = ? ORDER BY dueDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (lmsType == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, lmsType);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"lms_assignments"}, false, new Callable<List<LMSAssignment>>() {
      @Override
      @Nullable
      public List<LMSAssignment> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLmsType = CursorUtil.getColumnIndexOrThrow(_cursor, "lmsType");
          final int _cursorIndexOfLmsId = CursorUtil.getColumnIndexOrThrow(_cursor, "lmsId");
          final int _cursorIndexOfCourseId = CursorUtil.getColumnIndexOrThrow(_cursor, "courseId");
          final int _cursorIndexOfCourseName = CursorUtil.getColumnIndexOrThrow(_cursor, "courseName");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfSubmissionUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "submissionUrl");
          final int _cursorIndexOfIsSubmitted = CursorUtil.getColumnIndexOrThrow(_cursor, "isSubmitted");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final List<LMSAssignment> _result = new ArrayList<LMSAssignment>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LMSAssignment _item;
            final String _tmpLmsType;
            if (_cursor.isNull(_cursorIndexOfLmsType)) {
              _tmpLmsType = null;
            } else {
              _tmpLmsType = _cursor.getString(_cursorIndexOfLmsType);
            }
            final String _tmpLmsId;
            if (_cursor.isNull(_cursorIndexOfLmsId)) {
              _tmpLmsId = null;
            } else {
              _tmpLmsId = _cursor.getString(_cursorIndexOfLmsId);
            }
            final String _tmpCourseId;
            if (_cursor.isNull(_cursorIndexOfCourseId)) {
              _tmpCourseId = null;
            } else {
              _tmpCourseId = _cursor.getString(_cursorIndexOfCourseId);
            }
            final String _tmpCourseName;
            if (_cursor.isNull(_cursorIndexOfCourseName)) {
              _tmpCourseName = null;
            } else {
              _tmpCourseName = _cursor.getString(_cursorIndexOfCourseName);
            }
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
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final String _tmpSubmissionUrl;
            if (_cursor.isNull(_cursorIndexOfSubmissionUrl)) {
              _tmpSubmissionUrl = null;
            } else {
              _tmpSubmissionUrl = _cursor.getString(_cursorIndexOfSubmissionUrl);
            }
            final boolean _tmpIsSubmitted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSubmitted);
            _tmpIsSubmitted = _tmp != 0;
            final long _tmpSyncedAt;
            _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            _item = new LMSAssignment(_tmpLmsType,_tmpLmsId,_tmpCourseId,_tmpCourseName,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpSubmissionUrl,_tmpIsSubmitted,_tmpSyncedAt);
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
  public LMSAssignment getAssignmentByLMSId(final String lmsId, final String lmsType) {
    final String _sql = "SELECT * FROM lms_assignments WHERE lmsId = ? AND lmsType = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (lmsId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, lmsId);
    }
    _argIndex = 2;
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
      final int _cursorIndexOfLmsId = CursorUtil.getColumnIndexOrThrow(_cursor, "lmsId");
      final int _cursorIndexOfCourseId = CursorUtil.getColumnIndexOrThrow(_cursor, "courseId");
      final int _cursorIndexOfCourseName = CursorUtil.getColumnIndexOrThrow(_cursor, "courseName");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
      final int _cursorIndexOfSubmissionUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "submissionUrl");
      final int _cursorIndexOfIsSubmitted = CursorUtil.getColumnIndexOrThrow(_cursor, "isSubmitted");
      final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
      final LMSAssignment _result;
      if (_cursor.moveToFirst()) {
        final String _tmpLmsType;
        if (_cursor.isNull(_cursorIndexOfLmsType)) {
          _tmpLmsType = null;
        } else {
          _tmpLmsType = _cursor.getString(_cursorIndexOfLmsType);
        }
        final String _tmpLmsId;
        if (_cursor.isNull(_cursorIndexOfLmsId)) {
          _tmpLmsId = null;
        } else {
          _tmpLmsId = _cursor.getString(_cursorIndexOfLmsId);
        }
        final String _tmpCourseId;
        if (_cursor.isNull(_cursorIndexOfCourseId)) {
          _tmpCourseId = null;
        } else {
          _tmpCourseId = _cursor.getString(_cursorIndexOfCourseId);
        }
        final String _tmpCourseName;
        if (_cursor.isNull(_cursorIndexOfCourseName)) {
          _tmpCourseName = null;
        } else {
          _tmpCourseName = _cursor.getString(_cursorIndexOfCourseName);
        }
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
        final long _tmpDueDate;
        _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
        final String _tmpSubmissionUrl;
        if (_cursor.isNull(_cursorIndexOfSubmissionUrl)) {
          _tmpSubmissionUrl = null;
        } else {
          _tmpSubmissionUrl = _cursor.getString(_cursorIndexOfSubmissionUrl);
        }
        final boolean _tmpIsSubmitted;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsSubmitted);
        _tmpIsSubmitted = _tmp != 0;
        final long _tmpSyncedAt;
        _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
        _result = new LMSAssignment(_tmpLmsType,_tmpLmsId,_tmpCourseId,_tmpCourseName,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpSubmissionUrl,_tmpIsSubmitted,_tmpSyncedAt);
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

  @Override
  public List<LMSAssignment> getAssignmentsBySubjectSync(final String courseName) {
    final String _sql = "SELECT * FROM lms_assignments WHERE courseName = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (courseName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, courseName);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfLmsType = CursorUtil.getColumnIndexOrThrow(_cursor, "lmsType");
      final int _cursorIndexOfLmsId = CursorUtil.getColumnIndexOrThrow(_cursor, "lmsId");
      final int _cursorIndexOfCourseId = CursorUtil.getColumnIndexOrThrow(_cursor, "courseId");
      final int _cursorIndexOfCourseName = CursorUtil.getColumnIndexOrThrow(_cursor, "courseName");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
      final int _cursorIndexOfSubmissionUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "submissionUrl");
      final int _cursorIndexOfIsSubmitted = CursorUtil.getColumnIndexOrThrow(_cursor, "isSubmitted");
      final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
      final List<LMSAssignment> _result = new ArrayList<LMSAssignment>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final LMSAssignment _item;
        final String _tmpLmsType;
        if (_cursor.isNull(_cursorIndexOfLmsType)) {
          _tmpLmsType = null;
        } else {
          _tmpLmsType = _cursor.getString(_cursorIndexOfLmsType);
        }
        final String _tmpLmsId;
        if (_cursor.isNull(_cursorIndexOfLmsId)) {
          _tmpLmsId = null;
        } else {
          _tmpLmsId = _cursor.getString(_cursorIndexOfLmsId);
        }
        final String _tmpCourseId;
        if (_cursor.isNull(_cursorIndexOfCourseId)) {
          _tmpCourseId = null;
        } else {
          _tmpCourseId = _cursor.getString(_cursorIndexOfCourseId);
        }
        final String _tmpCourseName;
        if (_cursor.isNull(_cursorIndexOfCourseName)) {
          _tmpCourseName = null;
        } else {
          _tmpCourseName = _cursor.getString(_cursorIndexOfCourseName);
        }
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
        final long _tmpDueDate;
        _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
        final String _tmpSubmissionUrl;
        if (_cursor.isNull(_cursorIndexOfSubmissionUrl)) {
          _tmpSubmissionUrl = null;
        } else {
          _tmpSubmissionUrl = _cursor.getString(_cursorIndexOfSubmissionUrl);
        }
        final boolean _tmpIsSubmitted;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsSubmitted);
        _tmpIsSubmitted = _tmp != 0;
        final long _tmpSyncedAt;
        _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
        _item = new LMSAssignment(_tmpLmsType,_tmpLmsId,_tmpCourseId,_tmpCourseName,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpSubmissionUrl,_tmpIsSubmitted,_tmpSyncedAt);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<LMSAssignment> getAssignmentsByDateRange(final long startTime, final long endTime) {
    final String _sql = "SELECT * FROM lms_assignments WHERE dueDate BETWEEN ? AND ? ORDER BY dueDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfLmsType = CursorUtil.getColumnIndexOrThrow(_cursor, "lmsType");
      final int _cursorIndexOfLmsId = CursorUtil.getColumnIndexOrThrow(_cursor, "lmsId");
      final int _cursorIndexOfCourseId = CursorUtil.getColumnIndexOrThrow(_cursor, "courseId");
      final int _cursorIndexOfCourseName = CursorUtil.getColumnIndexOrThrow(_cursor, "courseName");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
      final int _cursorIndexOfSubmissionUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "submissionUrl");
      final int _cursorIndexOfIsSubmitted = CursorUtil.getColumnIndexOrThrow(_cursor, "isSubmitted");
      final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
      final List<LMSAssignment> _result = new ArrayList<LMSAssignment>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final LMSAssignment _item;
        final String _tmpLmsType;
        if (_cursor.isNull(_cursorIndexOfLmsType)) {
          _tmpLmsType = null;
        } else {
          _tmpLmsType = _cursor.getString(_cursorIndexOfLmsType);
        }
        final String _tmpLmsId;
        if (_cursor.isNull(_cursorIndexOfLmsId)) {
          _tmpLmsId = null;
        } else {
          _tmpLmsId = _cursor.getString(_cursorIndexOfLmsId);
        }
        final String _tmpCourseId;
        if (_cursor.isNull(_cursorIndexOfCourseId)) {
          _tmpCourseId = null;
        } else {
          _tmpCourseId = _cursor.getString(_cursorIndexOfCourseId);
        }
        final String _tmpCourseName;
        if (_cursor.isNull(_cursorIndexOfCourseName)) {
          _tmpCourseName = null;
        } else {
          _tmpCourseName = _cursor.getString(_cursorIndexOfCourseName);
        }
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
        final long _tmpDueDate;
        _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
        final String _tmpSubmissionUrl;
        if (_cursor.isNull(_cursorIndexOfSubmissionUrl)) {
          _tmpSubmissionUrl = null;
        } else {
          _tmpSubmissionUrl = _cursor.getString(_cursorIndexOfSubmissionUrl);
        }
        final boolean _tmpIsSubmitted;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsSubmitted);
        _tmpIsSubmitted = _tmp != 0;
        final long _tmpSyncedAt;
        _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
        _item = new LMSAssignment(_tmpLmsType,_tmpLmsId,_tmpCourseId,_tmpCourseName,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpSubmissionUrl,_tmpIsSubmitted,_tmpSyncedAt);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        _result.add(_item);
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
