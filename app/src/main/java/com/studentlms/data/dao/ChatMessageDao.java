package com.studentlms.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.studentlms.data.models.ChatMessage;

import java.util.List;

@Dao
public interface ChatMessageDao {
    @Insert
    long insert(ChatMessage message);

    @Delete
    void delete(ChatMessage message);

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    LiveData<List<ChatMessage>> getMessagesBySession(String sessionId);

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 50")
    LiveData<List<ChatMessage>> getRecentMessages();

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    void clearSession(String sessionId);

    @Query("DELETE FROM chat_messages")
    void clearAllMessages();
}
