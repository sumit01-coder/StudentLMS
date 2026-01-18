package com.studentlms.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.studentlms.data.models.LMSAccount;

import java.util.List;

@Dao
public interface LMSAccountDao {
    @Insert
    long insert(LMSAccount account);

    @Update
    void update(LMSAccount account);

    @Delete
    void delete(LMSAccount account);

    @Query("SELECT * FROM lms_accounts WHERE isActive = 1")
    LiveData<List<LMSAccount>> getActiveAccounts();

    @Query("SELECT * FROM lms_accounts")
    LiveData<List<LMSAccount>> getAllAccounts();

    @Query("SELECT * FROM lms_accounts WHERE lmsType = :lmsType AND isActive = 1 LIMIT 1")
    LMSAccount getActiveAccountByType(String lmsType);
}
