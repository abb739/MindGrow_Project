package org.example.repository;

import org.example.model.Exercise;
import org.example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExerciseRepository {

    public void addExercise(Exercise exercise) {
        String query = "INSERT INTO exercises (title, description, duration_minutes, course_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, exercise.getTitle());
            stmt.setString(2, exercise.getDescription());
            stmt.setInt(3, exercise.getDurationMinutes());
            stmt.setInt(4, exercise.getCourseId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Exercise> getExercisesByCourse(int courseId) {
        List<Exercise> exercises = new ArrayList<>();
        String query = "SELECT * FROM exercises WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                exercises.add(new Exercise(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("duration_minutes"),
                        rs.getInt("course_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exercises;
    }

    public boolean updateExercise(Exercise exercise) {
        String query = "UPDATE exercises SET title = ?, description = ?, duration_minutes = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, exercise.getTitle());
            stmt.setString(2, exercise.getDescription());
            stmt.setInt(3, exercise.getDurationMinutes());
            stmt.setInt(4, exercise.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteExercise(int id) {
        String query = "DELETE FROM exercises WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
