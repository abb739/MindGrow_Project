package org.example.service;

import org.example.model.Course;
import org.example.repository.PatientRepository;

import java.util.List;

public class PatientService {
    private final PatientRepository patientRepository = new PatientRepository();

    public boolean enrollInCourse(int userId, int courseId) {
        return patientRepository.enrollInCourse(userId, courseId);
    }

    public List<Course> getEnrolledCourses(int userId) {
        return patientRepository.getEnrolledCourses(userId);
    }

    public List<Course> getAllCourses() {
        return patientRepository.getAllCourses();
    }

    public boolean markContentComplete(int userId, int courseId, String contentType, int contentId) {
        return patientRepository.markContentComplete(userId, courseId, contentType, contentId);
    }

    public double getProgress(int userId, int courseId) {
        return patientRepository.getProgress(userId, courseId);
    }

    public Course getCourseById(int courseId) {
        return patientRepository.getCourseById(courseId);
    }

    public boolean removeContentCompletion(int userId, int courseId, String contentType, int contentId) {
        return patientRepository.removeContentCompletion(userId, courseId, contentType, contentId);
    }

    public boolean isContentCompleted(int userId, int courseId, String contentType, int contentId) {
        return patientRepository.isContentCompleted(userId, courseId, contentType, contentId);
    }
}
