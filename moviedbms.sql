CREATE DATABASE IF NOT EXISTS movie_db;
USE movie_db;

CREATE TABLE genres (
    genre_id INT PRIMARY KEY AUTO_INCREMENT,
    genre_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE directors (
    director_id INT PRIMARY KEY AUTO_INCREMENT,
    director_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    nationality VARCHAR(50)
);

CREATE TABLE movies (
    movie_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    release_year INT,
    duration_minutes INT,
    language VARCHAR(50),
    genre_id INT,
    director_id INT,
    average_rating DECIMAL(3,1) DEFAULT 0,
    total_reviews INT DEFAULT 0,
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id) ON DELETE SET NULL,
    FOREIGN KEY (director_id) REFERENCES directors(director_id) ON DELETE SET NULL
);

CREATE TABLE actors (
    actor_id INT PRIMARY KEY AUTO_INCREMENT,
    actor_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    nationality VARCHAR(50)
);
CREATE TABLE movie_actors (
    movie_id INT,
    actor_id INT,
    role_name VARCHAR(100),
    PRIMARY KEY (movie_id, actor_id),
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE,
    FOREIGN KEY (actor_id) REFERENCES actors(actor_id) ON DELETE CASCADE
);

CREATE TABLE reviews (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    movie_id INT NOT NULL,
    reviewer_name VARCHAR(100) NOT NULL,
    rating DECIMAL(3,1) CHECK (rating >= 0 AND rating <= 10),
    review_text TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE
);

DELIMITER //
CREATE TRIGGER update_movie_rating
AFTER INSERT ON reviews
FOR EACH ROW
BEGIN
    UPDATE movies 
    SET average_rating = (SELECT AVG(rating) FROM reviews WHERE movie_id = NEW.movie_id),
        total_reviews = (SELECT COUNT(*) FROM reviews WHERE movie_id = NEW.movie_id)
    WHERE movie_id = NEW.movie_id;
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER update_movie_rating_delete
AFTER DELETE ON reviews
FOR EACH ROW
BEGIN
    UPDATE movies 
    SET average_rating = COALESCE((SELECT AVG(rating) FROM reviews WHERE movie_id = OLD.movie_id), 0),
        total_reviews = (SELECT COUNT(*) FROM reviews WHERE movie_id = OLD.movie_id)
    WHERE movie_id = OLD.movie_id;
END//
DELIMITER ;


CREATE VIEW top_rated_movies AS
SELECT m.title, m.release_year, m.average_rating, m.total_reviews, g.genre_name, d.director_name
FROM movies m
LEFT JOIN genres g ON m.genre_id = g.genre_id
LEFT JOIN directors d ON m.director_id = d.director_id
WHERE m.total_reviews > 0
ORDER BY m.average_rating DESC;

INSERT INTO genres (genre_name, description) VALUES
('Action', 'High-energy films with stunts and battles'),
('Drama', 'Emotional and character-driven stories'),
('Comedy', 'Humorous and entertaining films'),
('Sci-Fi', 'Science fiction and futuristic concepts'),
('Horror', 'Scary and suspenseful films'),
('Romance', 'Love and relationship stories'),
('Thriller', 'Suspenseful and exciting films');


INSERT INTO directors (director_name, date_of_birth, nationality) VALUES
('Christopher Nolan', '1970-07-30', 'British-American'),
('Steven Spielberg', '1946-12-18', 'American'),
('James Cameron', '1954-08-16', 'Canadian'),
('Greta Gerwig', '1983-08-04', 'American');


INSERT INTO movies (title, release_year, duration_minutes, language, genre_id, director_id) VALUES
('Inception', 2010, 148, 'English', 4, 1),
('The Dark Knight', 2008, 152, 'English', 1, 1),
('Jurassic Park', 1993, 127, 'English', 1, 2),
('Titanic', 1997, 194, 'English', 6, 3),
('Little Women', 2019, 135, 'English', 6, 4),
('Interstellar', 2014, 169, 'English', 4, 1);


INSERT INTO actors (actor_name, date_of_birth, nationality) VALUES
('Leonardo DiCaprio', '1974-11-11', 'American'),
('Christian Bale', '1974-01-30', 'British'),
('Sam Neill', '1947-09-14', 'New Zealander'),
('Kate Winslet', '1975-10-05', 'British'),
('Saoirse Ronan', '1994-04-12', 'Irish'),
('Matthew McConaughey', '1969-11-04', 'American');


INSERT INTO movie_actors (movie_id, actor_id, role_name) VALUES
(1, 1, 'Dom Cobb'),
(2, 2, 'Bruce Wayne'),
(3, 3, 'Dr. Alan Grant'),
(4, 1, 'Jack Dawson'),
(4, 4, 'Rose DeWitt'),
(5, 5, 'Jo March'),
(6, 6, 'Cooper');


INSERT INTO reviews (movie_id, reviewer_name, rating, review_text) VALUES
(1, 'John Doe', 9.5, 'Mind-blowing masterpiece!'),
(1, 'Jane Smith', 9.0, 'Incredible visuals and story'),
(2, 'John Doe', 9.8, 'Best superhero movie ever'),
(3, 'Alice Brown', 8.5, 'Revolutionary for its time'),
(4, 'Bob Wilson', 8.5, 'Epic love story'),
(5, 'Carol Davis', 8.0, 'Beautiful adaptation'),
(6, 'Jane Smith', 9.2, 'Emotional sci-fi masterpiece');

select *from movies;