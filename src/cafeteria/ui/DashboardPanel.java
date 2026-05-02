package cafeteria.ui;

import cafeteria.auth.Session;
import cafeteria.dao.MenuItemDAO;
import cafeteria.dao.OrderDAO;
import cafeteria.dao.UserDAO;
import cafeteria.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * DashboardPanel – Home screen showing summary statistics.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class DashboardPanel extends JPanel {

    private final OrderDAO    orderDAO    = new OrderDAO();
    private final MenuItemDAO menuDAO     = new MenuItemDAO();
    private final UserDAO     userDAO     = new UserDAO();

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.LIGHT_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
    }

    private void buildUI() {
        // ── Header ───────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.LIGHT_BG);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        String name = Session.getInstance().getCurrentUser().getFullName();
        JLabel lblWelcome = new JLabel("Welcome, " + name + "!");
        lblWelcome.setFont(UIConstants.TITLE_FONT);
        lblWelcome.setForeground(UIConstants.SECONDARY_COLOR);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
        JLabel lblDate = new JLabel(today, SwingConstants.RIGHT);
        lblDate.setFont(UIConstants.BODY_FONT);
        lblDate.setForeground(Color.GRAY);

        header.add(lblWelcome, BorderLayout.WEST);
        header.add(lblDate,    BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── Stat cards ───────────────────────────────────────────────────────
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        statsGrid.setOpaque(false);

        int     todayOrders   = orderDAO.getTodayOrderCount();
        int     pendingOrders = orderDAO.getPendingOrderCount();
        double  revenue       = orderDAO.getTotalRevenue();
        int     menuItems     = menuDAO.getAllMenuItems().size();

        statsGrid.add(statCard("📋", "Today's Orders",  String.valueOf(todayOrders),   UIConstants.PRIMARY_COLOR));
        statsGrid.add(statCard("⏳", "Pending Orders",  String.valueOf(pendingOrders),  UIConstants.WARNING_COLOR));
        statsGrid.add(statCard("💰", "Total Revenue",   String.format("Rs %.0f", revenue), UIConstants.SUCCESS_COLOR));
        statsGrid.add(statCard("🍜", "Menu Items",       String.valueOf(menuItems),      UIConstants.SECONDARY_COLOR));

        // ── Quick tips panel ─────────────────────────────────────────────────
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(statsGrid, BorderLayout.NORTH);

        JPanel quickPanel = buildQuickActions();
        centerPanel.add(quickPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel statCard(String icon, String label, String value, Color color) {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                // Accent bar on top
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), 5, 5, 5);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 15, 20, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        card.add(lblIcon, gbc);

        gbc.gridy = 1;
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(color);
        card.add(lblValue, gbc);

        gbc.gridy = 2;
        JLabel lblLabel = new JLabel(label, SwingConstants.CENTER);
        lblLabel.setFont(UIConstants.SMALL_FONT);
        lblLabel.setForeground(Color.GRAY);
        card.add(lblLabel, gbc);

        return card;
    }

    private JPanel buildQuickActions() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(20, 20, 20, 20)));

        JLabel title = new JLabel("ℹ️  System Information");
        title.setFont(UIConstants.HEADER_FONT);
        title.setForeground(UIConstants.SECONDARY_COLOR);
        panel.add(title, BorderLayout.NORTH);

        String role = Session.getInstance().isAdmin() ? "Administrator" : "Regular User";
        String info = "<html><br>"
            + "<b>System:</b> Cafeteria Management System<br><br>"
            + "<b>University:</b> Mehran UET – Khairpur Campus<br><br>"
            + "<b>Course:</b> SW121 – Object Oriented Programming<br><br>"
            + "<b>Batch:</b> K25SW<br><br>"
            + "<b>Your Role:</b> " + role + "<br><br>"
            + (Session.getInstance().isAdmin()
                ? "<b>Access:</b> Full admin access – manage menu, orders, users, and reports."
                : "<b>Access:</b> You can browse the menu and place/view orders.")
            + "</html>";

        JLabel infoLabel = new JLabel(info);
        infoLabel.setFont(UIConstants.BODY_FONT);
        infoLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        panel.add(infoLabel, BorderLayout.CENTER);

        return panel;
    }
}
