package com.studentlms.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.studentlms.data.models.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {
    @Insert
    long insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("SELECT * FROM reminders WHERE dateTime > :currentTime ORDER BY dateTime ASC")
    LiveData<List<Reminder>> getUpcomingReminders(long currentTime);

    @Query("SELECT * FROM reminders ORDER BY dateTime ASC")
    LiveData<List<Reminder>> getAllReminders();
}
