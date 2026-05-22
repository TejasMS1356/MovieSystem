import java.sql.*;

public class TestPassword {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/movie_db";
        String user = "root";
        String password = "55555";
        
        System.out.println("Testing MySQL connection with password: '55555'");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ SUCCESS! Password '55555' is CORRECT!");
            
            // Test if database exists
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW DATABASES");
            System.out.println("\nDatabases available:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1));
            }
            
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ FAILED! Password '55555' is WRONG!");
            System.out.println("Error: " + e.getMessage());
        }
    }
}