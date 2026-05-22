public class Main {
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("Movie Database System - Java JDBC Application");
        System.out.println("=========================================");
        System.out.println();
        
        // Test database connection
        if (DatabaseConnection.getConnection() != null) {
            System.out.println("✓ Database connection successful!");
            System.out.println();
            System.out.println("Starting web server...");
            System.out.println();
            
            try {
                WebServer.main(args);
            } catch (Exception e) {
                System.out.println("Error starting server: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("✗ Failed to connect to database!");
            System.out.println("Please check:");
            System.out.println("1. MySQL is running");
            System.out.println("2. Database 'movie_db' exists");
            System.out.println("3. Username/password in DatabaseConnection.java is correct");
        }
    }
}