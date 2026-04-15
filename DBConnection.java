import java.sql.*;

public class DBConnection {

    static final String URL = "jdbc:mysql://localhost:3306/club_db?useSSL=false&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "dbms";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Loaded ✅");

            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connected to DB ✅");

            return conn;

        } catch (Exception e) {
            System.out.println("DB ERROR BELOW 👇");
            e.printStackTrace();   // THIS IS KEY
            return null;
        }
    }
}