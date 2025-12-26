package Main.StaffManager;

import Utils.ComboItem;
import Utils.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.*;

import static Utils.Style.*;

public class StaffManagerPanel extends JPanel {
    // --- 1. KHAI BÁO BIẾN GIAO DIỆN ---
    private JList<ComboItem> listStaff;
    private JTextField txtSearch, txtName, txtSalary, txtPhone, txtAddress, txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkIsAdmin;
    private JComboBox<String> cbDay, cbMonth, cbYear;
    private JComboBox<String> cbStartDay, cbStartMonth, cbStartYear;
    private JButton btnAdd, btnSave, btnDelete;
    private JButton btnSort;

    // --- 2. BIẾN TRẠNG THÁI ---
    private int currentSortIndex = 0;
    private final String[] sortModes = {"A-Z", "Z-A", "NEW", "OLD"};
    private int selectedStaffID = -1;      // -1: Chế độ thêm mới
    private boolean isDataLoading = false;

    public StaffManagerPanel() {
        initUI();
        initComboBoxData();
        loadListData();
        addEvents();
        addChangeListeners();
    }

    // --- 3. KHỞI TẠO GIAO DIỆN ---
    private void initUI() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setBackground(Color.decode("#ecf0f1"));

        // A. PANEL TRÁI
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(250, 0));
        leftPanel.setOpaque(false);

        txtSearch = new JTextField();
        btnSort = new JButton("A-Z");
        btnSort.setFocusable(false);

        JPanel searchPanel = createSearchWithButtonPanel(txtSearch, btnSort, "Tìm kiếm", "Nhập tên nhân viên...");
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        listStaff = new JList<>();
        listStaff.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listStaff.setFixedCellHeight(30);
        leftPanel.add(new JScrollPane(listStaff), BorderLayout.CENTER);

        // B. PANEL PHẢI (FORM + FOOTER)

        // B1. Form nhập liệu (Cuộn được)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        formPanel.add(createHeaderLabel("THÔNG TIN NHÂN VIÊN"));
        formPanel.add(Box.createVerticalStrut(20));

        txtName = new JTextField();
        formPanel.add(createTextFieldWithLabel(txtName, "Họ và Tên:"));
        formPanel.add(Box.createVerticalStrut(16));

        // Ngày sinh & Quyền
        cbDay = new JComboBox<>(); cbMonth = new JComboBox<>(); cbYear = new JComboBox<>();
        JPanel datePanel = createDatePanel("Ngày sinh:", cbDay, cbMonth, cbYear);
        chkIsAdmin = new JCheckBox();
        JPanel pRoleWrapper = createCheckBoxWithLabel(chkIsAdmin, "Phân quyền:", "QUẢN TRỊ VIÊN");

        JPanel rowDateAndRole = new JPanel(new GridLayout(1, 2, 15, 0));
        rowDateAndRole.setBackground(Color.WHITE);
        rowDateAndRole.add(datePanel);
        rowDateAndRole.add(pRoleWrapper);
        rowDateAndRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        formPanel.add(rowDateAndRole);
        formPanel.add(Box.createVerticalStrut(16));

        txtPhone = new JTextField();
        formPanel.add(createTextFieldWithLabel(txtPhone, "Số điện thoại:"));
        formPanel.add(Box.createVerticalStrut(16));

        txtAddress = new JTextField();
        formPanel.add(createTextFieldWithLabel(txtAddress, "Địa chỉ:"));
        formPanel.add(Box.createVerticalStrut(16));

        // Lương & Ngày vào làm
        JPanel rowSalaryAndStart = new JPanel(new GridLayout(1, 2, 15, 0));
        rowSalaryAndStart.setBackground(Color.WHITE);
        rowSalaryAndStart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        txtSalary = new JTextField();
        rowSalaryAndStart.add(createTextFieldWithLabel(txtSalary, "Lương cơ bản (VNĐ):"));

        cbStartDay = new JComboBox<>(); cbStartMonth = new JComboBox<>(); cbStartYear = new JComboBox<>();
        JPanel pStartDate = createDatePanel("Ngày vào làm:", cbStartDay, cbStartMonth, cbStartYear);
        rowSalaryAndStart.add(pStartDate);

        formPanel.add(rowSalaryAndStart);
        formPanel.add(Box.createVerticalStrut(16));

        // Tài khoản & Mật khẩu
        txtUsername = new JTextField();
        formPanel.add(createTextFieldWithLabel(txtUsername, "Tài khoản (Có thể để trống):"));
        formPanel.add(Box.createVerticalStrut(16));

        txtPassword = new JPasswordField();
        JCheckBox chkShowPass = new JCheckBox();
        formPanel.add(createPasswordFieldWithLabel(txtPassword, "Mật khẩu (Có thể để trống):", chkShowPass));

        formPanel.add(Box.createVerticalGlue()); // Đẩy nội dung lên trên

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setBorder(null);
        scrollForm.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);

        // B2. Footer chứa nút (Cố định)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(5, 0, 5, 0)
        ));

        btnAdd = createButton("Tạo mới", Color.decode("#3498db"));
        btnSave = createButton("Lưu", new Color(46, 204, 113));
        btnDelete = createButton("Xóa", new Color(231, 76, 60));

        btnSave.setVisible(false);
        btnDelete.setVisible(false);

        footerPanel.add(btnAdd);
        footerPanel.add(btnSave);
        footerPanel.add(btnDelete);

        // B3. Ghép vào Panel phải
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.add(scrollForm, BorderLayout.CENTER);
        rightContainer.add(footerPanel, BorderLayout.SOUTH);

        this.add(leftPanel, BorderLayout.WEST);
        this.add(rightContainer, BorderLayout.CENTER);

        enableForm(false);
    }

    // --- 4. TẢI DỮ LIỆU ---
    private void loadListData() {
        DefaultListModel<ComboItem> model = new DefaultListModel<>();
        String keyword = txtSearch.getText().trim();
        boolean isSearching = !keyword.isEmpty() && !keyword.equals("Nhập tên nhân viên...");

        try (Connection con = DBConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT sta_id, sta_name FROM Staffs");

            if (isSearching) sql.append(" WHERE sta_name LIKE ?");

            switch (currentSortIndex) {
                case 1: sql.append(" ORDER BY sta_name DESC"); break;
                case 2: sql.append(" ORDER BY sta_start_date DESC"); break;
                case 3: sql.append(" ORDER BY sta_start_date ASC"); break;
                default: sql.append(" ORDER BY sta_name ASC");
            }

            PreparedStatement ps = con.prepareStatement(sql.toString());
            if (isSearching) ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addElement(new ComboItem(rs.getString("sta_name"), rs.getInt("sta_id")));
            }
            listStaff.setModel(model);
        } catch (Exception e) {
            showError(this, "Lỗi: " + e.getMessage());
        }
    }

    private void loadDetail(int id) {
        isDataLoading = true;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Staffs WHERE sta_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                selectedStaffID = rs.getInt("sta_id");

                txtName.setText(rs.getString("sta_name"));
                txtPhone.setText(rs.getString("sta_phone"));
                txtAddress.setText(rs.getString("sta_address"));
                txtSalary.setText(String.format("%.0f", rs.getDouble("sta_salary")));
                txtUsername.setText(rs.getString("sta_username"));
                txtPassword.setText(rs.getString("sta_password"));

                String role = rs.getString("sta_role");
                chkIsAdmin.setSelected(role != null && role.equalsIgnoreCase("Admin"));

                setComboBoxDate(rs.getString("sta_date_of_birth"), cbDay, cbMonth, cbYear);
                setComboBoxDate(rs.getString("sta_start_date"), cbStartDay, cbStartMonth, cbStartYear);

                enableForm(true);
                btnAdd.setVisible(true);      // Hiện nút Thêm
                btnDelete.setVisible(true);   // Hiện nút Xóa
                btnSave.setText("Lưu");
                btnSave.setVisible(false);    // Ẩn nút Lưu (chờ sửa mới hiện)
            }
        } catch (Exception e) {
            showError(this, "Lỗi: " + e.getMessage());
        } finally {
            isDataLoading = false;
        }
    }

    // --- 5. SỰ KIỆN ---
    private void addEvents() {
        listStaff.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ComboItem selected = listStaff.getSelectedValue();
                if (selected != null) {
                    selectedStaffID = selected.getValue();
                    loadDetail(selectedStaffID);
                }
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadListData(); }
            public void removeUpdate(DocumentEvent e) { loadListData(); }
            public void changedUpdate(DocumentEvent e) { loadListData(); }
        });

        java.awt.event.KeyAdapter digitOnly = new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }
        };
        txtPhone.addKeyListener(digitOnly);
        txtSalary.addKeyListener(digitOnly);

        btnSort.addActionListener(e -> {
            currentSortIndex = (currentSortIndex + 1) % sortModes.length;
            btnSort.setText(sortModes[currentSortIndex]);
            loadListData();
        });

        // --- NÚT THÊM MỚI ---
        btnAdd.addActionListener(e -> prepareCreate());

        // --- NÚT LƯU (Insert/Update) ---
        btnSave.addActionListener(e -> saveStaff());

        // --- NÚT XÓA ---
        btnDelete.addActionListener(e -> deleteStaff());
    }

    // --- CÁC HÀM LOGIC CHÍNH ---

    private void prepareCreate() {
        listStaff.clearSelection();
        selectedStaffID = -1; // Đánh dấu thêm mới

        isDataLoading = true;
        txtName.setText(""); txtPhone.setText(""); txtAddress.setText("");
        txtSalary.setText(""); txtUsername.setText(""); txtPassword.setText("");
        chkIsAdmin.setSelected(false);
        // Reset ngày về mặc định (hoặc ngày hiện tại nếu muốn)
        cbDay.setSelectedIndex(0); cbMonth.setSelectedIndex(0); cbYear.setSelectedItem("2000");
        cbStartDay.setSelectedIndex(0); cbStartMonth.setSelectedIndex(0); cbStartYear.setSelectedIndex(0);
        isDataLoading = false;

        enableForm(true);
        txtName.requestFocus();

        btnAdd.setVisible(false);      // Ẩn nút Thêm
        btnDelete.setVisible(false);   // Ẩn nút Xóa
        btnSave.setText("Lưu");
        btnSave.setVisible(true);      // Hiện nút Lưu
    }

    private void saveStaff() {
        // 1. Validate
        if (txtName.getText().trim().isEmpty() || txtPhone.getText().trim().isEmpty() ||
                txtAddress.getText().trim().isEmpty() || txtSalary.getText().trim().isEmpty()) {
            showError(this, "Vui lòng nhập đầy đủ: Tên, SĐT, Địa chỉ, Lương!");
            return;
        }

        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if ((!user.isEmpty() && pass.isEmpty()) || (user.isEmpty() && !pass.isEmpty())) {
            showError(this, "Tài khoản và Mật khẩu phải nhập cả hai (hoặc để trống cả hai)!");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            // Check trùng username
            if (!user.isEmpty()) {
                String checkSql = "SELECT COUNT(*) FROM Staffs WHERE sta_username = ? AND sta_id != ?";
                PreparedStatement psCheck = con.prepareStatement(checkSql);
                psCheck.setString(1, user);
                psCheck.setInt(2, selectedStaffID); // Nếu thêm mới (-1) thì check toàn bộ
                ResultSet rsCheck = psCheck.executeQuery();
                if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                    showError(this, "Tài khoản '" + user + "' đã tồn tại!");
                    return;
                }
            }

            String dob = cbYear.getSelectedItem() + "-" + cbMonth.getSelectedItem() + "-" + cbDay.getSelectedItem();
            String startDate = cbStartYear.getSelectedItem() + "-" + cbStartMonth.getSelectedItem() + "-" + cbStartDay.getSelectedItem();
            double salary = Double.parseDouble(txtSalary.getText().trim());
            String role = chkIsAdmin.isSelected() ? "Admin" : "Staff";

            if (selectedStaffID == -1) {
                // INSERT
                String sql = "INSERT INTO Staffs (sta_name, sta_date_of_birth, sta_phone, sta_address, sta_salary, sta_start_date, sta_username, sta_password, sta_role) VALUES (?,?,?,?,?,?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, txtName.getText().trim());
                ps.setString(2, dob);
                ps.setString(3, txtPhone.getText().trim());
                ps.setString(4, txtAddress.getText().trim());
                ps.setDouble(5, salary);
                ps.setString(6, startDate);
                if (user.isEmpty()) ps.setNull(7, Types.VARCHAR); else ps.setString(7, user);
                if (pass.isEmpty()) ps.setNull(8, Types.VARCHAR); else ps.setString(8, pass);
                ps.setString(9, role);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    showSuccess(this, "Thêm nhân viên thành công!");
                    loadListData();
                    selectStaffByID(rs.getInt(1));
                }
            } else {
                // UPDATE
                String sql = "UPDATE Staffs SET sta_name=?, sta_date_of_birth=?, sta_phone=?, sta_address=?, sta_salary=?, sta_start_date=?, sta_username=?, sta_password=?, sta_role=? WHERE sta_id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, txtName.getText().trim());
                ps.setString(2, dob);
                ps.setString(3, txtPhone.getText().trim());
                ps.setString(4, txtAddress.getText().trim());
                ps.setDouble(5, salary);
                ps.setString(6, startDate);
                if (user.isEmpty()) ps.setNull(7, Types.VARCHAR); else ps.setString(7, user);
                if (pass.isEmpty()) ps.setNull(8, Types.VARCHAR); else ps.setString(8, pass);
                ps.setString(9, role);
                ps.setInt(10, selectedStaffID);

                if (ps.executeUpdate() > 0) {
                    showSuccess(this, "Cập nhật thành công!");
                    loadListData();
                    selectStaffByID(selectedStaffID);
                }
            }
        } catch (Exception ex) {
            showError(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void deleteStaff() {
        if(selectedStaffID == -1) return;
        if(showConfirm(this, "Xóa nhân viên này?")){
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM Staffs WHERE sta_id=?");
                ps.setInt(1, selectedStaffID);
                if (ps.executeUpdate() > 0) {
                    showSuccess(this, "Đã xóa!");
                    loadListData();
                    clearForm();
                }
            } catch (Exception ex) {
                if (ex.getMessage().contains("foreign key")) showError(this, "Không thể xóa: Nhân viên đã lập hóa đơn!");
                else showError(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    // --- CÁC HÀM TIỆN ÍCH ---

    private void addChangeListeners() {
        SimpleDocumentListener docListener = new SimpleDocumentListener(e -> {
            if (!isDataLoading) {
                btnSave.setVisible(true);
                if(selectedStaffID != -1) btnSave.setText("Lưu");
            }
        });
        txtName.getDocument().addDocumentListener(docListener);
        txtPhone.getDocument().addDocumentListener(docListener);
        txtAddress.getDocument().addDocumentListener(docListener);
        txtSalary.getDocument().addDocumentListener(docListener);
        txtUsername.getDocument().addDocumentListener(docListener);
        txtPassword.getDocument().addDocumentListener(docListener);
        chkIsAdmin.addActionListener(e -> { if (!isDataLoading) btnSave.setVisible(true); });

        java.awt.event.ActionListener dateListener = e -> { if (!isDataLoading) btnSave.setVisible(true); };
        cbDay.addActionListener(dateListener); cbMonth.addActionListener(dateListener); cbYear.addActionListener(dateListener);
        cbStartDay.addActionListener(dateListener); cbStartMonth.addActionListener(dateListener); cbStartYear.addActionListener(dateListener);
    }

    private void clearForm() {
        isDataLoading = true;
        txtName.setText(""); txtPhone.setText(""); txtAddress.setText(""); txtSalary.setText("");
        txtUsername.setText(""); txtPassword.setText(""); chkIsAdmin.setSelected(false);
        isDataLoading = false;

        enableForm(false);
        selectedStaffID = -1;

        btnAdd.setVisible(true);
        btnSave.setVisible(false);
        btnDelete.setVisible(false);
    }

    private void enableForm(boolean enable) {
        txtName.setEnabled(enable); txtPhone.setEnabled(enable);
        txtAddress.setEnabled(enable); txtSalary.setEnabled(enable);
        cbStartDay.setEnabled(enable); cbStartMonth.setEnabled(enable); cbStartYear.setEnabled(enable);
        cbDay.setEnabled(enable); cbMonth.setEnabled(enable); cbYear.setEnabled(enable);
        txtUsername.setEnabled(enable); txtPassword.setEnabled(enable); chkIsAdmin.setEnabled(enable);
    }

    private void initComboBoxData() {
        for (int i=1; i<=31; i++) { String v = String.format("%02d", i); cbDay.addItem(v); cbStartDay.addItem(v); }
        for (int i=1; i<=12; i++) { String v = String.format("%02d", i); cbMonth.addItem(v); cbStartMonth.addItem(v); }
        for (int i=2025; i>=1960; i--) { String v = String.valueOf(i); cbYear.addItem(v); cbStartYear.addItem(v); }
    }

    private void setComboBoxDate(String dateStr, JComboBox<String> d, JComboBox<String> m, JComboBox<String> y) {
        if (dateStr != null && !dateStr.isEmpty()) {
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                y.setSelectedItem(parts[0]);
                m.setSelectedItem(parts[1]);
                d.setSelectedItem(parts[2]);
            }
        }
    }

    private void selectStaffByID(int id) {
        ListModel<ComboItem> model = listStaff.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).getValue() == id) {
                listStaff.setSelectedIndex(i);
                listStaff.ensureIndexIsVisible(i);
                break;
            }
        }
    }

    public void refreshData() {
        loadListData();
        selectStaffByID(selectedStaffID);
    }

    @FunctionalInterface interface DocumentUpdateListener { void update(DocumentEvent e); }
    static class SimpleDocumentListener implements DocumentListener {
        private final DocumentUpdateListener listener;
        public SimpleDocumentListener(DocumentUpdateListener listener) { this.listener = listener; }
        public void insertUpdate(DocumentEvent e) { listener.update(e); }
        public void removeUpdate(DocumentEvent e) { listener.update(e); }
        public void changedUpdate(DocumentEvent e) { listener.update(e); }
    }
}