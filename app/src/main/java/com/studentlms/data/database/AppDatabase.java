package com.studentlms.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.studentlms.data.dao.*;
import com.studentlms.data.models.*;

@Database(entities = {
        Subject.class,
        StudySession.class,
        LMSAssignment.class,
        LMSAccount.class,
        Reminder.class,
        Resource.class,
        ChatMessage.class
}, version = 10, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract SubjectDao subjectDao();

    public abstract LMSAssignmentDao lmsAssignmentDao();

    public abstract LMSAccountDao lmsAccountDao();

    public abstract ReminderDao reminderDao();

    public abstract ResourceDao resourceDao();

    public abstract StudySessionDao studySessionDao();

    public abstract ChatMessageDao chatMessageDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "student_lms_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
