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
import com.studentlms.data.models.ChatMessage;
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
public final class ChatMessageDao_Impl implements ChatMessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ChatMessage> __insertionAdapterOfChatMessage;

  private final EntityDeletionOrUpdateAdapter<ChatMessage> __deletionAdapterOfChatMessage;

  private final SharedSQLiteStatement __preparedStmtOfClearSession;

  private final SharedSQLiteStatement __preparedStmtOfClearAllMessages;

  public ChatMessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfChatMessage = new EntityInsertionAdapter<ChatMessage>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `chat_messages` (`id`,`message`,`isUser`,`timestamp`,`sessionId`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ChatMessage entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getMessage() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getMessage());
        }
        final int _tmp = entity.isUser() ? 1 : 0;
        statement.bindLong(3, _tmp);
        statement.bindLong(4, entity.getTimestamp());
        if (entity.getSessionId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getSessionId());
        }
      }
    };
    this.__deletionAdapterOfChatMessage = new EntityDeletionOrUpdateAdapter<ChatMessage>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `chat_messages` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ChatMessage entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfClearSession = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM chat_messages WHERE sessionId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllMessages = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM chat_messages";
        return _query;
      }
    };
  }

  @Override
  public long insert(final ChatMessage message) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfChatMessage.insertAndReturnId(message);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final ChatMessage message) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfChatMessage.handle(message);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clearSession(final String sessionId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearSession.acquire();
    int _argIndex = 1;
    if (sessionId == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, sessionId);
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
      __preparedStmtOfClearSession.release(_stmt);
    }
  }

  @Override
  public void clearAllMessages() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllMessages.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfClearAllMessages.release(_stmt);
    }
  }

  @Override
  public LiveData<List<ChatMessage>> getMessagesBySession(final String sessionId) {
    final String _sql = "SELECT * FROM chat_messages WHERE sessionId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (sessionId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, sessionId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"chat_messages"}, false, new Callable<List<ChatMessage>>() {
      @Override
      @Nullable
      public List<ChatMessage> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfIsUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isUser");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final List<ChatMessage> _result = new ArrayList<ChatMessage>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatMessage _item;
            final String _tmpMessage;
            if (_cursor.isNull(_cursorIndexOfMessage)) {
              _tmpMessage = null;
            } else {
              _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            }
            final boolean _tmpIsUser;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUser);
            _tmpIsUser = _tmp != 0;
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpSessionId;
            if (_cursor.isNull(_cursorIndexOfSessionId)) {
              _tmpSessionId = null;
            } else {
              _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            }
            _item = new ChatMessage(_tmpMessage,_tmpIsUser,_tmpTimestamp,_tmpSessionId);
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
  public LiveData<List<ChatMessage>> getRecentMessages() {
    final String _sql = "SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 50";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"chat_messages"}, false, new Callable<List<ChatMessage>>() {
      @Override
      @Nullable
      public List<ChatMessage> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfIsUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isUser");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final List<ChatMessage> _result = new ArrayList<ChatMessage>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatMessage _item;
            final String _tmpMessage;
            if (_cursor.isNull(_cursorIndexOfMessage)) {
              _tmpMessage = null;
            } else {
              _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            }
            final boolean _tmpIsUser;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUser);
            _tmpIsUser = _tmp != 0;
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpSessionId;
            if (_cursor.isNull(_cursorIndexOfSessionId)) {
              _tmpSessionId = null;
            } else {
              _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            }
            _item = new ChatMessage(_tmpMessage,_tmpIsUser,_tmpTimestamp,_tmpSessionId);
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
