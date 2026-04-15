import java.sql.*;
import javax.swing.*;

public class Dashboard {

    JFrame f;
    JTextArea area;

    public Dashboard() {

        f = new JFrame("Club Dashboard");

        JButton add = new JButton("Add Event");
        JButton delete = new JButton("Delete Event");
        JButton update = new JButton("Update Event");
        JButton view = new JButton("View Events");
        JButton stats = new JButton("Stats");

        add.setBounds(30, 50, 150, 30);
        delete.setBounds(200, 50, 150, 30);
        update.setBounds(30, 100, 150, 30);
        view.setBounds(200, 100, 150, 30);
        stats.setBounds(120, 150, 150, 30);

        area = new JTextArea();
        area.setBounds(30, 200, 320, 150);

        f.add(add); f.add(delete); f.add(update);
        f.add(view); f.add(stats); f.add(area);

        f.setSize(400, 420);
        f.setLayout(null);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        // 🔥 ADD
        add.addActionListener(e -> {
            try {
                String name = JOptionPane.showInputDialog("Event Name:");
                String location = JOptionPane.showInputDialog("Location:");
                String date = JOptionPane.showInputDialog("Date (YYYY-MM-DD):");
                int fee = Integer.parseInt(JOptionPane.showInputDialog("Fee:"));

                Connection conn = DBConnection.getConnection();

                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO events(event_name, location, event_date, fee) VALUES (?, ?, ?, ?)"
                );

                ps.setString(1, name);
                ps.setString(2, location);
                ps.setString(3, date);
                ps.setInt(4, fee);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(f, "Event Added ✅");
                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // 🔥 VIEW
        view.addActionListener(e -> {
            try {
                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT * FROM events");

                area.setText("");

                while (rs.next()) {
                    area.append(
                        rs.getInt("event_id") + " | " +
                        rs.getString("event_name") + " | " +
                        rs.getString("location") + " | " +
                        rs.getString("event_date") + " | ₹" +
                        rs.getInt("fee") + "\n"
                    );
                }

                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // 🔥 DELETE
        delete.addActionListener(e -> {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Event ID to delete"));

                Connection conn = DBConnection.getConnection();

                PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM events WHERE event_id=?"
                );

                ps.setInt(1, id);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(f, "Deleted ✅");
                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // 🔥 UPDATE
        update.addActionListener(e -> {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Event ID:"));
                String name = JOptionPane.showInputDialog("New Name:");
                String location = JOptionPane.showInputDialog("New Location:");
                int fee = Integer.parseInt(JOptionPane.showInputDialog("New Fee:"));

                Connection conn = DBConnection.getConnection();

                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE events SET event_name=?, location=?, fee=? WHERE event_id=?"
                );

                ps.setString(1, name);
                ps.setString(2, location);
                ps.setInt(3, fee);
                ps.setInt(4, id);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(f, "Updated ✅");
                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // 🔥 STATS
        stats.addActionListener(e -> {
            try {
                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM events");

                if (rs.next()) {
                    int count = rs.getInt(1);
                    JOptionPane.showMessageDialog(f, "Total Events: " + count);
                }

                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });
    }
}