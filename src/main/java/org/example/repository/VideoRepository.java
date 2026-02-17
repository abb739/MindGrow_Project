package org.example.repository;

import org.example.model.Video;
import org.example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VideoRepository {

    public void addVideo(Video video) {
        String query = "INSERT INTO videos (title, description, url, course_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, video.getTitle());
            stmt.setString(2, video.getDescription());
            stmt.setString(3, video.getUrl());
            stmt.setInt(4, video.getCourseId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Video> getVideosByCourse(int courseId) {
        List<Video> videos = new ArrayList<>();
        String query = "SELECT * FROM videos WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                videos.add(new Video(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("url"),
                        rs.getInt("course_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videos;
    }

    public boolean updateVideo(Video video) {
        String query = "UPDATE videos SET title = ?, description = ?, url = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, video.getTitle());
            stmt.setString(2, video.getDescription());
            stmt.setString(3, video.getUrl());
            stmt.setInt(4, video.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVideo(int id) {
        String query = "DELETE FROM videos WHERE id = ?";
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
