package com.studentlms.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.studentlms.data.models.LMSAssignment;

import java.util.List;

@Dao
public interface LMSAssignmentDao {
    @Insert
    long insert(LMSAssignment assignment);

    @Update
    void update(LMSAssignment assignment);

    @Delete
    void delete(LMSAssignment assignment);

    @Query("SELECT * FROM lms_assignments WHERE dueDate > :currentTime ORDER BY dueDate ASC")
    LiveData<List<LMSAssignment>> getUpcomingAssignments(long currentTime);

    @Query("SELECT * FROM lms_assignments WHERE lmsType = :lmsType ORDER BY dueDate ASC")
    LiveData<List<LMSAssignment>> getAssignmentsByLMS(String lmsType);

    @Query("SELECT * FROM lms_assignments WHERE lmsId = :lmsId AND lmsType = :lmsType LIMIT 1")
    LMSAssignment getAssignmentByLMSId(String lmsId, String lmsType);

    @Query("DELETE FROM lms_assignments WHERE lmsType = :lmsType")
    void deleteByLMSType(String lmsType);

    @Query("SELECT * FROM lms_assignments WHERE courseName = :courseName")
    List<LMSAssignment> getAssignmentsBySubjectSync(String courseName);

    @Query("SELECT * FROM lms_assignments WHERE dueDate BETWEEN :startTime AND :endTime ORDER BY dueDate ASC")
    List<LMSAssignment> getAssignmentsByDateRange(long startTime, long endTime);

    @Query("SELECT DISTINCT courseName FROM lms_assignments ORDER BY courseName ASC")
    LiveData<List<String>> getDistinctCourseNames();

    @Query("SELECT * FROM lms_assignments WHERE isSubmitted = 0 AND dueDate > :currentTime AND dueDate <= :endTime ORDER BY dueDate ASC")
    List<LMSAssignment> getPendingAssignmentsInRange(long currentTime, long endTime);

    @Query("SELECT COUNT(*) FROM lms_assignments WHERE lmsId = :lmsId")
    int countByLmsId(String lmsId);

    @Query("SELECT COUNT(*) FROM lms_assignments WHERE title = :title AND courseName = :courseName")
    int countByTitleAndSubject(String title, String courseName);
}
