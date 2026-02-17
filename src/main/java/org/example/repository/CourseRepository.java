package org.example.repository;

import org.example.model.Course;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {
    private Connection connection;

    public CourseRepository() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void addCourse(Course course) {
        String query = "INSERT INTO courses (title, description, therapist_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, course.getTitle());
            stmt.setString(2, course.getDescription());
            stmt.setInt(3, course.getTherapistId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Course> getCoursesByTherapist(int therapistId) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM courses WHERE therapist_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, therapistId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("therapist_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public void updateCourse(Course course) {
        String query = "UPDATE courses SET title = ?, description = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, course.getTitle());
            stmt.setString(2, course.getDescription());
            stmt.setInt(3, course.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteCourse(int id) {
        String query = "DELETE FROM courses WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
