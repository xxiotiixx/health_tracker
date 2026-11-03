package com.healthtracker.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private final String url;
    private final String user;
    private final String password;

    public Database(String url, String user, String password) {
        if (url == null || url.isBlank() || user == null || user.isBlank() || password == null) {
            throw new IllegalArgumentException("Параметри підключення до бази не вказано");
        }
        this.url = url;
        this.user = user;
        this.password = password;
    }
    public static Database fromEnvironment() {
        String url = System.getenv().getOrDefault(
                "DB_URL", "jdbc:postgresql://localhost:5432/health_tracker");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        if (user == null || password == null) {
            throw new IllegalStateException("Змінні оточення DB_USER і DB_PASSWORD не задано");
        }
        return new Database(url, user, password);
    }
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}