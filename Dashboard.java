import java.sql.*;
import javax.swing.*;

public class Dashboard {

    JFrame f;
    JTextArea area;

    public Dashboard() {

        f = new JFrame("Club Dashboard");

        // --- Buttons ---
        JButton add      = new JButton("Add Event");
        JButton delete   = new JButton("Delete Event");
        JButton update   = new JButton("Update Event");
        JButton view     = new JButton("View Registrations");
        JButton register = new JButton("Register for Event");
        JButton payment  = new JButton("Make Payment");
        JButton stats     = new JButton("Stats");
        JButton viewEvents = new JButton("View Events");

        // --- Layout ---
        add.setBounds(30,  50,  150, 30);
        delete.setBounds(210, 50,  150, 30);

        update.setBounds(30,  100, 150, 30);
        view.setBounds(210, 100, 150, 30);

        register.setBounds(30,  150, 150, 30);
        payment.setBounds(210, 150, 150, 30);

        stats.setBounds(30, 200, 150, 30);
        viewEvents.setBounds(210, 200, 150, 30);

        area = new JTextArea();
        area.setEditable(false);
        area.setBounds(30, 250, 330, 170);

        f.add(add);      f.add(delete);
        f.add(update);   f.add(view);
        f.add(register); f.add(payment);
        f.add(stats);    f.add(viewEvents); f.add(area);

        f.setSize(400, 470);
        f.setLayout(null);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        // ---- ADD EVENT ----
        add.addActionListener(e -> {
            try {
                String name     = JOptionPane.showInputDialog(f, "Event Name:");
                String location = JOptionPane.showInputDialog(f, "Location:");
                String date     = JOptionPane.showInputDialog(f, "Date (YYYY-MM-DD):");
                String feeStr   = JOptionPane.showInputDialog(f, "Fee:");

                if (name == null || location == null || date == null || feeStr == null) return;

                int fee = Integer.parseInt(feeStr);

                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO events(event_name, location, event_date, fee) VALUES (?, ?, ?, ?)"
                );
                ps.setString(1, name);
                ps.setString(2, location);
                ps.setString(3, date);
                ps.setInt(4, fee);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(f, "Event Added!");
                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // ---- DELETE EVENT ----
        delete.addActionListener(e -> {
            try {
                String idStr = JOptionPane.showInputDialog(f, "Enter Event ID to delete:");
                if (idStr == null) return;
                int id = Integer.parseInt(idStr);

                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM events WHERE event_id = ?"
                );
                ps.setInt(1, id);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(f, "Event Deleted!");
                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // ---- UPDATE EVENT ----
        update.addActionListener(e -> {
            try {
                String idStr   = JOptionPane.showInputDialog(f, "Event ID to update:");
                String name    = JOptionPane.showInputDialog(f, "New Event Name:");
                String location = JOptionPane.showInputDialog(f, "New Location:");
                String feeStr  = JOptionPane.showInputDialog(f, "New Fee:");

                if (idStr == null || name == null || location == null || feeStr == null) return;

                int id  = Integer.parseInt(idStr);
                int fee = Integer.parseInt(feeStr);

                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE events SET event_name = ?, location = ?, fee = ? WHERE event_id = ?"
                );
                ps.setString(1, name);
                ps.setString(2, location);
                ps.setInt(3, fee);
                ps.setInt(4, id);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(f, "Event Updated!");
                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // ---- VIEW REGISTRATIONS (JOIN across all 4 tables) ----
        view.addActionListener(e -> {
            try {
                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();

                String sql =
                    "SELECT u.username, e.event_name, r.reg_date, " +
                    "       COALESCE(p.status, 'Not Paid') AS pay_status " +
                    "FROM registrations r " +
                    "JOIN users u ON r.user_id = u.user_id " +
                    "JOIN events e ON r.event_id = e.event_id " +
                    "LEFT JOIN payments p ON p.reg_id = r.reg_id";

                ResultSet rs = stmt.executeQuery(sql);

                area.setText("Username | Event | Reg Date | Payment\n");
                area.append("--------------------------------------------------\n");

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    area.append(
                        rs.getString("username")   + " | " +
                        rs.getString("event_name") + " | " +
                        rs.getString("reg_date")   + " | " +
                        rs.getString("pay_status") + "\n"
                    );
                }

                if (!found) area.append("No registrations found.");
                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // ---- REGISTER FOR EVENT ----
        register.addActionListener(e -> {
            try {
                String userIdStr  = JOptionPane.showInputDialog(f, "User ID:");
                String eventIdStr = JOptionPane.showInputDialog(f, "Event ID:");

                if (userIdStr == null || eventIdStr == null) return;

                int userId  = Integer.parseInt(userIdStr);
                int eventId = Integer.parseInt(eventIdStr);

                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO registrations(user_id, event_id, reg_date) VALUES (?, ?, CURDATE())"
                );
                ps.setInt(1, userId);
                ps.setInt(2, eventId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(f, "Registered Successfully!");
                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // ---- MAKE PAYMENT ----
        payment.addActionListener(e -> {
            try {
                String regIdStr = JOptionPane.showInputDialog(f, "Registration ID (reg_id):");
                String amtStr   = JOptionPane.showInputDialog(f, "Amount:");

                if (regIdStr == null || amtStr == null) return;

                int    regId  = Integer.parseInt(regIdStr);
                double amount = Double.parseDouble(amtStr);

                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO payments(reg_id, amount, status) VALUES (?, ?, 'Paid')"
                );
                ps.setInt(1, regId);
                ps.setDouble(2, amount);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(f, "Payment Recorded!");
                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // ---- VIEW EVENTS ----
        viewEvents.addActionListener(e -> {
            try {
                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT * FROM events");

                area.setText("Event ID | Name | Location | Date | Fee\n");
                area.append("--------------------------------------------------\n");

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    area.append(
                        rs.getInt("event_id")       + " | " +
                        rs.getString("event_name")  + " | " +
                        rs.getString("location")    + " | " +
                        rs.getString("event_date")  + " | " +
                        rs.getInt("fee")            + "\n"
                    );
                }

                if (!found) area.append("No events found.");
                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // ---- STATS ----
        stats.addActionListener(e -> {
            try {
                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();

                ResultSet r1 = stmt.executeQuery("SELECT COUNT(*) FROM events");
                r1.next();
                int totalEvents = r1.getInt(1);

                ResultSet r2 = stmt.executeQuery("SELECT COUNT(*) FROM registrations");
                r2.next();
                int totalRegs = r2.getInt(1);

                ResultSet r3 = stmt.executeQuery("SELECT COUNT(*) FROM payments WHERE status = 'Paid'");
                r3.next();
                int paidCount = r3.getInt(1);

                JOptionPane.showMessageDialog(f,
                    "Total Events      : " + totalEvents + "\n" +
                    "Total Registrations: " + totalRegs  + "\n" +
                    "Payments Done     : " + paidCount
                );

                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });
    }
}
