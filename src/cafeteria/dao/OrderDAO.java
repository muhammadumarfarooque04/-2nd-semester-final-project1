package cafeteria.dao;

import cafeteria.model.Order;
import cafeteria.model.Order.OrderItem;
import cafeteria.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderDAO – DAO for all order-related database operations.
 * Handles order creation with items, status updates, and reporting queries.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class OrderDAO {

    /**
     * Creates a new order with all its items in a single transaction.
     *
     * @param order the order to insert (with items list populated)
     * @return generated order ID, or -1 on failure
     */
    public int createOrder(Order order) {
        String orderSql = "INSERT INTO orders (user_id, total_amount, status, notes) VALUES (?,?,?,?)";
        String itemSql  = "INSERT INTO order_items (order_id, item_id, quantity, unit_price, subtotal) "
                        + "VALUES (?,?,?,?,?)";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("[OrderDAO] Database not available - demo mode");
            return -1;
        }
        try {
            conn.setAutoCommit(false);

            // Insert order header
            PreparedStatement orderPs = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderPs.setInt   (1, order.getUserId());
            orderPs.setDouble(2, order.getTotalAmount());
            orderPs.setString(3, order.getStatus());
            orderPs.setString(4, order.getNotes());
            orderPs.executeUpdate();

            ResultSet keys = orderPs.getGeneratedKeys();
            if (!keys.next()) { conn.rollback(); return -1; }
            int orderId = keys.getInt(1);

            // Insert order items
            PreparedStatement itemPs = conn.prepareStatement(itemSql);
            for (OrderItem item : order.getItems()) {
                itemPs.setInt   (1, orderId);
                itemPs.setInt   (2, item.getItemId());
                itemPs.setInt   (3, item.getQuantity());
                itemPs.setDouble(4, item.getUnitPrice());
                itemPs.setDouble(5, item.getSubtotal());
                itemPs.addBatch();
            }
            itemPs.executeBatch();
            conn.commit();
            return orderId;

        } catch (SQLException e) {
            System.err.println("[OrderDAO] createOrder: " + e.getMessage());
            try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            return -1;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
        }
    }

    /** Returns all orders with customer names (joined). */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name AS customer_name FROM orders o "
                   + "LEFT JOIN users u ON o.user_id = u.user_id "
                   + "ORDER BY o.order_date DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[OrderDAO] Database not available - returning empty list");
                return orders;
            }
            try (Statement  st   = conn.createStatement();
                 ResultSet  rs   = st.executeQuery(sql)) {

                while (rs.next()) orders.add(extractOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] getAllOrders: " + e.getMessage());
        }
        return orders;
    }

    /** Returns orders for a specific user. */
    public List<Order> getOrdersByUser(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name AS customer_name FROM orders o "
                   + "LEFT JOIN users u ON o.user_id = u.user_id "
                   + "WHERE o.user_id = ? ORDER BY o.order_date DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[OrderDAO] Database not available - returning empty list");
                return orders;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) orders.add(extractOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] getOrdersByUser: " + e.getMessage());
        }
        return orders;
    }

    /** Returns the items of a specific order. */
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, m.item_name FROM order_items oi "
                   + "JOIN menu_items m ON oi.item_id = m.item_id "
                   + "WHERE oi.order_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[OrderDAO] Database not available - returning empty list");
                return items;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, orderId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    OrderItem item = new OrderItem(
                        rs.getInt   ("item_id"),
                        rs.getString("item_name"),
                        rs.getInt   ("quantity"),
                        rs.getDouble("unit_price")
                    );
                    item.setOrderItemId(rs.getInt   ("order_item_id"));
                    item.setOrderId    (rs.getInt   ("order_id"));
                    item.setSubtotal   (rs.getDouble("subtotal"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] getOrderItems: " + e.getMessage());
        }
        return items;
    }

    /** Updates the status of an order. */
    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[OrderDAO] Database not available - demo mode");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, status);
                ps.setInt   (2, orderId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] updateOrderStatus: " + e.getMessage());
            return false;
        }
    }

    /** Deletes an order (cascades to order_items). */
    public boolean deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[OrderDAO] Database not available - demo mode");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, orderId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] deleteOrder: " + e.getMessage());
            return false;
        }
    }

    /** Searches orders by customer name or status. */
    public List<Order> searchOrders(String keyword, String statusFilter) {
        List<Order> orders = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT o.*, u.full_name AS customer_name FROM orders o "
          + "LEFT JOIN users u ON o.user_id = u.user_id WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty())
            sql.append(" AND u.full_name LIKE ?");
        if (statusFilter != null && !statusFilter.equals("All"))
            sql.append(" AND o.status = ?");
        sql.append(" ORDER BY o.order_date DESC");

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[OrderDAO] Database not available - returning empty list");
                return orders;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {

                int idx = 1;
                if (keyword != null && !keyword.trim().isEmpty())
                    ps.setString(idx++, "%" + keyword + "%");
                if (statusFilter != null && !statusFilter.equals("All"))
                    ps.setString(idx, statusFilter);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) orders.add(extractOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] searchOrders: " + e.getMessage());
        }
        return orders;
    }

    // ── Report Queries ───────────────────────────────────────────────────────

    /** Returns total revenue (sum of delivered orders). */
    public double getTotalRevenue() {
        String sql = "SELECT IFNULL(SUM(total_amount),0) FROM orders WHERE status='DELIVERED'";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[OrderDAO] Database not available - returning 0");
                return 0;
            }
            try (Statement  st   = conn.createStatement();
                 ResultSet  rs   = st.executeQuery(sql)) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] getTotalRevenue: " + e.getMessage());
        }
        return 0;
    }

    /** Returns count of today's orders. */
    public int getTodayOrderCount() {
        String sql = "SELECT COUNT(*) FROM orders WHERE DATE(order_date) = CURDATE()";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[OrderDAO] Database not available - returning 0");
                return 0;
            }
            try (Statement  st   = conn.createStatement();
                 ResultSet  rs   = st.executeQuery(sql)) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] getTodayOrderCount: " + e.getMessage());
        }
        return 0;
    }

    /** Returns count of pending orders. */
    public int getPendingOrderCount() {
        String sql = "SELECT COUNT(*) FROM orders WHERE status IN ('PENDING','PREPARING')";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[OrderDAO] Database not available - returning 0");
                return 0;
            }
            try (Statement  st   = conn.createStatement();
                 ResultSet  rs   = st.executeQuery(sql)) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] getPendingOrderCount: " + e.getMessage());
        }
        return 0;
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private Order extractOrder(ResultSet rs) throws SQLException {
        return new Order(
            rs.getInt      ("order_id"),
            rs.getInt      ("user_id"),
            rs.getString   ("customer_name"),
            rs.getTimestamp("order_date"),
            rs.getDouble   ("total_amount"),
            rs.getString   ("status"),
            rs.getString   ("notes")
        );
    }
}
