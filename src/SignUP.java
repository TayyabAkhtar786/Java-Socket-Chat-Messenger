import javax.swing.*;
import java.awt.Color;
import java.awt.event.*;
import java.sql.*;

public class SignUP extends JFrame implements ActionListener {
    JTextField t1;
    JPasswordField t2;
    JButton b1;

    public SignUP() {
        setTitle("Sign Up");
        setSize(350, 250);
        setLayout(null);

        JLabel l1 = new JLabel("Username:");
        l1.setBounds(30, 40, 100, 25);
        add(l1);

        t1 = new JTextField(20);
        t1.setBounds(130, 40, 150, 25);
        add(t1);

        JLabel l2 = new JLabel("Password:");
        l2.setBounds(30, 80, 100, 25);
        add(l2);

        t2 = new JPasswordField(20);
        t2.setBounds(130, 80, 150, 25);
        add(t2);

        b1 = new JButton("Register");
        b1.setBackground(Color.GREEN);
        b1.setBounds(120, 140, 100, 30);
        add(b1);
        b1.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String uname = t1.getText();
        String pwd = new String(t2.getPassword());

        try {
            Connection conn = DatabaseHelper.getConnection();
            Statement stmt = conn.createStatement();   
            String checkSQL = "SELECT username FROM users WHERE username='" + uname + "'";
            ResultSet rs = stmt.executeQuery(checkSQL);      
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists!");
                stmt.close();
                return;
            }
            // Insert new user
            String insertSQL = "INSERT INTO users VALUES ('" + uname + "', '" + pwd + "')";
            stmt.executeUpdate(insertSQL);

            JOptionPane.showMessageDialog(this, "Registration Successful!");
            dispose();

            stmt.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
