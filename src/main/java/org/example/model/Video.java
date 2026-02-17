package org.example.model;

public class Video {
    private int id;
    private String title;
    private String description;
    private String url;
    private int courseId;

    public Video() {
    }

    public Video(int id, String title, String description, String url, int courseId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
        this.courseId = courseId;
    }

    public Video(String title, String description, String url, int courseId) {
        this.title = title;
        this.description = description;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
