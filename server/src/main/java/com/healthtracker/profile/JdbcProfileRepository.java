package com.healthtracker.profile;

import com.healthtracker.calories.ActivityLevel;
import com.healthtracker.calories.Gender;
import com.healthtracker.db.DataAccessException;
import com.healthtracker.db.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcProfileRepository implements ProfileRepository {
    private final Database database;
    public JdbcProfileRepository(Database database) {
        this.database = database;
    }
    @Override
    public void save(Profile profile) {
        String sql = "INSERT INTO profiles "
                + "(user_id, height_cm, weight_kg, birth_date, gender, activity_level) "
                + "VALUES (?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT (user_id) DO UPDATE SET "
                + "height_cm = EXCLUDED.height_cm, weight_kg = EXCLUDED.weight_kg, "
                + "birth_date = EXCLUDED.birth_date, gender = EXCLUDED.gender, "
                + "activity_level = EXCLUDED.activity_level";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, profile.userId());
            statement.setDouble(2, profile.heightCm());
            statement.setDouble(3, profile.weightKg());
            statement.setDate(4, Date.valueOf(profile.birthDate()));
            statement.setString(5, profile.gender().name());
            statement.setString(6, profile.activity().name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Не вдалося зберегти профіль", e);
        }
    }
    @Override
    public Optional<Profile> findByUserId(long userId) {
        String sql = "SELECT user_id, height_cm, weight_kg, birth_date, gender, activity_level "
                + "FROM profiles WHERE user_id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Profile(
                            rs.getLong("user_id"),
                            rs.getDouble("height_cm"),
                            rs.getDouble("weight_kg"),
                            rs.getDate("birth_date").toLocalDate(),
                            Gender.valueOf(rs.getString("gender")),
                            ActivityLevel.valueOf(rs.getString("activity_level"))));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Не вдалося прочитати профіль", e);
        }
    }
}