package cafeteria.ui;

import cafeteria.auth.Session;
import cafeteria.dao.MenuItemDAO;
import cafeteria.dao.OrderDAO;
import cafeteria.model.MenuItem;
import cafeteria.model.Order;
import cafeteria.model.Order.OrderItem;
import cafeteria.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderPanel – Place new orders and manage existing ones.
 * Admin can see all orders and update statuses.
 * Users can place orders and view their own.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class OrderPanel extends JPanel {

    private final OrderDAO    orderDAO  = new OrderDAO();
    private final MenuItemDAO menuDAO   = new MenuItemDAO();
    private final boolean     isAdmin   = Session.getInstance().isAdmin();

    // Order list
    private JTable            orderTable;
    private DefaultTableModel orderModel;
    private JTextField        txtSearch;
    private JComboBox<String> cmbStatus;

    // Cart (new order)
    private List<OrderItem>   cart      = new ArrayList<>();
    private DefaultTableModel cartModel;
    private JLabel            lblTotal;

    public OrderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.LIGHT_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        loadOrders();
    }

    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIConstants.BODY_FONT);
        tabs.addTab("📋 Order List", buildOrderListTab());
        tabs.addTab("🛒 New Order",  buildNewOrderTab());
        add(tabs, BorderLayout.CENTER);
    }

    // ── Order List Tab ───────────────────────────────────────────────────────

    private JPanel buildOrderListTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchBar.setOpaque(false);

        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(180, 32));

        cmbStatus = new JComboBox<>(new String[]{"All","PENDING","PREPARING","READY","DELIVERED","CANCELLED"});
        cmbStatus.setPreferredSize(new Dimension(130, 32));

        JButton btnSearch  = btn("Search",  UIConstants.PRIMARY_COLOR);
        JButton btnRefresh = btn("Refresh", UIConstants.SECONDARY_COLOR);

        searchBar.add(new JLabel("Customer:")); searchBar.add(txtSearch);
        searchBar.add(new JLabel("Status:"));   searchBar.add(cmbStatus);
        searchBar.add(btnSearch); searchBar.add(btnRefresh);

        btnSearch .addActionListener(e -> loadOrders());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); cmbStatus.setSelectedIndex(0); loadOrders(); });

        // Table
        String[] cols = {"ID", "Customer", "Date", "Total (Rs)", "Status", "Notes"};
        orderModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        orderTable = new JTable(orderModel);
        orderTable.setFont(UIConstants.BODY_FONT);
        orderTable.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        orderTable.getTableHeader().setFont(UIConstants.TABLE_HEADER_FONT);
        orderTable.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        orderTable.getTableHeader().setForeground(UIConstants.WHITE);
        orderTable.setSelectionBackground(new Color(184, 218, 255));

        // Status column colour
        orderTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                String s = String.valueOf(val);
                switch (s) {
                    case "PENDING":   setForeground(UIConstants.WARNING_COLOR);  break;
                    case "PREPARING": setForeground(UIConstants.PRIMARY_COLOR);  break;
                    case "READY":     setForeground(new Color(0, 150, 200));     break;
                    case "DELIVERED": setForeground(UIConstants.SUCCESS_COLOR);  break;
                    case "CANCELLED": setForeground(UIConstants.DANGER_COLOR);   break;
                    default:          setForeground(Color.BLACK);
                }
                setFont(getFont().deriveFont(Font.BOLD));
                return this;
            }
        });

        int[] widths = {40, 160, 160, 90, 100, 200};
        for (int i = 0; i < widths.length; i++)
            orderTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(orderTable);

        // Action buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        actions.setOpaque(false);

        JButton btnView   = btn("View Items",     UIConstants.PRIMARY_COLOR);
        JButton btnStatus = btn("Update Status",  UIConstants.WARNING_COLOR);
        JButton btnDelete = btn("Delete Order",   UIConstants.DANGER_COLOR);

        btnView  .addActionListener(e -> viewOrderItems());
        btnStatus.addActionListener(e -> updateOrderStatus());
        btnDelete.addActionListener(e -> deleteOrder());

        actions.add(btnView);
        if (isAdmin) { actions.add(btnStatus); actions.add(btnDelete); }

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(scroll,    BorderLayout.CENTER);
        panel.add(actions,   BorderLayout.SOUTH);
        return panel;
    }

    private void loadOrders() {
        orderModel.setRowCount(0);
        String keyword = txtSearch.getText().trim();
        String status  = (String) cmbStatus.getSelectedItem();
        List<Order> orders = isAdmin
            ? orderDAO.searchOrders(keyword, status)
            : orderDAO.getOrdersByUser(Session.getInstance().getCurrentUser().getUserId());

        for (Order o : orders) {
            orderModel.addRow(new Object[]{
                o.getOrderId(),
                o.getCustomerName() != null ? o.getCustomerName() : "Guest",
                o.getOrderDate(),
                String.format("%.2f", o.getTotalAmount()),
                o.getStatus(),
                o.getNotes()
            });
        }
    }

    private void viewOrderItems() {
        int row = orderTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an order first."); return; }
        int orderId = (int) orderModel.getValueAt(row, 0);

        List<OrderItem> items = orderDAO.getOrderItems(orderId);
        StringBuilder sb = new StringBuilder("<html><table border='1' cellpadding='5'>");
        sb.append("<tr><th>Item</th><th>Qty</th><th>Unit Price</th><th>Subtotal</th></tr>");
        for (OrderItem oi : items) {
            sb.append("<tr><td>").append(oi.getItemName())
              .append("</td><td>").append(oi.getQuantity())
              .append("</td><td>Rs ").append(String.format("%.2f", oi.getUnitPrice()))
              .append("</td><td>Rs ").append(String.format("%.2f", oi.getSubtotal()))
              .append("</td></tr>");
        }
        sb.append("</table></html>");

        JOptionPane.showMessageDialog(this, new JLabel(sb.toString()),
            "Order #" + orderId + " – Items", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateOrderStatus() {
        int row = orderTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an order first."); return; }
        int orderId = (int) orderModel.getValueAt(row, 0);

        String[] statuses = {"PENDING","PREPARING","READY","DELIVERED","CANCELLED"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Select new status:", "Update Status",
            JOptionPane.QUESTION_MESSAGE, null, statuses, orderModel.getValueAt(row, 4));

        if (newStatus != null) {
            if (orderDAO.updateOrderStatus(orderId, newStatus)) {
                JOptionPane.showMessageDialog(this, "Status updated to " + newStatus);
                loadOrders();
            }
        }
    }

    private void deleteOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an order first."); return; }
        int orderId = (int) orderModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete order #" + orderId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (orderDAO.deleteOrder(orderId)) {
                JOptionPane.showMessageDialog(this, "Order deleted.");
                loadOrders();
            }
        }
    }

    // ── New Order Tab ────────────────────────────────────────────────────────

    private JPanel buildNewOrderTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Left: menu items to pick from
        JPanel menuList = buildMenuSelector();
        // Right: cart
        JPanel cartPanel = buildCartPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuList, cartPanel);
        split.setDividerLocation(550);
        split.setBorder(null);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildMenuSelector() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(10, 10, 10, 10)));

        JLabel title = new JLabel("Available Menu Items");
        title.setFont(UIConstants.HEADER_FONT);

        String[] cols = {"ID", "Item", "Category", "Price (Rs)"};
        DefaultTableModel menuModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable menuTable = new JTable(menuModel);
        menuTable.setFont(UIConstants.BODY_FONT);
        menuTable.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        menuTable.getTableHeader().setFont(UIConstants.TABLE_HEADER_FONT);
        menuTable.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        menuTable.getTableHeader().setForeground(UIConstants.WHITE);

        List<MenuItem> available = menuDAO.getAvailableItems();
        for (MenuItem m : available) {
            menuModel.addRow(new Object[]{
                m.getItemId(), m.getItemName(), m.getCategoryName(),
                String.format("%.2f", m.getPrice())
            });
        }

        JScrollPane scroll = new JScrollPane(menuTable);

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        addRow.setOpaque(false);
        JSpinner spinQty = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        spinQty.setPreferredSize(new Dimension(60, 30));
        JButton btnAddToCart = btn("Add to Cart ➜", UIConstants.SUCCESS_COLOR);

        btnAddToCart.addActionListener(e -> {
            int row = menuTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "Select an item."); return; }
            int itemId   = (int) menuModel.getValueAt(row, 0);
            String name  = (String) menuModel.getValueAt(row, 1);
            double price = Double.parseDouble((String) menuModel.getValueAt(row, 3));
            int qty      = (int) spinQty.getValue();

            // Check if already in cart, update qty
            for (OrderItem oi : cart) {
                if (oi.getItemId() == itemId) {
                    oi.setQuantity(oi.getQuantity() + qty);
                    refreshCart();
                    return;
                }
            }
            cart.add(new OrderItem(itemId, name, qty, price));
            refreshCart();
        });

        addRow.add(new JLabel("Qty:")); addRow.add(spinQty); addRow.add(btnAddToCart);

        panel.add(title,  BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(addRow, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(10, 10, 10, 10)));

        JLabel title = new JLabel("🛒 Cart");
        title.setFont(UIConstants.HEADER_FONT);

        String[] cols = {"Item", "Qty", "Price", "Subtotal"};
        cartModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable cartTable = new JTable(cartModel);
        cartTable.setFont(UIConstants.BODY_FONT);
        cartTable.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        JScrollPane scroll = new JScrollPane(cartTable);

        lblTotal = new JLabel("Total: Rs 0.00", SwingConstants.RIGHT);
        lblTotal.setFont(UIConstants.HEADER_FONT);
        lblTotal.setForeground(UIConstants.SUCCESS_COLOR);

        JTextField txtNotes = new JTextField();
        txtNotes.setBorder(BorderFactory.createTitledBorder("Notes (optional)"));

        JPanel bottom = new JPanel(new BorderLayout(0, 5));
        bottom.setOpaque(false);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        JButton btnRemove = btn("Remove Selected", UIConstants.DANGER_COLOR);
        JButton btnClear  = btn("Clear Cart",      UIConstants.WARNING_COLOR);
        JButton btnPlace  = btn("✔ Place Order",   UIConstants.SUCCESS_COLOR);

        btnRemove.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row >= 0) { cart.remove(row); refreshCart(); }
        });
        btnClear.addActionListener(e -> { cart.clear(); refreshCart(); });
        btnPlace.addActionListener(e -> placeOrder(txtNotes.getText().trim()));

        btnRow.add(btnRemove); btnRow.add(btnClear); btnRow.add(btnPlace);

        bottom.add(lblTotal,  BorderLayout.NORTH);
        bottom.add(txtNotes,  BorderLayout.CENTER);
        bottom.add(btnRow,    BorderLayout.SOUTH);

        panel.add(title,  BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshCart() {
        cartModel.setRowCount(0);
        double total = 0;
        for (OrderItem oi : cart) {
            cartModel.addRow(new Object[]{
                oi.getItemName(), oi.getQuantity(),
                String.format("%.2f", oi.getUnitPrice()),
                String.format("%.2f", oi.getSubtotal())
            });
            total += oi.getSubtotal();
        }
        lblTotal.setText("Total: Rs " + String.format("%.2f", total));
    }

    private void placeOrder(String notes) {
        if (cart.isEmpty()) { JOptionPane.showMessageDialog(this, "Cart is empty!"); return; }
        double total = cart.stream().mapToDouble(OrderItem::getSubtotal).sum();

        Order order = new Order();
        order.setUserId(Session.getInstance().getCurrentUser().getUserId());
        order.setTotalAmount(total);
        order.setStatus("PENDING");
        order.setNotes(notes);
        order.setItems(cart);

        int orderId = orderDAO.createOrder(order);
        if (orderId > 0) {
            JOptionPane.showMessageDialog(this,
                "✔ Order placed successfully!\nOrder ID: #" + orderId
                + "\nTotal: Rs " + String.format("%.2f", total),
                "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
            cart.clear();
            refreshCart();
            loadOrders();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to place order.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private JButton btn(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(UIConstants.BUTTON_FONT);
        b.setBackground(color);
        b.setForeground(UIConstants.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
