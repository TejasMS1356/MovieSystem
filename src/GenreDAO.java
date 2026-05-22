import java.sql.*;
import java.util.*;

public class GenreDAO {
    
    // Get all genres
    public static List<Map<String, Object>> getAllGenres() {
        List<Map<String, Object>> genres = new ArrayList<>();
        String sql = "SELECT * FROM genres ORDER BY genre_name";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> genre = new HashMap<>();
                genre.put("genre_id", rs.getInt("genre_id"));
                genre.put("genre_name", rs.getString("genre_name"));
                genre.put("description", rs.getString("description"));
                genres.add(genre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genres;
    }
    
    // Add new genre
    public static boolean addGenre(String name, String description) {
        String sql = "INSERT INTO genres (genre_name, description) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get movies by genre
    public static List<Map<String, Object>> getMoviesByGenre(String genreName) {
        List<Map<String, Object>> movies = new ArrayList<>();
        String sql = "SELECT m.title, m.release_year, m.average_rating, m.total_reviews " +
                     "FROM movies m JOIN genres g ON m.genre_id = g.genre_id " +
                     "WHERE g.genre_name LIKE ? ORDER BY m.average_rating DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, "%" + genreName + "%");
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