package cafeteria.ui;

import cafeteria.auth.Session;
import cafeteria.dao.MenuItemDAO;
import cafeteria.model.Category;
import cafeteria.model.MenuItem;
import cafeteria.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * MenuPanel – CRUD interface for menu items and categories.
 * Admin: full CRUD. User: read-only browsing.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class MenuPanel extends JPanel {

    private final MenuItemDAO  menuDAO  = new MenuItemDAO();
    private final boolean      isAdmin  = Session.getInstance().isAdmin();

    private JTable             table;
    private DefaultTableModel  tableModel;
    private JTextField         txtSearch;
    private JComboBox<String>  cmbCategory;
    private JLabel             lblCount;

    public MenuPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.LIGHT_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        loadData("", "All");
    }

    private void buildUI() {
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        if (isAdmin) add(buildFormPanel(), BorderLayout.EAST);
    }

    // ── Header ───────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("🍜 Menu Items");
        title.setFont(UIConstants.TITLE_FONT);
        title.setForeground(UIConstants.SECONDARY_COLOR);

        // Search row
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setOpaque(false);

        txtSearch = new JTextField(18);
        txtSearch.setFont(UIConstants.BODY_FONT);
        txtSearch.setPreferredSize(new Dimension(200, 35));

        cmbCategory = new JComboBox<>();
        cmbCategory.addItem("All");
        menuDAO.getAllCategories().forEach(c -> cmbCategory.addItem(c.getCategoryName()));
        cmbCategory.setPreferredSize(new Dimension(140, 35));

        JButton btnSearch = styledButton("Search", UIConstants.PRIMARY_COLOR);
        JButton btnRefresh = styledButton("Refresh", UIConstants.SECONDARY_COLOR);

        lblCount = new JLabel("Items: 0");
        lblCount.setFont(UIConstants.SMALL_FONT);
        lblCount.setForeground(Color.GRAY);

        searchRow.add(new JLabel("Search:"));
        searchRow.add(txtSearch);
        searchRow.add(new JLabel("Category:"));
        searchRow.add(cmbCategory);
        searchRow.add(btnSearch);
        searchRow.add(btnRefresh);
        searchRow.add(lblCount);

        btnSearch.addActionListener(e -> loadData(txtSearch.getText(), (String) cmbCategory.getSelectedItem()));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); cmbCategory.setSelectedIndex(0); loadData("", "All"); });
        txtSearch.addActionListener(e -> loadData(txtSearch.getText(), (String) cmbCategory.getSelectedItem()));

        panel.add(title,     BorderLayout.NORTH);
        panel.add(searchRow, BorderLayout.SOUTH);
        return panel;
    }

    // ── Table ────────────────────────────────────────────────────────────────

    private JScrollPane buildTable() {
        String[] columns = {"ID", "Item Name", "Category", "Price (Rs)", "Available", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(UIConstants.BODY_FONT);
        table.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        table.getTableHeader().setFont(UIConstants.TABLE_HEADER_FONT);
        table.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        table.getTableHeader().setForeground(UIConstants.WHITE);
        table.setSelectionBackground(new Color(184, 218, 255));
        table.setGridColor(new Color(230, 230, 230));
        table.setIntercellSpacing(new Dimension(5, 2));

        // Column widths
        int[] widths = {40, 180, 120, 90, 80, 250};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Colour "Available" column
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                boolean available = "Yes".equals(val);
                setText(available ? "✔ Yes" : "✘ No");
                setForeground(available ? UIConstants.SUCCESS_COLOR : UIConstants.DANGER_COLOR);
                setHorizontalAlignment(CENTER);
                return this;
            }
        });

        // Double-click to edit (admin only)
        if (isAdmin) {
            table.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) openEditDialog();
                }
            });
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        return scroll;
    }

    // ── Side Form Panel (admin only) ─────────────────────────────────────────

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(230, 0));
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)));

        JLabel title = new JLabel("Admin Actions");
        title.setFont(UIConstants.HEADER_FONT);
        title.setForeground(UIConstants.SECONDARY_COLOR);

        JPanel btnPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnAdd    = styledButton("➕ Add Item",      UIConstants.SUCCESS_COLOR);
        JButton btnEdit   = styledButton("✏ Edit Item",     UIConstants.PRIMARY_COLOR);
        JButton btnDelete = styledButton("🗑 Delete Item",   UIConstants.DANGER_COLOR);
        JButton btnCats   = styledButton("📁 Categories",    UIConstants.WARNING_COLOR);

        btnAdd   .addActionListener(e -> openAddDialog());
        btnEdit  .addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnCats  .addActionListener(e -> openCategoryDialog());

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnCats);

        panel.add(title,    BorderLayout.NORTH);
        panel.add(btnPanel, BorderLayout.CENTER);
        return panel;
    }

    // ── CRUD dialogs ─────────────────────────────────────────────────────────

    private void openAddDialog() {
        showItemDialog(null);
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select an item to edit."); return; }
        int itemId = (int) tableModel.getValueAt(row, 0);
        // Find the MenuItem from current data
        List<MenuItem> items = menuDAO.searchMenuItems(txtSearch.getText(),
                                                        (String) cmbCategory.getSelectedItem());
        MenuItem item = items.stream().filter(m -> m.getItemId() == itemId).findFirst().orElse(null);
        showItemDialog(item);
    }

    private void showItemDialog(MenuItem existing) {
        boolean isEdit = (existing != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                     isEdit ? "Edit Item" : "Add New Item", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 2;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Item Name*:"), gbc);
        JTextField txtName = new JTextField(isEdit ? existing.getItemName() : "");
        gbc.gridy = 1; form.add(txtName, gbc);

        // Category
        gbc.gridy = 2; form.add(new JLabel("Category*:"), gbc);
        JComboBox<Category> cmbCat = new JComboBox<>();
        menuDAO.getAllCategories().forEach(cmbCat::addItem);
        if (isEdit) {
            for (int i = 0; i < cmbCat.getItemCount(); i++)
                if (cmbCat.getItemAt(i).getCategoryId() == existing.getCategoryId())
                    cmbCat.setSelectedIndex(i);
        }
        gbc.gridy = 3; form.add(cmbCat, gbc);

        // Price
        gbc.gridy = 4; form.add(new JLabel("Price (Rs)*:"), gbc);
        JTextField txtPrice = new JTextField(isEdit ? String.valueOf(existing.getPrice()) : "");
        gbc.gridy = 5; form.add(txtPrice, gbc);

        // Description
        gbc.gridy = 6; form.add(new JLabel("Description:"), gbc);
        JTextField txtDesc = new JTextField(isEdit ? existing.getDescription() : "");
        gbc.gridy = 7; form.add(txtDesc, gbc);

        // Available
        gbc.gridy = 8;
        JCheckBox chkAvail = new JCheckBox("Available", !isEdit || existing.isAvailable());
        form.add(chkAvail, gbc);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave   = styledButton(isEdit ? "Update" : "Save", UIConstants.SUCCESS_COLOR);
        JButton btnCancel = styledButton("Cancel", Color.GRAY);
        btnRow.add(btnCancel);
        btnRow.add(btnSave);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            // Validation
            if (txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Item name is required."); return;
            }
            double price;
            try { price = Double.parseDouble(txtPrice.getText().trim()); }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid price."); return;
            }
            if (price <= 0) { JOptionPane.showMessageDialog(dialog, "Price must be positive."); return; }

            Category selCat = (Category) cmbCat.getSelectedItem();
            MenuItem item = isEdit ? existing : new MenuItem();
            item.setItemName  (txtName.getText().trim());
            item.setCategoryId(selCat.getCategoryId());
            item.setPrice     (price);
            item.setDescription(txtDesc.getText().trim());
            item.setAvailable (chkAvail.isSelected());

            boolean ok = isEdit ? menuDAO.updateMenuItem(item) : menuDAO.addMenuItem(item);
            if (ok) {
                JOptionPane.showMessageDialog(dialog, isEdit ? "Item updated!" : "Item added!");
                dialog.dispose();
                loadData(txtSearch.getText(), (String) cmbCategory.getSelectedItem());
            } else {
                JOptionPane.showMessageDialog(dialog, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(form,   BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select an item to delete."); return; }
        int itemId   = (int) tableModel.getValueAt(row, 0);
        String name  = (String) tableModel.getValueAt(row, 1);
        int confirm  = JOptionPane.showConfirmDialog(this,
            "Delete \"" + name + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (menuDAO.deleteMenuItem(itemId)) {
                JOptionPane.showMessageDialog(this, "Item deleted.");
                loadData(txtSearch.getText(), (String) cmbCategory.getSelectedItem());
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openCategoryDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                     "Manage Categories", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));

        // List of categories
        DefaultListModel<Category> listModel = new DefaultListModel<>();
        menuDAO.getAllCategories().forEach(listModel::addElement);
        JList<Category> listCats = new JList<>(listModel);
        listCats.setFont(UIConstants.BODY_FONT);

        JScrollPane scroll = new JScrollPane(listCats);

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtCatName = new JTextField(12);
        JTextField txtCatDesc = new JTextField(12);
        JButton btnAddCat = styledButton("Add", UIConstants.SUCCESS_COLOR);
        JButton btnDelCat = styledButton("Delete Selected", UIConstants.DANGER_COLOR);

        addRow.add(new JLabel("Name:")); addRow.add(txtCatName);
        addRow.add(new JLabel("Desc:")); addRow.add(txtCatDesc);
        addRow.add(btnAddCat);

        btnAddCat.addActionListener(e -> {
            if (txtCatName.getText().trim().isEmpty()) return;
            Category cat = new Category(0, txtCatName.getText().trim(), txtCatDesc.getText().trim());
            if (menuDAO.addCategory(cat)) {
                listModel.clear();
                menuDAO.getAllCategories().forEach(listModel::addElement);
                txtCatName.setText(""); txtCatDesc.setText("");
                // Refresh category combo in main panel
                cmbCategory.removeAllItems();
                cmbCategory.addItem("All");
                menuDAO.getAllCategories().forEach(c -> cmbCategory.addItem(c.getCategoryName()));
            }
        });

        btnDelCat.addActionListener(e -> {
            Category sel = listCats.getSelectedValue();
            if (sel == null) return;
            if (menuDAO.deleteCategory(sel.getCategoryId())) {
                listModel.remove(listCats.getSelectedIndex());
                cmbCategory.removeItem(sel.getCategoryName());
            }
        });

        dialog.add(scroll,  BorderLayout.CENTER);
        dialog.add(addRow,  BorderLayout.NORTH);
        dialog.add(btnDelCat, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ── Data loading ─────────────────────────────────────────────────────────

    private void loadData(String keyword, String category) {
        tableModel.setRowCount(0);
        List<MenuItem> items = menuDAO.searchMenuItems(keyword, category);
        for (MenuItem m : items) {
            tableModel.addRow(new Object[]{
                m.getItemId(), m.getItemName(), m.getCategoryName(),
                String.format("%.2f", m.getPrice()),
                m.isAvailable() ? "Yes" : "No",
                m.getDescription()
            });
        }
        lblCount.setText("Items: " + items.size());
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private JButton styledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.BUTTON_FONT);
        btn.setBackground(color);
        btn.setForeground(UIConstants.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
