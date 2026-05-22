import java.sql.*;
import java.util.*;

public class ReviewDAO {
    
    // Get all reviews
    public static List<Map<String, Object>> getAllReviews() {
        List<Map<String, Object>> reviews = new ArrayList<>();
        String sql = "SELECT r.*, m.title as movie_title FROM reviews r " +
                     "JOIN movies m ON r.movie_id = m.movie_id " +
                     "ORDER BY r.review_date DESC LIMIT 20";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> review = new HashMap<>();
                review.put("review_id", rs.getInt("review_id"));
                review.put("movie_title", rs.getString("movie_title"));
                review.put("reviewer_name", rs.getString("reviewer_name"));
                review.put("rating", rs.getDouble("rating"));
                review.put("review_text", rs.getString("review_text"));
                review.put("review_date", rs.getTimestamp("review_date"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }
    
    // Add new review
    public static boolean addReview(int movieId, String reviewerName, double rating, String reviewText) {
        String sql = "INSERT INTO reviews (movie_id, reviewer_name, rating, review_text) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setString(2, reviewerName);
            pstmt.setDouble(3, rating);
            pstmt.setString(4, reviewText);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}