package cafeteria.ui;

import cafeteria.auth.Session;
import cafeteria.dao.UserDAO;
import cafeteria.model.User;
import cafeteria.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginFrame – Authentication screen with username/password fields.
 * Supports Admin and User roles.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class LoginFrame extends JFrame {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private JLabel         lblStatus;
    private final UserDAO  userDAO = new UserDAO();

    public LoginFrame() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Cafeteria Management System – Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 550);
        setResizable(false);

        // ── Background panel ────────────────────────────────────────────────
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0, UIConstants.PRIMARY_COLOR,
                    0, getHeight(), UIConstants.SECONDARY_COLOR);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // ── Header ──────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(40, 20, 20, 20));

        JLabel lblIcon  = new JLabel("🍽", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));

        JLabel lblTitle = new JLabel("Cafeteria MS", SwingConstants.CENTER);
        lblTitle.setFont(UIConstants.TITLE_FONT.deriveFont(28f));
        lblTitle.setForeground(UIConstants.WHITE);

        JLabel lblSub   = new JLabel("Mehran University – K25SW", SwingConstants.CENTER);
        lblSub.setFont(UIConstants.SMALL_FONT);
        lblSub.setForeground(new Color(200, 230, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(5,5,5,5);
        headerPanel.add(lblIcon, gbc);
        gbc.gridy = 1; headerPanel.add(lblTitle, gbc);
        gbc.gridy = 2; headerPanel.add(lblSub, gbc);

        // ── Login Card ──────────────────────────────────────────────────────
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UIConstants.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 40, 40, 40),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(30, 30, 30, 30))));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);

        // Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIConstants.BODY_FONT.deriveFont(Font.BOLD));
        c.gridx = 0; c.gridy = 0;
        card.add(lblUser, c);

        txtUsername = new JTextField(20);
        styleTextField(txtUsername);
        c.gridy = 1;
        card.add(txtUsername, c);

        // Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIConstants.BODY_FONT.deriveFont(Font.BOLD));
        c.gridy = 2;
        card.add(lblPass, c);

        txtPassword = new JPasswordField(20);
        styleTextField(txtPassword);
        c.gridy = 3;
        card.add(txtPassword, c);

        // Status label
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(UIConstants.SMALL_FONT);
        lblStatus.setForeground(UIConstants.DANGER_COLOR);
        c.gridy = 4;
        card.add(lblStatus, c);

        // Login button
        btnLogin = new JButton("LOGIN");
        styleButton(btnLogin, UIConstants.PRIMARY_COLOR);
        c.gridy = 5;
        card.add(btnLogin, c);

        // Hint label
        JLabel lblHint = new JLabel("<html><center>Admin: admin / admin123<br>User: user1 / user123</center></html>",
                                    SwingConstants.CENTER);
        lblHint.setFont(UIConstants.SMALL_FONT);
        lblHint.setForeground(Color.GRAY);
        c.gridy = 6; c.insets = new Insets(15, 0, 0, 0);
        card.add(lblHint, c);

        // ── Wrap card in a filler panel ─────────────────────────────────────
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.add(card, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(cardWrapper, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // ── Event listeners ─────────────────────────────────────────────────
        btnLogin.addActionListener(e -> handleLogin());
        txtPassword.addActionListener(e -> handleLogin()); // Enter key
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
    }

    // ── Login logic ──────────────────────────────────────────────────────────

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        // Input validation
        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Please enter username and password.");
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Authenticating...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                return userDAO.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        try {
                            Session.getInstance().setCurrentUser(user);
                            dispose();
                            MainFrame mainFrame = new MainFrame();
                            mainFrame.setVisible(true);
                        } catch (Exception e) {
                            System.err.println("[LoginFrame] Error opening MainFrame: " + e.getMessage());
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(LoginFrame.this, 
                                "Error loading main screen: " + e.getMessage(), 
                                "Error", JOptionPane.ERROR_MESSAGE);
                            btnLogin.setEnabled(true);
                            btnLogin.setText("LOGIN");
                        }
                    } else {
                        lblStatus.setText("Invalid username or password.");
                        txtPassword.setText("");
                        btnLogin.setEnabled(true);
                        btnLogin.setText("LOGIN");
                    }
                } catch (Exception ex) {
                    System.err.println("[LoginFrame] Login exception: " + ex.getMessage());
                    ex.printStackTrace();
                    lblStatus.setText("Login error: " + ex.getMessage());
                    btnLogin.setEnabled(true);
                    btnLogin.setText("LOGIN");
                }
            }
        };
        worker.execute();
    }

    // ── Style helpers ────────────────────────────────────────────────────────

    private void styleTextField(JTextField field) {
        field.setFont(UIConstants.BODY_FONT);
        field.setPreferredSize(new Dimension(300, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)));
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(UIConstants.BUTTON_FONT);
        btn.setBackground(bg);
        btn.setForeground(UIConstants.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(300, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
    }
}
