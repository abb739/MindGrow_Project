package org.example.model;

public class Exercise {
    private int id;
    private String title;
    private String description;
    private int durationMinutes;
    private int courseId;

    public Exercise() {
    }

    public Exercise(int id, String title, String description, int durationMinutes, int courseId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.courseId = courseId;
    }

    public Exercise(String title, String description, int durationMinutes, int courseId) {
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.courseId = courseId;
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

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
