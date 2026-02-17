package org.example.model;

public class Course {
    private int id;
    private String title;
    private String description;
    private int therapistId;

    public Course() {
    }

    public Course(int id, String title, String description, int therapistId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.therapistId = therapistId;
    }

    public Course(String title, String description, int therapistId) {
        this.title = title;
        this.description = description;
        this.therapistId = therapistId;
    }

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

    public int getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(int therapistId) {
        this.therapistId = therapistId;
    }
}
