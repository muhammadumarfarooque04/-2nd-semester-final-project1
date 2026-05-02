package cafeteria.ui;

import cafeteria.dao.OrderDAO;
import cafeteria.util.DatabaseConnection;
import cafeteria.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * ReportPanel – Sales summary and revenue analytics for Admin.
 * Provides daily, monthly, and top-selling item reports.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class ReportPanel extends JPanel {

    private final OrderDAO orderDAO = new OrderDAO();

    public ReportPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.LIGHT_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
    }

    private void buildUI() {
        JLabel title = new JLabel("📈 Reports & Analytics");
        title.setFont(UIConstants.TITLE_FONT);
        title.setForeground(UIConstants.SECONDARY_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIConstants.BODY_FONT);
        tabs.addTab("📊 Summary",        buildSummaryTab());
        tabs.addTab("📅 Daily Orders",   buildDailyTab());
        tabs.addTab("🏆 Top Items",      buildTopItemsTab());
        tabs.addTab("👤 Orders by User", buildUserOrdersTab());
        add(tabs, BorderLayout.CENTER);
    }

    // ── Summary Tab ──────────────────────────────────────────────────────────

    private JPanel buildSummaryTab() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        double revenue       = orderDAO.getTotalRevenue();
        int    todayOrders   = orderDAO.getTodayOrderCount();
        int    pendingOrders = orderDAO.getPendingOrderCount();
        int    totalOrders   = getTotalOrderCount();

        panel.add(summaryCard("💰 Total Revenue",     String.format("Rs %.2f", revenue),     UIConstants.SUCCESS_COLOR));
        panel.add(summaryCard("📋 Total Orders",      String.valueOf(totalOrders),             UIConstants.PRIMARY_COLOR));
        panel.add(summaryCard("📅 Today's Orders",    String.valueOf(todayOrders),             UIConstants.WARNING_COLOR));
        panel.add(summaryCard("⏳ Pending/Preparing", String.valueOf(pendingOrders),            UIConstants.DANGER_COLOR));

        return panel;
    }

    private JPanel summaryCard(String label, String value, Color color) {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, 6, getHeight(), 6, 6);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 25, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblVal.setForeground(color);

        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(UIConstants.BODY_FONT);
        lblLbl.setForeground(Color.GRAY);

        card.add(lblVal, gbc);
        gbc.gridy = 1; card.add(lblLbl, gbc);
        return card;
    }

    // ── Daily Orders Tab ─────────────────────────────────────────────────────

    private JPanel buildDailyTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        String[] cols = {"Date", "Total Orders", "Total Revenue (Rs)", "Avg Order Value (Rs)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = "SELECT DATE(order_date) AS day, COUNT(*) AS cnt, "
                   + "SUM(total_amount) AS total, AVG(total_amount) AS avg_val "
                   + "FROM orders WHERE status != 'CANCELLED' "
                   + "GROUP BY DATE(order_date) ORDER BY day DESC LIMIT 30";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                try (Statement  st   = conn.createStatement();
                     ResultSet  rs   = st.executeQuery(sql)) {

                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getDate("day"),
                            rs.getInt  ("cnt"),
                            String.format("%.2f", rs.getDouble("total")),
                            String.format("%.2f", rs.getDouble("avg_val"))
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportPanel] daily: " + e.getMessage());
        }

        JTable table = styledTable(model);
        panel.add(new JLabel("  Last 30 Days of Orders"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ── Top Items Tab ────────────────────────────────────────────────────────

    private JPanel buildTopItemsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        String[] cols = {"Rank", "Item Name", "Category", "Total Sold", "Revenue (Rs)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = "SELECT m.item_name, c.category_name, "
                   + "SUM(oi.quantity) AS total_sold, SUM(oi.subtotal) AS revenue "
                   + "FROM order_items oi "
                   + "JOIN menu_items m ON oi.item_id = m.item_id "
                   + "JOIN categories c ON m.category_id = c.category_id "
                   + "GROUP BY m.item_id ORDER BY total_sold DESC LIMIT 15";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                try (Statement  st   = conn.createStatement();
                     ResultSet  rs   = st.executeQuery(sql)) {

                    int rank = 1;
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rank++,
                            rs.getString("item_name"),
                            rs.getString("category_name"),
                            rs.getInt   ("total_sold"),
                            String.format("%.2f", rs.getDouble("revenue"))
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportPanel] topItems: " + e.getMessage());
        }

        JTable table = styledTable(model);
        panel.add(new JLabel("  Top 15 Best-Selling Items"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ── Orders by User Tab ───────────────────────────────────────────────────

    private JPanel buildUserOrdersTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        String[] cols = {"User ID", "Full Name", "Total Orders", "Total Spent (Rs)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = "SELECT u.user_id, u.full_name, COUNT(o.order_id) AS order_cnt, "
                   + "SUM(o.total_amount) AS total_spent "
                   + "FROM users u LEFT JOIN orders o ON u.user_id = o.user_id "
                   + "GROUP BY u.user_id ORDER BY total_spent DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                try (Statement  st   = conn.createStatement();
                     ResultSet  rs   = st.executeQuery(sql)) {

                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt   ("user_id"),
                            rs.getString("full_name"),
                            rs.getInt   ("order_cnt"),
                            String.format("%.2f", rs.getDouble("total_spent"))
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportPanel] userOrders: " + e.getMessage());
        }

        JTable table = styledTable(model);
        panel.add(new JLabel("  Spending Per User"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(UIConstants.BODY_FONT);
        t.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        t.getTableHeader().setFont(UIConstants.TABLE_HEADER_FONT);
        t.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        t.getTableHeader().setForeground(UIConstants.WHITE);
        t.setGridColor(new Color(230, 230, 230));
        t.setSelectionBackground(new Color(184, 218, 255));
        return t;
    }

    private int getTotalOrderCount() {
        String sql = "SELECT COUNT(*) FROM orders";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                try (Statement  st   = conn.createStatement();
                     ResultSet  rs   = st.executeQuery(sql)) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportPanel] totalOrders: " + e.getMessage());
        }
        return 0;
    }
}
