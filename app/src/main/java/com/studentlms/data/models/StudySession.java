package com.studentlms.data.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "study_sessions")
public class StudySession {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String subjectName;
    private long startTime;
    private long endTime;
    private String notes;
    private boolean completed;

    public StudySession(String subjectName, long startTime, long endTime, String notes, boolean completed) {
        this.subjectName = subjectName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes;
        this.completed = completed;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
