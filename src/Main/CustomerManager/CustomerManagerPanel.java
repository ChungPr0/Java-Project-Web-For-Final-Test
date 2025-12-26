package Main.CustomerManager;

import Utils.ComboItem;
import Utils.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.*;

import static Utils.Style.*;

public class CustomerManagerPanel extends JPanel {

    // --- 1. KHAI BÁO BIẾN GIAO DIỆN ---
    private JList<ComboItem> listCustomer;
    private JTextField txtSearch, txtName, txtPhone, txtAddress;
    private JButton btnAdd, btnSave, btnDelete;
    private JButton btnSort;

    // --- 2. BIẾN TRẠNG THÁI ---
    private int currentSortIndex = 0;
    private final String[] sortModes = {"A-Z", "Z-A", "NEW", "OLD"};
    private int selectedCusID = -1;
    private boolean isDataLoading = false;

    public CustomerManagerPanel() {
        initUI();
        loadListData();
        addEvents();
        addChangeListeners();
    }

    // --- 3. KHỞI TẠO GIAO DIỆN ---
    private void initUI() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setBackground(Color.decode("#ecf0f1"));

        // A. TRÁI
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(250, 0));
        leftPanel.setOpaque(false);

        txtSearch = new JTextField();
        btnSort = new JButton("A-Z");
        btnSort.setFocusable(false);

        JPanel searchPanel = createSearchWithButtonPanel(txtSearch, btnSort, "Tìm kiếm", "Nhập tên hoặc SĐT...");
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        listCustomer = new JList<>();
        listCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listCustomer.setFixedCellHeight(30);
        leftPanel.add(new JScrollPane(listCustomer), BorderLayout.CENTER);

        // B. PHẢI
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        formPanel.add(createHeaderLabel("THÔNG TIN KHÁCH HÀNG"));
        formPanel.add(Box.createVerticalStrut(20));

        txtName = new JTextField();
        formPanel.add(createTextFieldWithLabel(txtName, "Tên Khách Hàng:"));
        formPanel.add(Box.createVerticalStrut(15));

        txtPhone = new JTextField();
        formPanel.add(createTextFieldWithLabel(txtPhone, "Số điện thoại:"));
        formPanel.add(Box.createVerticalStrut(15));

        txtAddress = new JTextField();
        formPanel.add(createTextFieldWithLabel(txtAddress, "Địa chỉ:"));
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(Box.createVerticalGlue()); // Đẩy nội dung lên trên

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setBorder(null);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(5, 0, 5, 0)
        ));

        btnAdd = createButton("Tạo mới", Color.decode("#3498db"));
        btnSave = createButton("Lưu", new Color(46, 204, 113));
        btnDelete = createButton("Xóa", new Color(231, 76, 60));

        footerPanel.add(btnAdd);
        footerPanel.add(btnSave);
        footerPanel.add(btnDelete);

        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.add(scrollForm, BorderLayout.CENTER);
        rightContainer.add(footerPanel, BorderLayout.SOUTH);

        this.add(leftPanel, BorderLayout.WEST);
        this.add(rightContainer, BorderLayout.CENTER);

        clearForm(); // Gọi hàm này để set trạng thái ban đầu
    }

    // --- 4. LOGIC DỮ LIỆU ---

    private void loadListData() {
        DefaultListModel<ComboItem> model = new DefaultListModel<>();
        String keyword = txtSearch.getText().trim();
        boolean isSearching = !keyword.isEmpty() && !keyword.equals("Nhập tên hoặc SĐT...");

        try (Connection con = DBConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT cus_id, cus_name FROM Customers WHERE cus_ID != 1");

            if (isSearching) sql.append(" AND (cus_name LIKE ? OR cus_phone LIKE ?)");

            switch (currentSortIndex) {
                case 1: sql.append(" ORDER BY cus_name DESC"); break;
                case 2: sql.append(" ORDER BY cus_id DESC"); break;
                case 3: sql.append(" ORDER BY cus_id ASC"); break;
                default: sql.append(" ORDER BY cus_name ASC");
            }

            PreparedStatement ps = con.prepareStatement(sql.toString());
            if (isSearching) {
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addElement(new ComboItem(rs.getString("cus_name"), rs.getInt("cus_id")));
            }
            listCustomer.setModel(model);
        } catch (Exception e) {
            showError(this, "Lỗi: " + e.getMessage());
        }
    }

    public void loadDetail(int id) {
        isDataLoading = true;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Customers WHERE cus_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                selectedCusID = rs.getInt("cus_id");
                txtName.setText(rs.getString("cus_name"));
                txtPhone.setText(rs.getString("cus_phone"));
                txtAddress.setText(rs.getString("cus_address"));

                enableForm(true);

                // [SỬA LOGIC NÚT BẤM KHI XEM CHI TIẾT]
                btnAdd.setVisible(true);      // Hiện lại nút Tạo mới
                btnDelete.setVisible(true);   // Hiện nút Xóa
                btnSave.setText("Lưu");
                btnSave.setVisible(false);    // Ẩn nút Lưu (chờ gõ phím mới hiện)
            }
        } catch (Exception e) {
            showError(this, "Lỗi: " + e.getMessage());
        } finally {
            isDataLoading = false;
        }
    }

    // --- 5. SỰ KIỆN ---

    private void addEvents() {
        listCustomer.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ComboItem selected = listCustomer.getSelectedValue();
                if (selected != null) {
                    selectedCusID = selected.getValue();
                    loadDetail(selectedCusID);
                }
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadListData(); }
            public void removeUpdate(DocumentEvent e) { loadListData(); }
            public void changedUpdate(DocumentEvent e) { loadListData(); }
        });

        txtPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) e.consume();
            }
        });

        btnSort.addActionListener(e -> {
            currentSortIndex = (currentSortIndex + 1) % sortModes.length;
            btnSort.setText(sortModes[currentSortIndex]);
            loadListData();
        });

        btnAdd.addActionListener(e -> prepareCreate());
        btnSave.addActionListener(e -> saveCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
    }

    // --- CÁC HÀM LOGIC CHÍNH ---

    private void prepareCreate() {
        listCustomer.clearSelection();
        selectedCusID = -1;

        isDataLoading = true;
        txtName.setText(""); txtPhone.setText(""); txtAddress.setText("");
        isDataLoading = false;

        enableForm(true);
        txtName.requestFocus();

        // [SỬA LOGIC NÚT BẤM KHI TẠO MỚI]
        btnAdd.setVisible(false);       // Ẩn nút Tạo mới
        btnDelete.setVisible(false);    // Ẩn nút Xóa
        btnSave.setText("Lưu");
        btnSave.setVisible(true);       // Hiện nút Lưu ngay lập tức
    }

    private void saveCustomer() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            showError(this, "Tên và Số điện thoại là bắt buộc!");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            if (selectedCusID == -1) {
                // INSERT
                String sql = "INSERT INTO Customers (cus_name, cus_phone, cus_address) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, name); ps.setString(2, phone); ps.setString(3, address);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int newID = rs.getInt(1);
                    showSuccess(this, "Thêm thành công!");
                    loadListData();
                    selectCustomerByID(newID); // Sẽ gọi loadDetail -> Hiện lại nút Add
                }
            } else {
                // UPDATE
                String sql = "UPDATE Customers SET cus_name=?, cus_phone=?, cus_address=? WHERE cus_id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, name); ps.setString(2, phone); ps.setString(3, address); ps.setInt(4, selectedCusID);

                if (ps.executeUpdate() > 0) {
                    showSuccess(this, "Cập nhật thành công!");
                    loadListData();
                    selectCustomerByID(selectedCusID); // Sẽ gọi loadDetail -> Hiện lại nút Add
                }
            }
        } catch (Exception ex) {
            showError(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void deleteCustomer() {
        if (selectedCusID == -1) return;
        if (showConfirm(this, "Bạn chắc chắn muốn xóa khách hàng này?")) {
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM Customers WHERE cus_id=?");
                ps.setInt(1, selectedCusID);
                if (ps.executeUpdate() > 0) {
                    showSuccess(this, "Đã xóa!");
                    loadListData();
                    clearForm();
                }
            } catch (Exception ex) {
                if (ex.getMessage().contains("foreign key")) showError(this, "Không thể xóa: Khách đã có hóa đơn!");
                else showError(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    // --- CÁC HÀM TIỆN ÍCH ---

    private void addChangeListeners() {
        SimpleDocumentListener docListener = new SimpleDocumentListener(e -> {
            if (!isDataLoading) {
                btnSave.setVisible(true);
                if (selectedCusID != -1) btnSave.setText("Lưu");
            }
        });
        txtName.getDocument().addDocumentListener(docListener);
        txtPhone.getDocument().addDocumentListener(docListener);
        txtAddress.getDocument().addDocumentListener(docListener);
    }

    private void clearForm() {
        isDataLoading = true;
        txtName.setText(""); txtPhone.setText(""); txtAddress.setText("");
        isDataLoading = false;

        enableForm(false);
        selectedCusID = -1;

        // [SỬA LOGIC NÚT BẤM TRẠNG THÁI CHỜ]
        btnAdd.setVisible(true);     // Hiện nút Tạo mới
        btnSave.setVisible(false);   // Ẩn nút Lưu
        btnDelete.setVisible(false); // Ẩn nút Xóa
    }

    private void enableForm(boolean enable) {
        txtName.setEnabled(enable);
        txtPhone.setEnabled(enable);
        txtAddress.setEnabled(enable);
    }

    private void selectCustomerByID(int id) {
        ListModel<ComboItem> model = listCustomer.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).getValue() == id) {
                listCustomer.setSelectedIndex(i);
                listCustomer.ensureIndexIsVisible(i);
                break;
            }
        }
    }

    public void refreshData() {
        loadListData();
        selectCustomerByID(selectedCusID);
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