package org.example.service;

import org.example.model.Course;
import org.example.model.Exercise;
import org.example.model.Video;
import org.example.repository.CourseRepository;
import org.example.repository.ExerciseRepository;
import org.example.repository.VideoRepository;

import java.util.List;

public class TherapistService {
    private final VideoRepository videoRepository;
    private final ExerciseRepository exerciseRepository;
    private final CourseRepository courseRepository;

    public TherapistService() {
        this.videoRepository = new VideoRepository();
        this.exerciseRepository = new ExerciseRepository();
        this.courseRepository = new CourseRepository();
    }

    // --- Course Management ---
    public void addCourse(String title, String description, int therapistId) {
        Course course = new Course(title, description, therapistId);
        courseRepository.addCourse(course);
    }

    public List<Course> getCoursesByTherapist(int therapistId) {
        return courseRepository.getCoursesByTherapist(therapistId);
    }

    public boolean deleteCourse(int id) {
        return courseRepository.deleteCourse(id);
    }

    public boolean updateCourse(Course course) {
        courseRepository.updateCourse(course);
        return true;
    }

    // --- Video Management ---
    public void addVideo(String title, String description, String url, int courseId) {
        Video video = new Video(title, description, url, courseId);
        videoRepository.addVideo(video);
    }

    public List<Video> getVideosByCourse(int courseId) {
        return videoRepository.getVideosByCourse(courseId);
    }

    public boolean deleteVideo(int id) {
        return videoRepository.deleteVideo(id);
    }

    public boolean updateVideo(Video video) {
        return videoRepository.updateVideo(video);
    }

    // --- Exercise Management ---
    public void addExercise(String title, String description, int durationMinutes, int courseId) {
        Exercise exercise = new Exercise(title, description, durationMinutes, courseId);
        exerciseRepository.addExercise(exercise);
    }

    public List<Exercise> getExercisesByCourse(int courseId) {
        return exerciseRepository.getExercisesByCourse(courseId);
    }

    public boolean deleteExercise(int id) {
        return exerciseRepository.deleteExercise(id);
    }

    public boolean updateExercise(Exercise exercise) {
        return exerciseRepository.updateExercise(exercise);
    }
}
