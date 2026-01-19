package com.studentlms.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.studentlms.data.models.StudySession;

import java.util.List;

@Dao
public interface StudySessionDao {
    @Insert
    long insert(StudySession studySession);

    @Update
    void update(StudySession studySession);

    @Delete
    void delete(StudySession studySession);

    @Query("SELECT * FROM study_sessions ORDER BY startTime DESC")
    LiveData<List<StudySession>> getAllSessions();

    @Query("SELECT * FROM study_sessions WHERE subjectName = :subjectName ORDER BY startTime DESC")
    LiveData<List<StudySession>> getSessionsBySubject(String subjectName);

    @Query("SELECT * FROM study_sessions WHERE startTime > :currentTime ORDER BY startTime ASC")
    LiveData<List<StudySession>> getUpcomingSessions(long currentTime);

    @Query("SELECT * FROM study_sessions WHERE completed = 1 ORDER BY startTime DESC")
    LiveData<List<StudySession>> getCompletedSessions();

    @Query("SELECT * FROM study_sessions WHERE startTime BETWEEN :startTime AND :endTime ORDER BY startTime ASC")
    LiveData<List<StudySession>> getSessionsInRange(long startTime, long endTime);

    @Query("SELECT * FROM study_sessions WHERE startTime BETWEEN :startTime AND :endTime ORDER BY startTime ASC")
    List<StudySession> getSessionsInRangeSync(long startTime, long endTime);
}
