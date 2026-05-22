import java.sql.*;
import java.util.*;

public class ActorDAO {
    
    // Get all actors
    public static List<Map<String, Object>> getAllActors() {
        List<Map<String, Object>> actors = new ArrayList<>();
        String sql = "SELECT * FROM actors ORDER BY actor_name";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> actor = new HashMap<>();
                actor.put("actor_id", rs.getInt("actor_id"));
                actor.put("actor_name", rs.getString("actor_name"));
                actor.put("date_of_birth", rs.getDate("date_of_birth"));
                actor.put("nationality", rs.getString("nationality"));
                actors.add(actor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actors;
    }
    
    // Add new actor
    public static boolean addActor(String name, String dob, String nationality) {
        String sql = "INSERT INTO actors (actor_name, date_of_birth, nationality) VALUES (?, ?, ?)";
        
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
    
    // Link actor to movie
    public static boolean linkActorToMovie(int movieId, int actorId, String role) {
        String sql = "INSERT INTO movie_actors (movie_id, actor_id, role_name) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, actorId);
            pstmt.setString(3, role);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get movies by actor
    public static List<Map<String, Object>> getMoviesByActor(String actorName) {
        List<Map<String, Object>> movies = new ArrayList<>();
        String sql = "SELECT m.title, m.release_year, m.average_rating, ma.role_name " +
                     "FROM movies m " +
                     "JOIN movie_actors ma ON m.movie_id = ma.movie_id " +
                     "JOIN actors a ON ma.actor_id = a.actor_id " +
                     "WHERE a.actor_name LIKE ? ORDER BY m.release_year DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, "%" + actorName + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> movie = new HashMap<>();
                movie.put("title", rs.getString("title"));
                movie.put("release_year", rs.getInt("release_year"));
                movie.put("average_rating", rs.getDouble("average_rating"));
                movie.put("role_name", rs.getString("role_name"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
}