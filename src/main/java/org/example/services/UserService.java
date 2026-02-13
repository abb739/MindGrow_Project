package org.example.services;

import org.example.models.User;
import org.example.utils.MyDatabase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service handling all user-related database operations.
 */
public class UserService {

    private Connection connection;

    public UserService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    // ==================== CRUD ====================

    /**
     * Registers a new user with hashed password.
     */
    public void register(User user) throws SQLException {
        // Check for duplicate email
        String check = "SELECT id FROM utilisateur WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(check)) {
            ps.setString(1, user.getEmail());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                throw new SQLException("Email already exists");
            }
        }

        String req = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, roles, telephone, date_naissance, date_inscription) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            String hashedPassword = BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt(13));
            ps.setString(4, hashedPassword);
            ps.setString(5, user.getRoles());
            ps.setString(6, user.getTelephone());
            ps.setString(7, user.getDateNaissance());
            ps.executeUpdate();
        }
    }

    /**
     * Returns all users from the database.
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM utilisateur";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    /**
     * Updates a user's profile (does NOT update password).
     */
    public void update(User user) throws SQLException {
        String req = "UPDATE utilisateur SET nom=?, prenom=?, email=?, roles=?, telephone=?, date_naissance=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRoles());
            ps.setString(5, user.getTelephone());
            ps.setString(6, user.getDateNaissance());
            ps.setInt(7, user.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Deletes a user by ID.
     */
    public void delete(int id) throws SQLException {
        String req = "DELETE FROM utilisateur WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ==================== AUTH ====================

    /**
     * Authenticates a user by email and password.
     * Handles both $2y$ (PHP) and $2a$ (Java) BCrypt prefixes.
     */
    public User authenticate(String email, String password) throws SQLException {
        String req = "SELECT * FROM utilisateur WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("mot_de_passe");
                if (storedHash.startsWith("$2y$")) {
                    storedHash = storedHash.replaceFirst("\\$2y\\$", "\\$2a\\$");
                }
                if (BCrypt.checkpw(password, storedHash)) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    // ==================== SEARCH & FILTER ====================

    /**
     * Searches users by name or email (case-insensitive).
     */
    public List<User> searchUsers(String keyword) throws SQLException {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM utilisateur WHERE LOWER(nom) LIKE ? OR LOWER(prenom) LIKE ? OR LOWER(email) LIKE ?";
        String search = "%" + keyword.toLowerCase() + "%";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    /**
     * Filters users by role string.
     */
    public List<User> filterByRole(String role) throws SQLException {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM utilisateur WHERE roles LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, "%" + role + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    // ==================== PASSWORD RESET ====================

    /**
     * Finds a user by email.
     */
    public User getUserByEmail(String email) throws SQLException {
        String req = "SELECT * FROM utilisateur WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        }
        return null;
    }

    /**
     * Generates and saves a reset token for a user. Returns the token.
     */
    public String generateResetToken(int userId) throws SQLException {
        String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String expiry = LocalDateTime.now().plusMinutes(15)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String req = "UPDATE utilisateur SET reset_token=?, reset_token_expiry=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, token);
            ps.setString(2, expiry);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
        return token;
    }

    /**
     * Validates a reset token and returns the user if valid and not expired.
     */
    public User getUserByResetToken(String token) throws SQLException {
        String req = "SELECT * FROM utilisateur WHERE reset_token = ? AND reset_token_expiry > NOW()";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        }
        return null;
    }

    /**
     * Updates a user's password and clears the reset token.
     */
    public void updatePassword(int userId, String newPassword) throws SQLException {
        String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt(13));
        String req = "UPDATE utilisateur SET mot_de_passe=?, reset_token=NULL, reset_token_expiry=NULL WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, hashed);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // ==================== HELPER ====================

    /**
     * Maps a ResultSet row to a User object.
     */
    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setMotDePasse(rs.getString("mot_de_passe"));
        u.setRoles(rs.getString("roles"));
        u.setTelephone(rs.getString("telephone"));
        u.setDateNaissance(rs.getString("date_naissance"));
        // Try to read reset token columns (may not exist yet)
        try {
            u.setResetToken(rs.getString("reset_token"));
            u.setResetTokenExpiry(rs.getString("reset_token_expiry"));
        } catch (SQLException ignored) {
            // Columns don't exist yet — that's fine
        }
        return u;
    }
}
