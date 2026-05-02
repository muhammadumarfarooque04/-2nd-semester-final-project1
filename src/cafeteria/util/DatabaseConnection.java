package cafeteria.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Singleton utility class for managing database connections.
 * Provides a single shared connection to the MySQL database.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 * @author Cafeteria MS Team
 */
public class DatabaseConnection {

    // ── Database configuration ──────────────────────────────────────────────
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/cafeteria_db";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "";  // Change to your MySQL password

    private static Connection connection = null;

    /** Private constructor – prevents direct instantiation. */
    private DatabaseConnection() {}

    /**
     * Returns the singleton Connection instance.
     * Creates a new connection if one does not exist or has been closed.
     *
     * @return active {@link Connection} to cafeteria_db
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("[DB] Connection established successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
        }
        return connection;
    }

    /** Closes the current connection (call on application shutdown). */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
