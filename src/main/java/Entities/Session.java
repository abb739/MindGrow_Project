package Entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;

public class Session {

    private int idSession;
    private LocalDate Start_Session;
    private LocalTime Start_Time;
    private int dureeSession; // durée totale de la session
    private SessionType typeSession; // INDIVIDUEL / GROUPE
    private String lien;
    private SessionStatus status;
    private String title;
    private int idCoach;
    private String description;

    public Session() {}

    public Session(LocalDate start_Session, LocalTime start_Time, int dureeSession, SessionType typeSession, String lien, SessionStatus status, String title, int idCoach, String description) {
        Start_Session = start_Session;
        Start_Time = start_Time;
        this.dureeSession = dureeSession;
        this.typeSession = typeSession;
        this.lien = lien;
        this.status = status;
        this.title = title;
        this.idCoach = idCoach;
        this.description = description;
    }

    public int getIdSession() {
        return idSession;
    }

    public void setIdSession(int idSession) {
        this.idSession = idSession;
    }

    public LocalDate getStart_Session() {
        return Start_Session;
    }

    public void setStart_Session(LocalDate start_Session) {
        Start_Session = start_Session;
    }

    public LocalTime getStart_Time() {
        return Start_Time;
    }

    public void setStart_Time(LocalTime start_Time) {
        Start_Time = start_Time;
    }

    public int getDureeSession() {
        return dureeSession;
    }

    public void setDureeSession(int dureeSession) {
        this.dureeSession = dureeSession;
    }

    public SessionType getTypeSession() {
        return typeSession;
    }

    public void setTypeSession(SessionType typeSession) {
        this.typeSession = typeSession;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIdCoach() {
        return idCoach;
    }

    public void setIdCoach(int idCoach) {
        this.idCoach = idCoach;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Session session)) return false;
        return idSession == session.idSession;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idSession);
    }

    @Override
    public String toString() {
        return "Session{" +
                "idSession=" + idSession +
                ", Start_Session=" + Start_Session +
                ", Start_Time=" + Start_Time +
                ", dureeSession=" + dureeSession +
                ", typeSession=" + typeSession +
                ", lien='" + lien + '\'' +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", idCoach=" + idCoach +
                ", description='" + description + '\'' +
                '}';
    }
}
