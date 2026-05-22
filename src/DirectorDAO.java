import java.sql.*;
import java.util.*;

public class DirectorDAO {
    
    // Get all directors
    public static List<Map<String, Object>> getAllDirectors() {
        List<Map<String, Object>> directors = new ArrayList<>();
        String sql = "SELECT * FROM directors ORDER BY director_name";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> director = new HashMap<>();
                director.put("director_id", rs.getInt("director_id"));
                director.put("director_name", rs.getString("director_name"));
                director.put("date_of_birth", rs.getDate("date_of_birth"));
                director.put("nationality", rs.getString("nationality"));
                directors.add(director);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return directors;
    }
    
    // Add new director
    public static boolean addDirector(String name, String dob, String nationality) {
        String sql = "INSERT INTO directors (director_name, date_of_birth, nationality) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, dob.isEmpty() ? null : dob);
            pstmt.setString(3, nationality.isEmpty() ? null : nationality);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get movies by director
    public static List<Map<String, Object>> getMoviesByDirector(int directorId) {
        List<Map<String, Object>> movies = new ArrayList<>();
        String sql = "SELECT title, release_year, average_rating, total_reviews " +
                     "FROM movies WHERE director_id = ? ORDER BY release_year DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, directorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> movie = new HashMap<>();
                movie.put("title", rs.getString("title"));
                movie.put("release_year", rs.getInt("release_year"));
                movie.put("average_rating", rs.getDouble("average_rating"));
                movie.put("total_reviews", rs.getInt("total_reviews"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
}