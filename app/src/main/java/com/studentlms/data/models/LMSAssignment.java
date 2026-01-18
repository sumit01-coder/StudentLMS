package com.studentlms.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lms_assignments")
public class LMSAssignment {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String lmsType; // "GOOGLE_CLASSROOM", "MOODLE", "CANVAS"
    private String lmsId; // Original assignment ID from LMS
    private String courseId;
    private String courseName;
    private String title;
    private String description;
    private long dueDate;
    private String submissionUrl;
    private boolean isSubmitted;
    private long syncedAt;

    public LMSAssignment(String lmsType, String lmsId, String courseId, String courseName,
            String title, String description, long dueDate, String submissionUrl,
            boolean isSubmitted, long syncedAt) {
        this.lmsType = lmsType;
        this.lmsId = lmsId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.submissionUrl = submissionUrl;
        this.isSubmitted = isSubmitted;
        this.syncedAt = syncedAt;
    }

    // Simplified constructor for ERP portal
    @androidx.room.Ignore
    public LMSAssignment(String courseName, String title, String description, long dueDate, String lmsType) {
        this.lmsType = lmsType;
        this.lmsId = "";
        this.courseId = "";
        this.courseName = courseName;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.submissionUrl = "";
        this.isSubmitted = false;
        this.syncedAt = System.currentTimeMillis();
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

    public String getLmsId() {
        return lmsId;
    }

    public void setLmsId(String lmsId) {
        this.lmsId = lmsId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public String getSubmissionUrl() {
        return submissionUrl;
    }

    public void setSubmissionUrl(String submissionUrl) {
        this.submissionUrl = submissionUrl;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public void setSubmitted(boolean submitted) {
        isSubmitted = submitted;
    }

    public long getSyncedAt() {
        return syncedAt;
    }

    public void setSyncedAt(long syncedAt) {
        this.syncedAt = syncedAt;
    }
}
