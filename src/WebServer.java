import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.sql.*;

public class WebServer {
    private static final int PORT = 8080;
    
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // API endpoints
        server.createContext("/api/movies", new MoviesHandler());
        server.createContext("/api/movies/add", new AddMovieHandler());
        server.createContext("/api/movies/delete", new DeleteMovieHandler());
        server.createContext("/api/movies/update", new UpdateMovieHandler());
        server.createContext("/api/movies/get", new GetMovieHandler());
        server.createContext("/api/reviews/add", new AddReviewHandler());
        server.createContext("/api/top-rated", new TopRatedHandler());
        server.createContext("/api/actors", new ActorsHandler());
        
        // Serve HTML file
        server.createContext("/", new RootHandler());
        
        server.setExecutor(null);
        server.start();
        System.out.println("=========================================");
        System.out.println("✅ SERVER RUNNING on http://localhost:" + PORT);
        System.out.println("=========================================");
    }
    
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = getHTML();
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, html.length());
            exchange.getResponseBody().write(html.getBytes());
            exchange.getResponseBody().close();
        }
    }
    
    static class MoviesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = getMoviesJSON();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            exchange.getResponseBody().write(json.getBytes());
            exchange.getResponseBody().close();
        }
    }
    
    static class AddMovieHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                String title = getParam(body, "title");
                int year = Integer.parseInt(getParam(body, "year"));
                int duration = Integer.parseInt(getParam(body, "duration"));
                String language = getParam(body, "language");
                
                boolean success = addMovieToDB(title, year, duration, language);
                String response = "{\"success\": " + success + "}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }
            exchange.getResponseBody().close();
        }
    }
    
    static class DeleteMovieHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                int movieId = Integer.parseInt(getParam(body, "movie_id"));
                
                boolean success = deleteMovieFromDB(movieId);
                String response = "{\"success\": " + success + "}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }
            exchange.getResponseBody().close();
        }
    }
    
    static class UpdateMovieHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                int movieId = Integer.parseInt(getParam(body, "movie_id"));
                String title = getParam(body, "title");
                int year = Integer.parseInt(getParam(body, "year"));
                int duration = Integer.parseInt(getParam(body, "duration"));
                String language = getParam(body, "language");
                
                boolean success = updateMovieInDB(movieId, title, year, duration, language);
                String response = "{\"success\": " + success + "}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }
            exchange.getResponseBody().close();
        }
    }
    
    static class GetMovieHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            int movieId = Integer.parseInt(query.split("=")[1]);
            
            String json = getMovieJSON(movieId);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            exchange.getResponseBody().write(json.getBytes());
            exchange.getResponseBody().close();
        }
    }
    
    static class AddReviewHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                int movieId = Integer.parseInt(getParam(body, "movie_id"));
                String reviewer = getParam(body, "reviewer");
                double rating = Double.parseDouble(getParam(body, "rating"));
                String review = getParam(body, "review");
                
                boolean success = addReviewToDB(movieId, reviewer, rating, review);
                String response = "{\"success\": " + success + "}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }
            exchange.getResponseBody().close();
        }
    }
    
    static class TopRatedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = getTopRatedJSON();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            exchange.getResponseBody().write(json.getBytes());
            exchange.getResponseBody().close();
        }
    }
    
    static class ActorsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = getActorsJSON();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            exchange.getResponseBody().write(json.getBytes());
            exchange.getResponseBody().close();
        }
    }
    
    // Database helper methods
    private static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/movie_db", "root", "555555");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static String getMoviesJSON() {
        StringBuilder json = new StringBuilder("[");
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM movies ORDER BY release_year DESC");
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                json.append("{");
                json.append("\"movie_id\":").append(rs.getInt("movie_id")).append(",");
                json.append("\"title\":\"").append(rs.getString("title").replace("\"", "\\\"")).append("\",");
                json.append("\"release_year\":").append(rs.getInt("release_year")).append(",");
                json.append("\"duration_minutes\":").append(rs.getInt("duration_minutes")).append(",");
                json.append("\"language\":\"").append(rs.getString("language") != null ? rs.getString("language").replace("\"", "\\\"") : "N/A").append("\",");
                json.append("\"average_rating\":").append(rs.getDouble("average_rating")).append(",");
                json.append("\"total_reviews\":").append(rs.getInt("total_reviews"));
                json.append("}");
                first = false;
            }
        } catch (Exception e) {
            return "[{\"title\":\"Error: " + e.getMessage() + "\",\"release_year\":0}]";
        }
        json.append("]");
        return json.toString();
    }
    
    private static boolean addMovieToDB(String title, int year, int duration, String language) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO movies (title, release_year, duration_minutes, language) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setInt(2, year);
            pstmt.setInt(3, duration);
            pstmt.setString(4, language);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean deleteMovieFromDB(int movieId) {
        try (Connection conn = getConnection()) {
            String sql = "DELETE FROM movies WHERE movie_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, movieId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean updateMovieInDB(int movieId, String title, int year, int duration, String language) {
        try (Connection conn = getConnection()) {
            String sql = "UPDATE movies SET title=?, release_year=?, duration_minutes=?, language=? WHERE movie_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setInt(2, year);
            pstmt.setInt(3, duration);
            pstmt.setString(4, language);
            pstmt.setInt(5, movieId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static String getMovieJSON(int movieId) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM movies WHERE movie_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return "{" +
                    "\"movie_id\":" + rs.getInt("movie_id") + "," +
                    "\"title\":\"" + rs.getString("title").replace("\"", "\\\"") + "\"," +
                    "\"release_year\":" + rs.getInt("release_year") + "," +
                    "\"duration_minutes\":" + rs.getInt("duration_minutes") + "," +
                    "\"language\":\"" + (rs.getString("language") != null ? rs.getString("language").replace("\"", "\\\"") : "N/A") + "\"" +
                "}";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }
    
    private static boolean addReviewToDB(int movieId, String reviewer, double rating, String review) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO reviews (movie_id, reviewer_name, rating, review_text) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, movieId);
            pstmt.setString(2, reviewer);
            pstmt.setDouble(3, rating);
            pstmt.setString(4, review);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static String getTopRatedJSON() {
        StringBuilder json = new StringBuilder("[");
        try (Connection conn = getConnection()) {
            String sql = "SELECT title, average_rating, total_reviews FROM movies WHERE total_reviews > 0 ORDER BY average_rating DESC LIMIT 5";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                json.append("{");
                json.append("\"title\":\"").append(rs.getString("title").replace("\"", "\\\"")).append("\",");
                json.append("\"average_rating\":").append(rs.getDouble("average_rating")).append(",");
                json.append("\"total_reviews\":").append(rs.getInt("total_reviews"));
                json.append("}");
                first = false;
            }
        } catch (Exception e) {
            return "[]";
        }
        json.append("]");
        return json.toString();
    }
    
    private static String getActorsJSON() {
        StringBuilder json = new StringBuilder("[");
        try (Connection conn = getConnection()) {
            String sql = "SELECT actor_id, actor_name, nationality, date_of_birth FROM actors ORDER BY actor_name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                json.append("{");
                json.append("\"actor_id\":").append(rs.getInt("actor_id")).append(",");
                json.append("\"actor_name\":\"").append(rs.getString("actor_name").replace("\"", "\\\"")).append("\",");
                json.append("\"nationality\":\"").append(rs.getString("nationality") != null ? rs.getString("nationality").replace("\"", "\\\"") : "N/A").append("\",");
                json.append("\"date_of_birth\":\"").append(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth") : "N/A").append("\"");
                json.append("}");
                first = false;
            }
        } catch (Exception e) {
            return "[]";
        }
        json.append("]");
        return json.toString();
    }
    
    private static String getParam(String body, String key) {
        for (String param : body.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1 && pair[0].equals(key)) {
                return java.net.URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            }
        }
        return "";
    }
    
    private static String getHTML() {
        return "<!DOCTYPE html><html><head><title>CineVault</title><style>" +
            "body{font-family:Arial;background:#0d0d0f;color:#f5f3ee;padding:20px}" +
            ".container{max-width:1200px;margin:auto}" +
            ".movie-card{background:#16161e;border:1px solid rgba(255,255,255,0.07);border-radius:16px;padding:20px;margin:15px 0}" +
            "button{background:#c9a84c;color:#0d0d0f;border:none;padding:8px 16px;margin:5px;cursor:pointer;border-radius:8px}" +
            ".delete{background:#c0392b;color:white}" +
            "</style></head><body>" +
            "<div class='container'><h1>🎬 CineVault</h1>" +
            "<div id='movies'></div>" +
            "<h2>Add Movie</h2>" +
            "<input type='text' id='title' placeholder='Title'><br>" +
            "<input type='number' id='year' placeholder='Year'><br>" +
            "<input type='number' id='duration' placeholder='Duration'><br>" +
            "<input type='text' id='language' placeholder='Language'><br>" +
            "<button onclick='addMovie()'>Add Movie</button>" +
            "<div id='message'></div></div>" +
            "<script>" +
            "async function loadMovies(){" +
            "const res=await fetch('/api/movies');const movies=await res.json();" +
            "document.getElementById('movies').innerHTML=movies.map(m=>" +
            "'<div class=movie-card><h3>'+m.title+' ('+m.release_year+')</h3>" +
            "<p>Language: '+m.language+'</p><p>Rating: '+m.average_rating+'/10</p>" +
            "<button onclick=\"deleteMovie('+m.movie_id+')\" class=delete>Delete</button></div>').join('');}" +
            "async function addMovie(){" +
            "const data=new URLSearchParams({title:document.getElementById('title').value,year:document.getElementById('year').value,duration:document.getElementById('duration').value,language:document.getElementById('language').value});" +
            "const res=await fetch('/api/movies/add',{method:'POST',body:data});const result=await res.json();" +
            "if(result.success){alert('Movie added!');loadMovies();}else alert('Error');}" +
            "async function deleteMovie(id){" +
            "if(confirm('Delete?')){const data=new URLSearchParams({movie_id:id});" +
            "await fetch('/api/movies/delete',{method:'POST',body:data});loadMovies();}}" +
            "loadMovies();" +
            "</script></body></html>";
    }
}