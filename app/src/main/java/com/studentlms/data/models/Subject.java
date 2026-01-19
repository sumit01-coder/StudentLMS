package com.studentlms.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subjects", indices = { @androidx.room.Index(value = { "subjectCode" }, unique = true) })
public class Subject {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String subjectCode; // e.g., "SECE3231"
    private String color;
    private int priority; // 1-5, higher is more important
    private int totalHours;
    private int semester; // 1-8
    private int contentCount; // Number of resources/contents
    private String contentUrl; // URL to the resource list page from ERP

    public Subject(String name, String subjectCode, String color, int priority, int totalHours, int semester,
            int contentCount, String contentUrl) {
        this.name = name;
        this.subjectCode = subjectCode;
        this.color = color;
        this.priority = priority;
        this.totalHours = totalHours;
        this.semester = semester;
        this.contentCount = contentCount;
        this.contentUrl = contentUrl;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getContentCount() {
        return contentCount;
    }

    public void setContentCount(int contentCount) {
        this.contentCount = contentCount;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
}
