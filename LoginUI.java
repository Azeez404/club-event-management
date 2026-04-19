import java.sql.*;
import javax.swing.*;

public class LoginUI {

    public static void main(String[] args) {

        JFrame f = new JFrame("Club Event Management - Login");

        JLabel userL = new JLabel("Username:");
        JLabel passL = new JLabel("Password:");

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();

        JButton login = new JButton("Login");

        userL.setBounds(50, 50, 100, 30);
        user.setBounds(150, 50, 150, 30);

        passL.setBounds(50, 100, 100, 30);
        pass.setBounds(150, 100, 150, 30);

        login.setBounds(120, 160, 100, 40);

        f.add(userL);
        f.add(user);
        f.add(passL);
        f.add(pass);
        f.add(login);

        f.setSize(350, 250);
        f.setLayout(null);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        login.addActionListener(e -> {
            String u = user.getText().trim();
            String p = new String(pass.getPassword());

            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(f, "Please enter username and password.");
                return;
            }

            try {
                Connection conn = DBConnection.getConnection();

                PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM users WHERE username = ? AND password = ?"
                );
                ps.setString(1, u);
                ps.setString(2, p);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    f.dispose();
                    new Dashboard();
                } else {
                    JOptionPane.showMessageDialog(f, "Invalid username or password.");
                }

                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "DB Error: " + ex.getMessage());
            }
        });
    }
}
