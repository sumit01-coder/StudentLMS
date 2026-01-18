package com.studentlms.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subjects")
public class Subject {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String color;
    private int priority; // 1-5, higher is more important
    private int totalHours;

    public Subject(String name, String color, int priority, int totalHours) {
        this.name = name;
        this.color = color;
        this.priority = priority;
        this.totalHours = totalHours;
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
}
