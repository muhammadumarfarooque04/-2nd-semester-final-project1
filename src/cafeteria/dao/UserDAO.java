package cafeteria.dao;

import cafeteria.model.User;
import cafeteria.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO – Data Access Object for all user-related database operations.
 * Implements CRUD + authentication.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class UserDAO {

    // ── Authentication ───────────────────────────────────────────────────────

    /**
     * Validates login credentials.
     *
     * @param username the entered username
     * @param password the entered password
     * @return a {@link User} object if valid, null otherwise
     */
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = TRUE";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[UserDAO] Database not available - using demo credentials");
                return demoLogin(username, password);
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] login error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Demo login for testing without database.
     */
    private User demoLogin(String username, String password) {
        if ("admin".equals(username) && "admin123".equals(password)) {
            return new User(1, "Admin User", "admin", "admin123", "admin@cafeteria.com", "ADMIN", true);
        } else if ("user1".equals(username) && "user123".equals(password)) {
            return new User(2, "John Doe", "user1", "user123", "user1@cafeteria.com", "USER", true);
        }
        return null;
    }

    // ── CRUD Operations ──────────────────────────────────────────────────────

    /** Returns all users from the database. */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[UserDAO] Database not available - returning empty list");
                return users;
            }
            try (Statement  st   = conn.createStatement();
                 ResultSet  rs   = st.executeQuery(sql)) {

                while (rs.next()) users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getAllUsers error: " + e.getMessage());
        }
        return users;
    }

    /** Inserts a new user. Returns true on success. */
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, full_name, role, email) VALUES (?,?,?,?,?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[UserDAO] Database not available - demo mode");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getFullName());
                ps.setString(4, user.getRole());
                ps.setString(5, user.getEmail());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] addUser error: " + e.getMessage());
            return false;
        }
    }

    /** Updates an existing user. Returns true on success. */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username=?, full_name=?, role=?, email=?, is_active=? WHERE user_id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[UserDAO] Database not available - demo mode");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString (1, user.getUsername());
                ps.setString (2, user.getFullName());
                ps.setString (3, user.getRole());
                ps.setString (4, user.getEmail());
                ps.setBoolean(5, user.isActive());
                ps.setInt    (6, user.getUserId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] updateUser error: " + e.getMessage());
            return false;
        }
    }

    /** Deletes a user by ID. Returns true on success. */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[UserDAO] Database not available - demo mode");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, userId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] deleteUser error: " + e.getMessage());
            return false;
        }
    }

    /** Searches users by name or username. */
    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE full_name LIKE ? OR username LIKE ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[UserDAO] Database not available - returning empty list");
                return users;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                String pattern = "%" + keyword + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] searchUsers error: " + e.getMessage());
        }
        return users;
    }

    /** Updates only the password for a user. */
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password=? WHERE user_id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[UserDAO] Database not available - demo mode");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, newPassword);
                ps.setInt   (2, userId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] updatePassword error: " + e.getMessage());
            return false;
        }
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private User extractUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt    ("user_id"),
            rs.getString ("username"),
            rs.getString ("password"),
            rs.getString ("full_name"),
            rs.getString ("role"),
            rs.getString ("email"),
            rs.getBoolean("is_active")
        );
    }
}
