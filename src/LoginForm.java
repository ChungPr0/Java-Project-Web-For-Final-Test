import javax.swing.*;

public class LoginForm extends  JFrame{
    private JPanel dangNhaptime;
    private JFormattedTextField e2weqweqwFormattedTextField;
    private JButton loginButton;
    private JPasswordField passwordField1;

    public LoginForm(){
        super();
        this.setContentPane(dangNhaptime);
        this.setSize(400,400);
        this.setTitle("quan ly dang nhap");
        this.setVisible(true);

    }

    static void main() {
        LoginForm loginForm =new LoginForm();
    }
}
