import java.sql.*;
import java.util.*;

public class MovieDAO {
    
    // Get all movies
    public static List<Map<String, Object>> getAllMovies() {
        List<Map<String, Object>> movies = new ArrayList<>();
        String sql = "SELECT m.*, g.genre_name, d.director_name FROM movies m " +
                     "LEFT JOIN genres g ON m.genre_id = g.genre_id " +
                     "LEFT JOIN directors d ON m.director_id = d.director_id " +
                     "ORDER BY m.release_year DESC";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> movie = new HashMap<>();
                movie.put("movie_id", rs.getInt("movie_id"));
                movie.put("title", rs.getString("title"));
                movie.put("release_year", rs.getInt("release_year"));
                movie.put("duration_minutes", rs.getInt("duration_minutes"));
                movie.put("language", rs.getString("language"));
                movie.put("average_rating", rs.getDouble("average_rating"));
                movie.put("total_reviews", rs.getInt("total_reviews"));
                movie.put("genre_name", rs.getString("genre_name"));
                movie.put("director_name", rs.getString("director_name"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
    
    // Add new movie
    public static boolean addMovie(String title, int releaseYear, int duration, String language) {
        String sql = "INSERT INTO movies (title, release_year, duration_minutes, language) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setInt(2, releaseYear);
            pstmt.setInt(3, duration);
            pstmt.setString(4, language);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete movie by ID
    public static boolean deleteMovie(int movieId) {
        String sql = "DELETE FROM movies WHERE movie_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update movie by ID
    public static boolean updateMovie(int movieId, String title, int releaseYear, int duration, String language) {
        String sql = "UPDATE movies SET title = ?, release_year = ?, duration_minutes = ?, language = ? WHERE movie_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setInt(2, releaseYear);
            pstmt.setInt(3, duration);
            pstmt.setString(4, language);
            pstmt.setInt(5, movieId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get movie by ID
    public static Map<String, Object> getMovieById(int movieId) {
        String sql = "SELECT * FROM movies WHERE movie_id = ?";
        Map<String, Object> movie = new HashMap<>();
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                movie.put("movie_id", rs.getInt("movie_id"));
                movie.put("title", rs.getString("title"));
                movie.put("release_year", rs.getInt("release_year"));
                movie.put("duration_minutes", rs.getInt("duration_minutes"));
                movie.put("language", rs.getString("language"));
                movie.put("average_rating", rs.getDouble("average_rating"));
                movie.put("total_reviews", rs.getInt("total_reviews"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movie;
    }
    
    // Get top rated movies
    public static List<Map<String, Object>> getTopRatedMovies() {
        List<Map<String, Object>> movies = new ArrayList<>();
        String sql = "SELECT title, average_rating, total_reviews FROM movies WHERE total_reviews > 0 ORDER BY average_rating DESC LIMIT 5";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> movie = new HashMap<>();
                movie.put("title", rs.getString("title"));
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