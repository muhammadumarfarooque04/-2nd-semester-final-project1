package cafeteria;

import cafeteria.ui.LoginFrame;
import cafeteria.util.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;

/**
 * Main – Application entry point.
 * Sets Look & Feel and launches the Login screen.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Use system Look & Feel for a native appearance
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Fall back to default Look & Feel silently
            }

            // Attempt database connection (non-blocking)
            Connection dbConn = DatabaseConnection.getConnection();
            if (dbConn == null) {
                System.out.println("⚠️  Running in Demo Mode (without database)");
            }

            // Launch on Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                try {
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                } catch (Exception e) {
                    System.err.println("[Main] Error launching LoginFrame: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Startup Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        } catch (Exception e) {
            System.err.println("[Main] Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
