package com.studentlms.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "resources")
public class Resource {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String type; // "PDF", "VIDEO", "LINK", "NOTE"
    private String urlOrPath;
    private int subjectId;
    private String subjectName; // For display on cards
    private int semester; // 1-8
    private long addedDate;
    private boolean isFavorite; // For quick access

    public Resource(String title, String type, String urlOrPath, int subjectId, int semester, long addedDate) {
        this.title = title;
        this.type = type;
        this.urlOrPath = urlOrPath;
        this.subjectId = subjectId;
        this.semester = semester;
        this.addedDate = addedDate;
        this.isFavorite = false;
        this.subjectName = "";
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrlOrPath() {
        return urlOrPath;
    }

    public void setUrlOrPath(String urlOrPath) {
        this.urlOrPath = urlOrPath;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
