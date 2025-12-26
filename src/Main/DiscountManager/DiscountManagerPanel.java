package Main.DiscountManager;

import Utils.ComboItem;
import Utils.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.Calendar;

import static Utils.Style.*;

public class DiscountManagerPanel extends JPanel {
    // --- 1. KHAI BÁO BIẾN GIAO DIỆN ---
    private JList<ComboItem> listDiscount;
    private JTextField txtSearch, txtCode, txtValue;
    private JComboBox<String> cbType, cbScope;
    private JComboBox<ComboItem> cbCategory;
    private JComboBox<String> cbStartDay, cbStartMonth, cbStartYear;
    private JComboBox<String> cbEndDay, cbEndMonth, cbEndYear;
    private JButton btnAdd, btnSave, btnDelete;
    private JButton btnSort;

    // --- 2. BIẾN TRẠNG THÁI ---
    private int currentSortIndex = 0;
    private final String[] sortModes = {"A-Z", "Z-A", "NEW", "OLD"};
    private int selectedDiscountID = -1; // -1: Chế độ thêm mới
    private boolean isDataLoading = false;

    public DiscountManagerPanel() {
        initUI();
        initDateData();
        loadCategoryData();
        loadListData();
        addEvents();
        addChangeListeners();
    }

    // --- 3. KHỞI TẠO GIAO DIỆN ---
    private void initUI() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setBackground(Color.decode("#ecf0f1"));

        // A. PANEL TRÁI (DANH SÁCH)
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(250, 0));
        leftPanel.setOpaque(false);

        txtSearch = new JTextField();
        btnSort = new JButton("A-Z");
        btnSort.setFocusable(false);

        JPanel searchPanel = createSearchWithButtonPanel(txtSearch, btnSort, "Tìm kiếm", "Nhập mã giảm giá...");
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        listDiscount = new JList<>();
        listDiscount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listDiscount.setFixedCellHeight(30);
        leftPanel.add(new JScrollPane(listDiscount), BorderLayout.CENTER);

        // B. PANEL PHẢI (FORM + FOOTER)

        // B1. Form Panel (Cuộn được)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        formPanel.add(createHeaderLabel("THÔNG TIN MÃ GIẢM GIÁ"));
        formPanel.add(Box.createVerticalStrut(20));

        txtCode = new JTextField();
        formPanel.add(createTextFieldWithLabel(txtCode, "Mã Code (Ví dụ: SALE10):"));
        formPanel.add(Box.createVerticalStrut(15));

        // Loại giảm giá & Giá trị
        JPanel rowTypeVal = new JPanel(new GridLayout(1, 2, 15, 0));
        rowTypeVal.setBackground(Color.WHITE);
        rowTypeVal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        cbType = new JComboBox<>(new String[]{"Phần trăm (%)", "Số tiền (VND)"});
        rowTypeVal.add(createComboBoxWithLabel(cbType, "Loại giảm giá:"));

        txtValue = new JTextField();
        rowTypeVal.add(createTextFieldWithLabel(txtValue, "Giá trị giảm:"));
        formPanel.add(rowTypeVal);
        formPanel.add(Box.createVerticalStrut(15));

        // Phạm vi & Danh mục
        JPanel rowScopeCat = new JPanel(new GridLayout(1, 2, 15, 0));
        rowScopeCat.setBackground(Color.WHITE);
        rowScopeCat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        cbScope = new JComboBox<>(new String[]{"Toàn bộ đơn hàng", "Theo danh mục"});
        rowScopeCat.add(createComboBoxWithLabel(cbScope, "Phạm vi áp dụng:"));

        cbCategory = new JComboBox<>();
        cbCategory.setVisible(false);
        rowScopeCat.add(createComboBoxWithLabel(cbCategory, "Danh mục (Nếu chọn):"));
        formPanel.add(rowScopeCat);
        formPanel.add(Box.createVerticalStrut(15));

        // Ngày bắt đầu
        cbStartDay = new JComboBox<>(); cbStartMonth = new JComboBox<>(); cbStartYear = new JComboBox<>();
        formPanel.add(createDatePanel("Ngày bắt đầu:", cbStartDay, cbStartMonth, cbStartYear));
        formPanel.add(Box.createVerticalStrut(15));

        // Ngày kết thúc
        cbEndDay = new JComboBox<>(); cbEndMonth = new JComboBox<>(); cbEndYear = new JComboBox<>();
        formPanel.add(createDatePanel("Ngày kết thúc:", cbEndDay, cbEndMonth, cbEndYear));

        formPanel.add(Box.createVerticalGlue()); // Đẩy nội dung lên trên

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setBorder(null);
        scrollForm.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);

        // B2. Footer Panel (Cố định)
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

        // Chỉ admin mới thấy nút Thêm
        if (!Utils.Session.isAdmin()) {
            btnAdd.setVisible(false);
        }

        footerPanel.add(btnAdd);
        footerPanel.add(btnSave);
        footerPanel.add(btnDelete);

        // B3. Ghép vào Panel Phải
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.add(scrollForm, BorderLayout.CENTER);
        rightContainer.add(footerPanel, BorderLayout.SOUTH);

        this.add(leftPanel, BorderLayout.WEST);
        this.add(rightContainer, BorderLayout.CENTER);

        enableForm(false);
    }

    // --- 4. TẢI DỮ LIỆU ---

    private void initDateData() {
        for (int i=1; i<=31; i++) { String s = String.format("%02d", i); cbStartDay.addItem(s); cbEndDay.addItem(s); }
        for (int i=1; i<=12; i++) { String s = String.format("%02d", i); cbStartMonth.addItem(s); cbEndMonth.addItem(s); }
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i=currentYear; i<=currentYear+5; i++) { String s = String.valueOf(i); cbStartYear.addItem(s); cbEndYear.addItem(s); }
    }

    private void loadCategoryData() {
        isDataLoading = true;
        cbCategory.removeAllItems();
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT type_ID, type_name FROM ProductTypes");
            while (rs.next()) cbCategory.addItem(new ComboItem(rs.getString("type_name"), rs.getInt("type_ID")));
        } catch (Exception ignored) {}
        finally { isDataLoading = false; }
    }

    private void loadListData() {
        DefaultListModel<ComboItem> model = new DefaultListModel<>();
        String keyword = txtSearch.getText().trim();
        boolean isSearching = !keyword.isEmpty() && !keyword.equals("Nhập mã giảm giá...");

        try (Connection con = DBConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT dis_ID, dis_code FROM Discounts");

            if (isSearching) sql.append(" WHERE dis_code LIKE ?");

            switch (currentSortIndex) {
                case 1: sql.append(" ORDER BY dis_code DESC"); break;
                case 2: sql.append(" ORDER BY dis_ID DESC"); break;
                case 3: sql.append(" ORDER BY dis_ID ASC"); break;
                default: sql.append(" ORDER BY dis_code ASC");
            }

            PreparedStatement ps = con.prepareStatement(sql.toString());
            if (isSearching) ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addElement(new ComboItem(rs.getString("dis_code"), rs.getInt("dis_ID")));
            }
            listDiscount.setModel(model);
        } catch (Exception e) {
            showError(this, "Lỗi: " + e.getMessage());
        }
    }

    private void loadDetail(int id) {
        isDataLoading = true;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Discounts WHERE dis_ID = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                selectedDiscountID = rs.getInt("dis_ID");
                txtCode.setText(rs.getString("dis_code"));

                String type = rs.getString("dis_type");
                cbType.setSelectedIndex("PERCENT".equals(type) ? 0 : 1);

                txtValue.setText(String.valueOf(rs.getDouble("dis_value")));

                String scope = rs.getString("dis_scope");
                cbScope.setSelectedIndex("ALL".equals(scope) ? 0 : 1);

                int catID = rs.getInt("dis_category_id");
                if (catID > 0) setSelectedComboItem(cbCategory, catID);

                setDateToCombo(rs.getString("dis_start_date"), cbStartDay, cbStartMonth, cbStartYear);
                setDateToCombo(rs.getString("dis_end_date"), cbEndDay, cbEndMonth, cbEndYear);

                // Logic hiển thị
                if (Utils.Session.isAdmin()) {
                    enableForm(true);
                    btnAdd.setVisible(true);
                    btnDelete.setVisible(true);
                    btnSave.setText("Lưu");
                    btnSave.setVisible(false); // Chờ sửa
                } else {
                    enableForm(false);
                    btnAdd.setVisible(false);
                    btnDelete.setVisible(false);
                }
            }
        } catch (Exception e) {
            showError(this, "Lỗi: " + e.getMessage());
        } finally {
            isDataLoading = false;
        }
    }

    // --- 5. SỰ KIỆN ---

    private void addEvents() {
        listDiscount.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ComboItem selected = listDiscount.getSelectedValue();
                if (selected != null) {
                    selectedDiscountID = selected.getValue();
                    loadDetail(selectedDiscountID);
                }
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadListData(); }
            public void removeUpdate(DocumentEvent e) { loadListData(); }
            public void changedUpdate(DocumentEvent e) { loadListData(); }
        });

        btnSort.addActionListener(e -> {
            currentSortIndex = (currentSortIndex + 1) % sortModes.length;
            btnSort.setText(sortModes[currentSortIndex]);
            loadListData();
        });

        cbScope.addActionListener(e -> {
            // Chỉ enable nếu là Admin và chọn Scope là Category
            boolean isCategory = cbScope.getSelectedIndex() == 1;
            cbCategory.setVisible(isCategory && Utils.Session.isAdmin());
            checkChange();
        });

        // --- NÚT THÊM MỚI ---
        btnAdd.addActionListener(e -> prepareCreate());

        // --- NÚT LƯU ---
        btnSave.addActionListener(e -> saveDiscount());

        // --- NÚT XÓA ---
        btnDelete.addActionListener(e -> deleteDiscount());

        txtValue.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.') e.consume();
            }
        });
    }

    // --- CÁC HÀM LOGIC CHÍNH ---

    private void prepareCreate() {
        listDiscount.clearSelection();
        selectedDiscountID = -1; // Mode thêm mới

        isDataLoading = true;
        txtCode.setText(""); txtValue.setText("");
        cbType.setSelectedIndex(0); cbScope.setSelectedIndex(0);
        cbCategory.setVisible(false);
        if (cbCategory.getItemCount() > 0) cbCategory.setSelectedIndex(0);

        // Set ngày hiện tại
        Calendar cal = Calendar.getInstance();
        String d = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
        String m = String.format("%02d", cal.get(Calendar.MONTH) + 1);
        String y = String.valueOf(cal.get(Calendar.YEAR));
        cbStartDay.setSelectedItem(d); cbStartMonth.setSelectedItem(m); cbStartYear.setSelectedItem(y);
        cbEndDay.setSelectedItem(d); cbEndMonth.setSelectedItem(m); cbEndYear.setSelectedItem(y);
        isDataLoading = false;

        enableForm(true);
        txtCode.requestFocus();

        btnAdd.setVisible(false);
        btnDelete.setVisible(false);
        btnSave.setText("Lưu");
        btnSave.setVisible(true);
    }

    private void saveDiscount() {
        if (txtCode.getText().trim().isEmpty() || txtValue.getText().trim().isEmpty()) {
            showError(this, "Vui lòng nhập Mã Code và Giá trị!");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String code = txtCode.getText().trim().toUpperCase();
            double value = Double.parseDouble(txtValue.getText().trim());
            String type = cbType.getSelectedIndex() == 0 ? "PERCENT" : "FIXED";
            String scope = cbScope.getSelectedIndex() == 0 ? "ALL" : "CATEGORY";

            Integer catID = null;
            if ("CATEGORY".equals(scope)) {
                ComboItem item = (ComboItem) cbCategory.getSelectedItem();
                if (item != null) catID = item.getValue();
                else { showError(this, "Vui lòng chọn danh mục!"); return; }
            }

            String startDate = getSelectedDate(cbStartYear, cbStartMonth, cbStartDay);
            String endDate = getSelectedDate(cbEndYear, cbEndMonth, cbEndDay);

            if (startDate.compareTo(endDate) > 0) {
                showError(this, "Ngày bắt đầu phải trước ngày kết thúc!");
                return;
            }

            if (selectedDiscountID == -1) {
                // INSERT
                String sql = "INSERT INTO Discounts (dis_code, dis_type, dis_value, dis_scope, dis_category_id, dis_start_date, dis_end_date) VALUES (?,?,?,?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, code); ps.setString(2, type); ps.setDouble(3, value);
                ps.setString(4, scope);
                if (catID == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, catID);
                ps.setString(6, startDate); ps.setString(7, endDate);
                ps.executeUpdate();
                showSuccess(this, "Thêm thành công!");
            } else {
                // UPDATE
                String sql = "UPDATE Discounts SET dis_code=?, dis_type=?, dis_value=?, dis_scope=?, dis_category_id=?, dis_start_date=?, dis_end_date=? WHERE dis_ID=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, code); ps.setString(2, type); ps.setDouble(3, value);
                ps.setString(4, scope);
                if (catID == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, catID);
                ps.setString(6, startDate); ps.setString(7, endDate);
                ps.setInt(8, selectedDiscountID);
                ps.executeUpdate();
                showSuccess(this, "Cập nhật thành công!");
            }
            loadListData();
            // Nếu là insert thì clear, update thì load lại chi tiết
            if (selectedDiscountID == -1) clearForm();
            else loadDetail(selectedDiscountID);

        } catch (Exception ex) {
            if (ex.getMessage().contains("UNIQUE")) showError(this, "Mã giảm giá đã tồn tại!");
            else showError(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void deleteDiscount() {
        if(selectedDiscountID == -1) return;
        if (showConfirm(this, "Xóa mã này?")) {
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM Discounts WHERE dis_ID=?");
                ps.setInt(1, selectedDiscountID);
                ps.executeUpdate();
                showSuccess(this, "Xóa thành công!");
                loadListData();
                clearForm();
            } catch (Exception ex) {
                if (ex.getMessage().contains("foreign key")) showError(this, "Mã đã được sử dụng trong hóa đơn, không thể xóa!");
                else showError(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    // --- CÁC HÀM TIỆN ÍCH ---

    private void addChangeListeners() {
        SimpleDocumentListener docListener = new SimpleDocumentListener(e -> checkChange());
        txtCode.getDocument().addDocumentListener(docListener);
        txtValue.getDocument().addDocumentListener(docListener);

        ActionListener actionListener = e -> checkChange();
        cbType.addActionListener(actionListener);
        cbCategory.addActionListener(actionListener);
        cbStartDay.addActionListener(actionListener); cbStartMonth.addActionListener(actionListener); cbStartYear.addActionListener(actionListener);
        cbEndDay.addActionListener(actionListener); cbEndMonth.addActionListener(actionListener); cbEndYear.addActionListener(actionListener);
    }

    private void checkChange() {
        if (!isDataLoading && Utils.Session.isAdmin()) btnSave.setVisible(true);
    }

    private void clearForm() {
        isDataLoading = true;
        txtCode.setText(""); txtValue.setText("");
        cbType.setSelectedIndex(0); cbScope.setSelectedIndex(0);
        cbCategory.setVisible(false);
        isDataLoading = false;

        enableForm(false);
        selectedDiscountID = -1;

        if (Utils.Session.isAdmin()) btnAdd.setVisible(true);
        btnSave.setVisible(false);
        btnDelete.setVisible(false);
    }

    private void enableForm(boolean enable) {
        boolean isAdmin = Utils.Session.isAdmin();
        txtCode.setEnabled(enable && isAdmin);
        cbType.setEnabled(enable && isAdmin);
        txtValue.setEnabled(enable && isAdmin);
        cbScope.setEnabled(enable && isAdmin);
        cbCategory.setVisible(enable && isAdmin && cbScope.getSelectedIndex() == 1);
        cbStartDay.setEnabled(enable && isAdmin); cbStartMonth.setEnabled(enable && isAdmin); cbStartYear.setEnabled(enable && isAdmin);
        cbEndDay.setEnabled(enable && isAdmin); cbEndMonth.setEnabled(enable && isAdmin); cbEndYear.setEnabled(enable && isAdmin);
    }

    private void setSelectedComboItem(JComboBox<ComboItem> cb, int id) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).getValue() == id) { cb.setSelectedIndex(i); break; }
        }
    }

    private String getSelectedDate(JComboBox<String> y, JComboBox<String> m, JComboBox<String> d) {
        return y.getSelectedItem() + "-" + m.getSelectedItem() + "-" + d.getSelectedItem();
    }

    private void setDateToCombo(String dateStr, JComboBox<String> d, JComboBox<String> m, JComboBox<String> y) {
        if (dateStr != null && dateStr.length() >= 10) {
            String[] parts = dateStr.split("-");
            y.setSelectedItem(parts[0]);
            m.setSelectedItem(parts[1]);
            d.setSelectedItem(parts[2]);
        }
    }

    private void selectDiscountByID(int id) {
        for (int i = 0; i < listDiscount.getModel().getSize(); i++) {
            if (listDiscount.getModel().getElementAt(i).getValue() == id) {
                listDiscount.setSelectedIndex(i);
                listDiscount.ensureIndexIsVisible(i);
                break;
            }
        }
    }

    public void refreshData() {
        loadCategoryData(); // Load lại category phòng trường hợp mới thêm
        loadListData();
        selectDiscountByID(selectedDiscountID);
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