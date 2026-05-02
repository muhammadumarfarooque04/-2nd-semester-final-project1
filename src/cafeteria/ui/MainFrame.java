package cafeteria.ui;

import cafeteria.auth.Session;
import cafeteria.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * MainFrame – Primary application window.
 * Contains a sidebar navigation and a content panel that swaps views.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class MainFrame extends JFrame {

    private JPanel      contentPanel;
    private CardLayout  cardLayout;
    private JLabel      lblUser;

    // Navigation button references (for highlight tracking)
    private JButton     activeBtn = null;

    public MainFrame() {
        initComponents();
        setLocationRelativeTo(null);
        showPanel("dashboard");
    }

    private void initComponents() {
        setTitle("Cafeteria Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(900, 600));
        setLayout(new BorderLayout());

        // ── Sidebar ──────────────────────────────────────────────────────────
        JPanel sidebar = buildSidebar();

        // ── Content area ─────────────────────────────────────────────────────
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIConstants.LIGHT_BG);

        contentPanel.add(new DashboardPanel(),  "dashboard");
        contentPanel.add(new MenuPanel(),       "menu");
        contentPanel.add(new OrderPanel(),      "orders");

        if (Session.getInstance().isAdmin()) {
            contentPanel.add(new UserManagementPanel(), "users");
            contentPanel.add(new ReportPanel(),         "reports");
        }

        add(sidebar,       BorderLayout.WEST);
        add(contentPanel,  BorderLayout.CENTER);
    }

    // ── Sidebar builder ──────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIConstants.HEADER_COLOR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));

        // App logo area
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setBackground(UIConstants.PRIMARY_COLOR);
        logoPanel.setMaximumSize(new Dimension(220, 80));
        logoPanel.setPreferredSize(new Dimension(220, 80));

        JLabel lblLogo = new JLabel("🍽 Cafeteria MS", SwingConstants.CENTER);
        lblLogo.setFont(UIConstants.HEADER_FONT);
        lblLogo.setForeground(UIConstants.WHITE);
        logoPanel.add(lblLogo);
        sidebar.add(logoPanel);

        // User info area
        JPanel userArea = new JPanel(new GridBagLayout());
        userArea.setBackground(new Color(52, 73, 94));
        userArea.setMaximumSize(new Dimension(220, 60));
        userArea.setBorder(new EmptyBorder(10, 15, 10, 15));

        Session session = Session.getInstance();
        String userName  = session.getCurrentUser().getFullName();
        String userRole  = session.getCurrentUser().getRole();

        lblUser = new JLabel("<html><b>" + userName + "</b><br><small>" + userRole + "</small></html>");
        lblUser.setForeground(UIConstants.WHITE);
        lblUser.setFont(UIConstants.SMALL_FONT);
        userArea.add(lblUser);
        sidebar.add(userArea);

        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        // Navigation items
        JButton btnDashboard = navButton("📊  Dashboard",  "dashboard");
        JButton btnMenu      = navButton("🍜  Menu Items",  "menu");
        JButton btnOrders    = navButton("📋  Orders",      "orders");

        sidebar.add(btnDashboard);
        sidebar.add(btnMenu);
        sidebar.add(btnOrders);

        if (session.isAdmin()) {
            sidebar.add(navButton("👥  Users",    "users"));
            sidebar.add(navButton("📈  Reports",  "reports"));
        }

        sidebar.add(Box.createVerticalGlue());

        // Logout button at bottom
        JButton btnLogout = new JButton("⏻  Logout");
        styleNavButton(btnLogout);
        btnLogout.setBackground(UIConstants.DANGER_COLOR);
        btnLogout.addActionListener(e -> logout());
        btnLogout.setMaximumSize(new Dimension(220, 45));
        sidebar.add(btnLogout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        setActive(btnDashboard);
        return sidebar;
    }

    private JButton navButton(String text, String panelName) {
        JButton btn = new JButton(text);
        styleNavButton(btn);
        btn.addActionListener(e -> {
            showPanel(panelName);
            setActive(btn);
        });
        btn.setMaximumSize(new Dimension(220, 45));
        return btn;
    }

    private void styleNavButton(JButton btn) {
        btn.setFont(UIConstants.BODY_FONT);
        btn.setForeground(UIConstants.WHITE);
        btn.setBackground(UIConstants.HEADER_COLOR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 20, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn != activeBtn) btn.setBackground(UIConstants.PRIMARY_COLOR.darker());
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn != activeBtn) btn.setBackground(UIConstants.HEADER_COLOR);
            }
        });
    }

    private void setActive(JButton btn) {
        if (activeBtn != null) activeBtn.setBackground(UIConstants.HEADER_COLOR);
        activeBtn = btn;
        activeBtn.setBackground(UIConstants.PRIMARY_COLOR);
    }

    // ── Navigation & Logout ──────────────────────────────────────────────────

    public void showPanel(String name) {
        cardLayout.show(contentPanel, name);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            Session.getInstance().logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
