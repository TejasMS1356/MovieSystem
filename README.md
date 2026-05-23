# 🎬 Movie Database System - CineVault

A comprehensive movie database management system built with Java, JDBC, and MySQL.

## Features

- ✅ View all movies with ratings and details
- ✅ Add new movies to the database
- ✅ Edit movie information (title, year, duration, language)
- ✅ Delete movies from the database
- ✅ Add reviews and ratings (auto-updates movie ratings)
- ✅ View top-rated movies
- ✅ View actors list
- ✅ Beautiful dark-themed UI with CineVault design

## Tech Stack

- **Backend**: Java, JDBC
- **Database**: MySQL
- **Server**: Built-in Java HTTP Server (com.sun.net.httpserver)
- **Frontend**: HTML5, CSS3, JavaScript
- **API**: RESTful endpoints


## Setup Instructions

### Prerequisites
- Java JDK 11 or higher
- MySQL Server (XAMPP recommended)
- MySQL Connector/J

### Database Setup
1. Create database: `CREATE DATABASE movie_db;`
2. Run the `database.sql` script
3. Update password in `DatabaseConnection.java`

### Run the Application
```bash
cd src
javac -cp ".;..\lib\mysql-connector-j-9.7.0.jar" *.java
java -cp ".;..\lib\mysql-connector-j-9.7.0.jar" SimpleWebServer
