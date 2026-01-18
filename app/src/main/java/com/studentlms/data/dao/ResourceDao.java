package com.studentlms.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.studentlms.data.models.Resource;

import java.util.List;

@Dao
public interface ResourceDao {
    @Insert
    long insert(Resource resource);

    @Update
    void update(Resource resource);

    @Delete
    void delete(Resource resource);

    @Query("SELECT * FROM resources ORDER BY addedDate DESC")
    LiveData<List<Resource>> getAllResources();

    @Query("SELECT * FROM resources WHERE subjectId = :subjectId ORDER BY addedDate DESC")
    LiveData<List<Resource>> getResourcesBySubject(int subjectId);

    @Query("SELECT * FROM resources WHERE type = :type ORDER BY addedDate DESC")
    LiveData<List<Resource>> getResourcesByType(String type);

    @Query("SELECT * FROM resources WHERE title LIKE '%' || :query || '%' ORDER BY addedDate DESC")
    LiveData<List<Resource>> searchResources(String query);
}
