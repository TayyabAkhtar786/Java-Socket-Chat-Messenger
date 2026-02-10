import javax.swing.*;
import java.awt.Color;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame implements ActionListener {
    JTextField t1;
    JPasswordField t2;
    JButton b1, b2;
    JLabel l1, l2;

    public Login() {
        setTitle("Login");
        setSize(350, 250);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        l1 = new JLabel("Username:");
        l1.setBounds(30, 40, 100, 25);
        add(l1);

        t1 = new JTextField(20);
        t1.setBounds(130, 40, 150, 25);
        add(t1);

        l2 = new JLabel("Password:");
        l2.setBounds(30, 80, 100, 25);
        add(l2);

        t2 = new JPasswordField(20);
        t2.setBounds(130, 80, 150, 25);
        add(t2);

        b1 = new JButton("Sign In");
        b1.setBackground(Color.CYAN);
        b1.setBounds(40, 140, 100, 30);
        add(b1);

        b2 = new JButton("Sign Up");
        b2.setBackground(Color.LIGHT_GRAY);
        b2.setBounds(170, 140, 100, 30);
        add(b2);

        b1.addActionListener(this);
        b2.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            String uname = t1.getText();
            String pwd = new String(t2.getPassword());

            try {
                Connection conn = DatabaseHelper.getConnection();
                Statement st = conn.createStatement();

                String sql = "SELECT * FROM users WHERE username='" + uname + "' AND password='" + pwd + "'";
                ResultSet rs = st.executeQuery(sql);

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful");
                    dispose();
                    new Client(uname);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Credentials");
                }
                st.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            }
        }

        if (e.getSource() == b2) {
            new SignUP();
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}
