package com.healthtracker.auth;

import com.healthtracker.db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcUserRepository{

    private static final String UNIQUE_VIOLATION = "23505";

    private final Database database;

    public JdbcUserRepository(Database database) {
        this.database = database;
    }
    public User create(String username, String passwordHash) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?) RETURNING id";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return new User(rs.getLong(1), username, passwordHash);
            }
        } catch (SQLException e) {
            if (UNIQUE_VIOLATION.equals(e.getSQLState())) {
                throw new IllegalArgumentException("Користувач з таким ім'ям уже існує");
            }
            throw new IllegalStateException("Не вдалося зберегти користувача", e);
        }
    }
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash FROM users WHERE username = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getLong("id"), rs.getString("username"), rs.getString("password_hash")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Не вдалося знайти користувача", e);
        }
    }
}