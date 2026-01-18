package com.studentlms.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private long dateTime;
    private String type; // "STUDY_SESSION", "ASSIGNMENT", "CUSTOM"
    private boolean isRecurring;
    private int relatedId; // ID of study session or assignment

    public Reminder(String title, String description, long dateTime, String type,
            boolean isRecurring, int relatedId) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.type = type;
        this.isRecurring = isRecurring;
        this.relatedId = relatedId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public int getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(int relatedId) {
        this.relatedId = relatedId;
    }
}
