package org.example.repository;

import org.example.model.Course;
import org.example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientRepository {

    public boolean enrollInCourse(int userId, int courseId) {
        String query = "INSERT IGNORE INTO enrollments (user_id, course_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Course> getEnrolledCourses(int userId) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT c.* FROM courses c JOIN enrollments e ON c.id = e.course_id WHERE e.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
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

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM courses";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
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

    public boolean markContentComplete(int userId, int courseId, String contentType, int contentId) {
        String query = "INSERT IGNORE INTO content_completion (user_id, course_id, content_type, content_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            stmt.setString(3, contentType);
            stmt.setInt(4, contentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getProgress(int userId, int courseId) {
        String countTotal = "SELECT (SELECT COUNT(*) FROM videos WHERE course_id = ?) + (SELECT COUNT(*) FROM exercises WHERE course_id = ?)";
        String countCompleted = "SELECT COUNT(*) FROM content_completion WHERE user_id = ? AND course_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmtTotal = conn.prepareStatement(countTotal);
                PreparedStatement stmtCompleted = conn.prepareStatement(countCompleted)) {

            stmtTotal.setInt(1, courseId);
            stmtTotal.setInt(2, courseId);
            ResultSet rsTotal = stmtTotal.executeQuery();

            stmtCompleted.setInt(1, userId);
            stmtCompleted.setInt(2, courseId);
            ResultSet rsCompleted = stmtCompleted.executeQuery();

            if (rsTotal.next() && rsCompleted.next()) {
                int total = rsTotal.getInt(1);
                int completed = rsCompleted.getInt(1);
                if (total == 0)
                    return 0.0;
                return (double) completed / total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public Course getCourseById(int courseId) {
        String query = "SELECT * FROM courses WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Course(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("therapist_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean removeContentCompletion(int userId, int courseId, String contentType, int contentId) {
        String query = "DELETE FROM content_completion WHERE user_id = ? AND course_id = ? AND content_type = ? AND content_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            stmt.setString(3, contentType);
            stmt.setInt(4, contentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isContentCompleted(int userId, int courseId, String contentType, int contentId) {
        String query = "SELECT COUNT(*) FROM content_completion WHERE user_id = ? AND course_id = ? AND content_type = ? AND content_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            stmt.setString(3, contentType);
            stmt.setInt(4, contentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
