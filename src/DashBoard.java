import JDBCUntils.DBConnection;
import StaffForm.AddStaffForm;
import StaffForm.ChangeStaffForm;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class DashBoard extends JFrame {
    private JPanel dashBoardTime;
    private JList<String> listStaff;
    private JButton staffButtonAdd;
    private JButton staffButtonChange;
    private JTextField searchBox;
    private JTable productTable;
    private JPanel StaffPanel;
    private DefaultTableModel tableModel;

    public DashBoard(){
        super();
        this.setContentPane(dashBoardTime);
        this.setTitle("Dash Board");
        this.setSize(1000,500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        initTable();
        addEvents();
        loadListData();
        loadTableData();
        StaffPanel.setVisible(true);
    }

    void loadListData() {
        DefaultListModel<String> model = new DefaultListModel<>();
        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT sta_name FROM staffs LIMIT 5"; // MySQL
            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addElement(rs.getString("sta_name"));
            }

            listStaff.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadTableData() {
        try (Connection con = DBConnection.getConnection()) {
            tableModel.setRowCount(0);

            String sql =
                    "SELECT p.pro_name, p.pro_price, p.pro_count, p.pro_type, s.sup_name " +
                    "FROM Products p " +
                    "INNER JOIN Suppliers s ON p.sup_ID = s.sup_ID";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();

                row.add(rs.getString("pro_name"));
                row.add(rs.getBigDecimal("pro_price"));
                row.add(rs.getInt("pro_count"));
                row.add(rs.getInt("pro_type"));
                row.add(rs.getString("sup_name"));

                tableModel.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void searchStaff(String keyword) {
        DefaultListModel<String> model = new DefaultListModel<>();

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT sta_name FROM staffs WHERE sta_name LIKE ? LIMIT 5";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addElement(rs.getString("sta_name"));
            }

            listStaff.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addEvents() {
        searchBox.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { doSearch(); }
            public void removeUpdate(DocumentEvent e) { doSearch(); }
            public void changedUpdate(DocumentEvent e) { doSearch(); }

            private void doSearch() {
                String key = searchBox.getText().trim();
                if (key.isEmpty()) {
                    loadListData();
                } else {
                    searchStaff(key);
                }
            }
        });

        staffButtonAdd.addActionListener(e -> {
            AddStaffForm addStaffForm = new AddStaffForm(this);
            addStaffForm.setVisible(true);
            if (addStaffForm.isAddedSuccess()) {
                loadListData();
            }
        });

        staffButtonChange.addActionListener(e -> {
            String selectedName = listStaff.getSelectedValue();
            if (selectedName == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
                return;
            }

            ChangeStaffForm changeStaffForm = new ChangeStaffForm(this, selectedName);
            changeStaffForm.setVisible(true);

            if (changeStaffForm.isChangedSuccess()) {
                loadListData();
            }

        });
    }

    private void initTable() {
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Tên Sản Phẩm");
        tableModel.addColumn("Giá Tiền");
        tableModel.addColumn("Số Lượng");
        tableModel.addColumn("Loại");
        tableModel.addColumn("Nhà Cung Cấp");
        productTable.setModel(tableModel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashBoard().setVisible(true));
    }
}
