package com.studentlms.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.studentlms.data.models.Subject;

import java.util.List;

@Dao
public interface SubjectDao {
    @Insert
    long insert(Subject subject);

    @Update
    void update(Subject subject);

    @Delete
    void delete(Subject subject);

    @Query("SELECT * FROM subjects ORDER BY priority DESC")
    LiveData<List<Subject>> getAllSubjects();

    @Query("SELECT * FROM subjects WHERE id = :id")
    LiveData<Subject> getSubjectById(int id);
}
