package cafeteria.ui;

import cafeteria.dao.UserDAO;
import cafeteria.model.User;
import cafeteria.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * UserManagementPanel – Admin-only CRUD panel for managing system users.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class UserManagementPanel extends JPanel {

    private final UserDAO         userDAO    = new UserDAO();
    private JTable                table;
    private DefaultTableModel     tableModel;
    private JTextField            txtSearch;

    public UserManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.LIGHT_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        loadUsers("");
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel title = new JLabel("👥 User Management");
        title.setFont(UIConstants.TITLE_FONT);
        title.setForeground(UIConstants.SECONDARY_COLOR);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setOpaque(false);
        txtSearch = new JTextField(18);
        txtSearch.setPreferredSize(new Dimension(200, 35));
        JButton btnSearch  = btn("Search",  UIConstants.PRIMARY_COLOR);
        JButton btnRefresh = btn("Refresh", UIConstants.SECONDARY_COLOR);
        searchRow.add(new JLabel("Search:"));
        searchRow.add(txtSearch);
        searchRow.add(btnSearch);
        searchRow.add(btnRefresh);

        btnSearch .addActionListener(e -> loadUsers(txtSearch.getText()));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadUsers(""); });
        txtSearch .addActionListener(e -> loadUsers(txtSearch.getText()));

        header.add(title,     BorderLayout.NORTH);
        header.add(searchRow, BorderLayout.SOUTH);

        // Table
        String[] cols = {"ID", "Username", "Full Name", "Role", "Email", "Active"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(UIConstants.BODY_FONT);
        table.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        table.getTableHeader().setFont(UIConstants.TABLE_HEADER_FONT);
        table.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        table.getTableHeader().setForeground(UIConstants.WHITE);
        table.setSelectionBackground(new Color(184, 218, 255));

        int[] widths = {40, 120, 180, 80, 200, 60};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) openEditDialog();
            }
        });

        JScrollPane scroll = new JScrollPane(table);

        // Action buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        actions.setOpaque(false);

        JButton btnAdd    = btn("➕ Add User",        UIConstants.SUCCESS_COLOR);
        JButton btnEdit   = btn("✏ Edit User",        UIConstants.PRIMARY_COLOR);
        JButton btnDelete = btn("🗑 Delete User",      UIConstants.DANGER_COLOR);
        JButton btnPass   = btn("🔑 Change Password",  UIConstants.WARNING_COLOR);

        btnAdd   .addActionListener(e -> openAddDialog());
        btnEdit  .addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteUser());
        btnPass  .addActionListener(e -> changePassword());

        actions.add(btnAdd); actions.add(btnEdit);
        actions.add(btnDelete); actions.add(btnPass);

        add(header,  BorderLayout.NORTH);
        add(scroll,  BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
    }

    private void loadUsers(String keyword) {
        tableModel.setRowCount(0);
        List<User> users = keyword.isEmpty() ? userDAO.getAllUsers() : userDAO.searchUsers(keyword);
        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.getUserId(), u.getUsername(), u.getFullName(),
                u.getRole(), u.getEmail(), u.isActive() ? "Yes" : "No"
            });
        }
    }

    private void openAddDialog() { showUserDialog(null); }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        int userId = (int) tableModel.getValueAt(row, 0);
        userDAO.getAllUsers().stream()
            .filter(u -> u.getUserId() == userId)
            .findFirst()
            .ifPresent(this::showUserDialog);
    }

    private void showUserDialog(User existing) {
        boolean isEdit = (existing != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                     isEdit ? "Edit User" : "Add User", true);
        dialog.setSize(380, 370);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 2;

        JTextField txtUsername = field(isEdit ? existing.getUsername() : "");
        JTextField txtFullName = field(isEdit ? existing.getFullName()  : "");
        JTextField txtEmail    = field(isEdit ? existing.getEmail()     : "");
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"USER","ADMIN"});
        if (isEdit && "ADMIN".equals(existing.getRole())) cmbRole.setSelectedIndex(1);
        JCheckBox chkActive = new JCheckBox("Active", !isEdit || existing.isActive());
        JPasswordField txtPass = new JPasswordField(isEdit ? existing.getPassword() : "");

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y++; form.add(new JLabel("Username*:"), gbc);
        gbc.gridy = y++; form.add(txtUsername, gbc);
        gbc.gridy = y++; form.add(new JLabel("Full Name*:"), gbc);
        gbc.gridy = y++; form.add(txtFullName, gbc);
        gbc.gridy = y++; form.add(new JLabel("Password*:"), gbc);
        gbc.gridy = y++; form.add(txtPass, gbc);
        gbc.gridy = y++; form.add(new JLabel("Email:"), gbc);
        gbc.gridy = y++; form.add(txtEmail, gbc);
        gbc.gridy = y++; form.add(new JLabel("Role:"), gbc);
        gbc.gridy = y++; form.add(cmbRole, gbc);
        gbc.gridy = y++;  form.add(chkActive, gbc);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave   = btn(isEdit ? "Update" : "Save", UIConstants.SUCCESS_COLOR);
        JButton btnCancel = btn("Cancel", Color.GRAY);
        btnRow.add(btnCancel);
        btnRow.add(btnSave);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            if (txtUsername.getText().trim().isEmpty() || txtFullName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username and Full Name are required."); return;
            }
            if (new String(txtPass.getPassword()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Password is required."); return;
            }
            User user = isEdit ? existing : new User();
            user.setUsername(txtUsername.getText().trim());
            user.setPassword(new String(txtPass.getPassword()).trim());
            user.setFullName(txtFullName.getText().trim());
            user.setRole    ((String) cmbRole.getSelectedItem());
            user.setEmail   (txtEmail.getText().trim());
            user.setActive  (chkActive.isSelected());

            boolean ok = isEdit ? userDAO.updateUser(user) : userDAO.addUser(user);
            if (ok) {
                JOptionPane.showMessageDialog(dialog, isEdit ? "User updated!" : "User added!");
                dialog.dispose();
                loadUsers("");
            } else {
                JOptionPane.showMessageDialog(dialog, "Operation failed (username may be taken).",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(form,   BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        int userId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 2);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete user \"" + name + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "User deleted.");
                loadUsers("");
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changePassword() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        int userId = (int) tableModel.getValueAt(row, 0);
        JPasswordField pf = new JPasswordField();
        int res = JOptionPane.showConfirmDialog(this, pf, "Enter new password:", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String pwd = new String(pf.getPassword()).trim();
            if (pwd.isEmpty()) { JOptionPane.showMessageDialog(this, "Password cannot be empty."); return; }
            if (userDAO.updatePassword(userId, pwd))
                JOptionPane.showMessageDialog(this, "Password updated.");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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

    private JTextField field(String text) {
        JTextField f = new JTextField(text);
        f.setFont(UIConstants.BODY_FONT);
        f.setPreferredSize(new Dimension(300, 35));
        return f;
    }
}
