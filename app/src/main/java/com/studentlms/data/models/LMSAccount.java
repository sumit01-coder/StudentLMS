package com.studentlms.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lms_accounts")
public class LMSAccount {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String lmsType; // "GOOGLE_CLASSROOM", "MOODLE", "CANVAS"
    private String email;
    private String accessToken;
    private String refreshToken;
    private long lastSyncTime;
    private boolean isActive;

    public LMSAccount(String lmsType, String email, String accessToken, String refreshToken,
            long lastSyncTime, boolean isActive) {
        this.lmsType = lmsType;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.lastSyncTime = lastSyncTime;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLmsType() {
        return lmsType;
    }

    public void setLmsType(String lmsType) {
        this.lmsType = lmsType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(long lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
