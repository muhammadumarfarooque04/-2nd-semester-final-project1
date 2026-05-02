package cafeteria.dao;

import cafeteria.model.Category;
import cafeteria.model.MenuItem;
import cafeteria.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MenuItemDAO – DAO for CRUD operations on menu items and categories.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class MenuItemDAO {

    // ── Menu Items ───────────────────────────────────────────────────────────

    /** Returns all menu items joined with their category name. */
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT m.*, c.category_name FROM menu_items m "
                   + "LEFT JOIN categories c ON m.category_id = c.category_id "
                   + "ORDER BY m.item_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[MenuItemDAO] Database not available - returning empty list");
                return items;
            }
            try (Statement  st   = conn.createStatement();
                 ResultSet  rs   = st.executeQuery(sql)) {

                while (rs.next()) items.add(extractItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MenuItemDAO] getAllMenuItems: " + e.getMessage());
        }
        return items;
    }

    /** Returns only available menu items. */
    public List<MenuItem> getAvailableItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT m.*, c.category_name FROM menu_items m "
                   + "LEFT JOIN categories c ON m.category_id = c.category_id "
                   + "WHERE m.is_available = TRUE ORDER BY c.category_name, m.item_name";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[MenuItemDAO] Database not available - returning empty list");
                return items;
            }
            try (Statement  st   = conn.createStatement();
                 ResultSet  rs   = st.executeQuery(sql)) {

                while (rs.next()) items.add(extractItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MenuItemDAO] getAvailableItems: " + e.getMessage());
        }
        return items;
    }

    /** Inserts a new menu item. */
    public boolean addMenuItem(MenuItem item) {
        String sql = "INSERT INTO menu_items (item_name, category_id, price, description, is_available) "
                   + "VALUES (?,?,?,?,?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[MenuItemDAO] Database not available - demo mode");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString (1, item.getItemName());
                ps.setInt    (2, item.getCategoryId());
                ps.setDouble (3, item.getPrice());
                ps.setString (4, item.getDescription());
                ps.setBoolean(5, item.isAvailable());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[MenuItemDAO] addMenuItem: " + e.getMessage());
            return false;
        }
    }

    /** Updates an existing menu item. */
    public boolean updateMenuItem(MenuItem item) {
        String sql = "UPDATE menu_items SET item_name=?, category_id=?, price=?, "
                   + "description=?, is_available=? WHERE item_id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[MenuItemDAO] Database not available - demo mode");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString (1, item.getItemName());
                ps.setInt    (2, item.getCategoryId());
                ps.setDouble (3, item.getPrice());
                ps.setString (4, item.getDescription());
                ps.setBoolean(5, item.isAvailable());
                ps.setInt    (6, item.getItemId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[MenuItemDAO] updateMenuItem: " + e.getMessage());
            return false;
        }
    }

    /** Deletes a menu item by ID. */
    public boolean deleteMenuItem(int itemId) {
        String sql = "DELETE FROM menu_items WHERE item_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[MenuItemDAO] Database not available - demo mode");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, itemId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[MenuItemDAO] deleteMenuItem: " + e.getMessage());
            return false;
        }
    }

    /** Searches menu items by name or category. */
    public List<MenuItem> searchMenuItems(String keyword, String categoryFilter) {
        List<MenuItem> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT m.*, c.category_name FROM menu_items m "
          + "LEFT JOIN categories c ON m.category_id = c.category_id WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty())
            sql.append(" AND m.item_name LIKE ?");
        if (categoryFilter != null && !categoryFilter.equals("All"))
            sql.append(" AND c.category_name = ?");

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[MenuItemDAO] Database not available - returning empty list");
                return items;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {

                int idx = 1;
                if (keyword != null && !keyword.trim().isEmpty())
                    ps.setString(idx++, "%" + keyword + "%");
                if (categoryFilter != null && !categoryFilter.equals("All"))
                    ps.setString(idx, categoryFilter);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) items.add(extractItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MenuItemDAO] searchMenuItems: " + e.getMessage());
        }
        return items;
    }

    // ── Categories ───────────────────────────────────────────────────────────

    public List<Category> getAllCategories() {
        List<Category> cats = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY category_name";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[MenuItemDAO] Database not available - returning empty list");
                return cats;
            }
            try (Statement  st   = conn.createStatement();
                 ResultSet  rs   = st.executeQuery(sql)) {

                while (rs.next())
                    cats.add(new Category(rs.getInt("category_id"),
                                          rs.getString("category_name"),
                                          rs.getString("description")));
            }
        } catch (SQLException e) {
            System.err.println("[MenuItemDAO] getAllCategories: " + e.getMessage());
        }
        return cats;
    }

    public boolean addCategory(Category cat) {
        String sql = "INSERT INTO categories (category_name, description) VALUES (?,?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("[MenuItemDAO] Database not available - demo mode");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, cat.getCategoryName());
                ps.setString(2, cat.getDescription());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[MenuItemDAO] addCategory: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[MenuItemDAO] deleteCategory: " + e.getMessage());
            return false;
        }
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private MenuItem extractItem(ResultSet rs) throws SQLException {
        return new MenuItem(
            rs.getInt    ("item_id"),
            rs.getString ("item_name"),
            rs.getInt    ("category_id"),
            rs.getString ("category_name"),
            rs.getDouble ("price"),
            rs.getString ("description"),
            rs.getBoolean("is_available")
        );
    }
}
