import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class SignIn extends JFrame {
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;
    private JButton đăngKýButton;
    private JPanel SignInTime;
    public SignIn(){
        super();
        this.setContentPane(SignInTime);
        this.setSize(400,400);
        this.setTitle("quan ly dang ky");
        this.setVisible(true);
        read();

    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/quanLyUser", "root", "123456");
    }


    void read(){
        // Đặt placeholder CHỈ 1 LẦN trong hàm read() hoặc constructor
        textField1.setText("nhập tên");
        textField1.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField1.getText().equals("nhập tên")) {
                    textField1.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField1.getText().isEmpty()) {
                    textField1.setText("nhập tên");
                }
            }
        });

        đăngKýButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String name = textField1.getText();
                String password = new String(passwordField1.getPassword());
                String conPassword = new String(passwordField2.getPassword());

                if(password.length() < 8){
                    JOptionPane.showMessageDialog(null, "Mật khẩu phải trên 8 kí tự","Cảnh báo" ,JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if(!password.equals(conPassword)){ // Dùng equals
                    JOptionPane.showMessageDialog(null, "Mật khẩu phải giống nhau","Cảnh báo" ,JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int results = insert(name, password);
                    if(results > 0){
                        JOptionPane.showMessageDialog(null, "Đăng ký thành công!");

                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Đăng ký thất bại","Cảnh báo" ,JOptionPane.WARNING_MESSAGE);

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }



    int insert(String name, String password) throws SQLException{
        Connection con =null;
        PreparedStatement ps =null;
        int RecordCount =0;

        try{
            con = getConnection();
            ps = con.prepareStatement("insert into user(name, password) values(?, ?)");
            ps.setString(1, name);
            ps.setString(2, password);
            RecordCount = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(ps!= null){
                ps.close();
            }
            if(con !=null){
                con.close();
            }
        }
        return RecordCount;
    }


    static void main() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SignIn();
            }
        });
    }
}
