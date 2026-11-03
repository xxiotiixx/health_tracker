package com.healthtracker.integration;

import com.healthtracker.auth.AuthService;
import com.healthtracker.auth.AuthenticatedUser;
import com.healthtracker.auth.JdbcUserRepository;
import com.healthtracker.auth.TokenService;
import com.healthtracker.auth.User;
import com.healthtracker.calories.ActivityLevel;
import com.healthtracker.calories.Gender;
import com.healthtracker.db.Database;
import com.healthtracker.profile.JdbcProfileRepository;
import com.healthtracker.profile.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine");
    static final Database DATABASE;

    static {
        POSTGRES.start();
        DATABASE = new Database(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
        try (Connection c = DATABASE.getConnection(); Statement s = c.createStatement()) {
            // Та сама схема, що й у docker compose
            s.execute(Files.readString(Path.of("../infra/init.sql")));
        } catch (Exception e) {
            throw new IllegalStateException("Не вдалося застосувати infra/init.sql", e);
        }
    }

    private final JdbcUserRepository users = new JdbcUserRepository(DATABASE);
    private final JdbcProfileRepository profiles = new JdbcProfileRepository(DATABASE);
    private final TokenService tokens = new TokenService(
            "integration-secret-key-0123456789-abcdef", Duration.ofHours(1));
    private final AuthService auth = new AuthService(users, tokens);

    @BeforeEach
    void cleanTables() throws Exception {
        try (Connection c = DATABASE.getConnection(); Statement s = c.createStatement()) {
            s.execute("TRUNCATE users RESTART IDENTITY CASCADE");
        }
    }

    @Test
    void userIsSavedAndFoundInDatabase() {
        User created = users.create("alice_01", "hash-value");
        assertEquals(created, users.findByUsername("alice_01").orElseThrow());
        assertTrue(users.findByUsername("nobody_1").isEmpty());
    }

    @Test
    void databaseRejectsDuplicateUsername() {
        users.create("alice_01", "hash-1");
        assertThrows(IllegalArgumentException.class, () -> users.create("alice_01", "hash-2"));
    }

    @Test
    void profileIsSavedUpdatedAndDeletedWithUser() throws Exception {
        User user = users.create("alice_01", "hash");
        LocalDate born = LocalDate.of(2000, 6, 15);

        Profile first = new Profile(user.id(), 175, 70, born, Gender.MALE, ActivityLevel.LIGHT);
        profiles.save(first);
        assertEquals(first, profiles.findByUserId(user.id()).orElseThrow());

        profiles.save(new Profile(user.id(), 175, 68, born, Gender.MALE, ActivityLevel.ACTIVE));
        assertEquals(68, profiles.findByUserId(user.id()).orElseThrow().weightKg(), 0.001);

        try (Connection c = DATABASE.getConnection(); Statement s = c.createStatement()) {
            s.executeUpdate("DELETE FROM users WHERE id = " + user.id());
        }
        assertTrue(profiles.findByUserId(user.id()).isEmpty());   // ON DELETE CASCADE
    }

    @Test
    void registerLoginAndPasswordStoredAsHash() throws Exception {
        User user = auth.register("alice_01", "password1");
        AuthenticatedUser authenticated = tokens.verify(auth.login("alice_01", "password1"));
        assertEquals(user.id(), authenticated.userId());
        assertThrows(IllegalArgumentException.class, () -> auth.login("alice_01", "wrong-pass1"));

        try (Connection c = DATABASE.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT password_hash FROM users WHERE username = 'alice_01'")) {
            assertTrue(rs.next());
            assertNotEquals("password1", rs.getString(1));
        }
    }

    @Test
    void sqlInjectionStringIsTreatedAsPlainText() {
        users.create("alice_01", "hash");
        assertTrue(users.findByUsername("' OR '1'='1").isEmpty());
    }
}