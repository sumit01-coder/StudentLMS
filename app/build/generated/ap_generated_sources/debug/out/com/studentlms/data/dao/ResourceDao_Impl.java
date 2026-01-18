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
import com.studentlms.data.models.Resource;
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
public final class ResourceDao_Impl implements ResourceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Resource> __insertionAdapterOfResource;

  private final EntityDeletionOrUpdateAdapter<Resource> __deletionAdapterOfResource;

  private final EntityDeletionOrUpdateAdapter<Resource> __updateAdapterOfResource;

  public ResourceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfResource = new EntityInsertionAdapter<Resource>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `resources` (`id`,`title`,`type`,`urlOrPath`,`subjectId`,`addedDate`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Resource entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getType() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getType());
        }
        if (entity.getUrlOrPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getUrlOrPath());
        }
        statement.bindLong(5, entity.getSubjectId());
        statement.bindLong(6, entity.getAddedDate());
      }
    };
    this.__deletionAdapterOfResource = new EntityDeletionOrUpdateAdapter<Resource>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `resources` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Resource entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfResource = new EntityDeletionOrUpdateAdapter<Resource>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `resources` SET `id` = ?,`title` = ?,`type` = ?,`urlOrPath` = ?,`subjectId` = ?,`addedDate` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Resource entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getType() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getType());
        }
        if (entity.getUrlOrPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getUrlOrPath());
        }
        statement.bindLong(5, entity.getSubjectId());
        statement.bindLong(6, entity.getAddedDate());
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public long insert(final Resource resource) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfResource.insertAndReturnId(resource);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Resource resource) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfResource.handle(resource);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Resource resource) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfResource.handle(resource);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<Resource>> getAllResources() {
    final String _sql = "SELECT * FROM resources ORDER BY addedDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"resources"}, false, new Callable<List<Resource>>() {
      @Override
      @Nullable
      public List<Resource> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfUrlOrPath = CursorUtil.getColumnIndexOrThrow(_cursor, "urlOrPath");
          final int _cursorIndexOfSubjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "subjectId");
          final int _cursorIndexOfAddedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "addedDate");
          final List<Resource> _result = new ArrayList<Resource>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Resource _item;
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final String _tmpUrlOrPath;
            if (_cursor.isNull(_cursorIndexOfUrlOrPath)) {
              _tmpUrlOrPath = null;
            } else {
              _tmpUrlOrPath = _cursor.getString(_cursorIndexOfUrlOrPath);
            }
            final int _tmpSubjectId;
            _tmpSubjectId = _cursor.getInt(_cursorIndexOfSubjectId);
            final long _tmpAddedDate;
            _tmpAddedDate = _cursor.getLong(_cursorIndexOfAddedDate);
            _item = new Resource(_tmpTitle,_tmpType,_tmpUrlOrPath,_tmpSubjectId,_tmpAddedDate);
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
  public LiveData<List<Resource>> getResourcesBySubject(final int subjectId) {
    final String _sql = "SELECT * FROM resources WHERE subjectId = ? ORDER BY addedDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, subjectId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"resources"}, false, new Callable<List<Resource>>() {
      @Override
      @Nullable
      public List<Resource> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfUrlOrPath = CursorUtil.getColumnIndexOrThrow(_cursor, "urlOrPath");
          final int _cursorIndexOfSubjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "subjectId");
          final int _cursorIndexOfAddedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "addedDate");
          final List<Resource> _result = new ArrayList<Resource>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Resource _item;
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final String _tmpUrlOrPath;
            if (_cursor.isNull(_cursorIndexOfUrlOrPath)) {
              _tmpUrlOrPath = null;
            } else {
              _tmpUrlOrPath = _cursor.getString(_cursorIndexOfUrlOrPath);
            }
            final int _tmpSubjectId;
            _tmpSubjectId = _cursor.getInt(_cursorIndexOfSubjectId);
            final long _tmpAddedDate;
            _tmpAddedDate = _cursor.getLong(_cursorIndexOfAddedDate);
            _item = new Resource(_tmpTitle,_tmpType,_tmpUrlOrPath,_tmpSubjectId,_tmpAddedDate);
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
  public LiveData<List<Resource>> getResourcesByType(final String type) {
    final String _sql = "SELECT * FROM resources WHERE type = ? ORDER BY addedDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (type == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, type);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"resources"}, false, new Callable<List<Resource>>() {
      @Override
      @Nullable
      public List<Resource> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfUrlOrPath = CursorUtil.getColumnIndexOrThrow(_cursor, "urlOrPath");
          final int _cursorIndexOfSubjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "subjectId");
          final int _cursorIndexOfAddedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "addedDate");
          final List<Resource> _result = new ArrayList<Resource>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Resource _item;
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final String _tmpUrlOrPath;
            if (_cursor.isNull(_cursorIndexOfUrlOrPath)) {
              _tmpUrlOrPath = null;
            } else {
              _tmpUrlOrPath = _cursor.getString(_cursorIndexOfUrlOrPath);
            }
            final int _tmpSubjectId;
            _tmpSubjectId = _cursor.getInt(_cursorIndexOfSubjectId);
            final long _tmpAddedDate;
            _tmpAddedDate = _cursor.getLong(_cursorIndexOfAddedDate);
            _item = new Resource(_tmpTitle,_tmpType,_tmpUrlOrPath,_tmpSubjectId,_tmpAddedDate);
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
  public LiveData<List<Resource>> searchResources(final String query) {
    final String _sql = "SELECT * FROM resources WHERE title LIKE '%' || ? || '%' ORDER BY addedDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"resources"}, false, new Callable<List<Resource>>() {
      @Override
      @Nullable
      public List<Resource> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfUrlOrPath = CursorUtil.getColumnIndexOrThrow(_cursor, "urlOrPath");
          final int _cursorIndexOfSubjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "subjectId");
          final int _cursorIndexOfAddedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "addedDate");
          final List<Resource> _result = new ArrayList<Resource>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Resource _item;
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final String _tmpUrlOrPath;
            if (_cursor.isNull(_cursorIndexOfUrlOrPath)) {
              _tmpUrlOrPath = null;
            } else {
              _tmpUrlOrPath = _cursor.getString(_cursorIndexOfUrlOrPath);
            }
            final int _tmpSubjectId;
            _tmpSubjectId = _cursor.getInt(_cursorIndexOfSubjectId);
            final long _tmpAddedDate;
            _tmpAddedDate = _cursor.getLong(_cursorIndexOfAddedDate);
            _item = new Resource(_tmpTitle,_tmpType,_tmpUrlOrPath,_tmpSubjectId,_tmpAddedDate);
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
