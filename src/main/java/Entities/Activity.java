package Entities;


import java.time.LocalTime;

public class Activity {
    private int idActivity;
    private ActivityType type;
    private String description;
    private int duree;
    private LocalTime startTime;
    private int sessionId;

    // ==========================
    // CONSTRUCTEURS
    // ==========================
    public Activity() { }

    public Activity(int idActivity, ActivityType type, String description, int duree, LocalTime startTime, int sessionId) {
        this.idActivity = idActivity;
        this.type = type;
        this.description = description;
        this.duree = duree;
        this.startTime = startTime;
        this.sessionId = sessionId;
    }

    // ==========================
    // GETTERS & SETTERS
    // ==========================
    public int getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(int idActivity) {
        this.idActivity = idActivity;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "idActivity=" + idActivity +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", duree=" + duree +
                ", startTime=" + startTime +
                ", sessionId=" + sessionId +
                '}';
    }
}
