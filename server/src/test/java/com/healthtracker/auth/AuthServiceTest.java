package com.healthtracker.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceTest {
    private static final String SECRET = "test-secret-key-for-unit-tests-0123456789";
    static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> users = new HashMap<>();
        private final AtomicLong ids = new AtomicLong(1);

        @Override
        public User create(String username, String passwordHash) {
            if (users.containsKey(username)) {
                throw new DuplicateUsernameException(username);
            }
            User user = new User(ids.getAndIncrement(), username, passwordHash);
            users.put(username, user);
            return user;
        }

        @Override
        public Optional<User> findByUsername(String username) {
            return Optional.ofNullable(users.get(username));
        }

        int size() {
            return users.size();
        }
    }

    private InMemoryUserRepository repository;
    private TokenService tokens;
    private AuthService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        tokens = new TokenService(SECRET, Duration.ofHours(1));
        service = new AuthService(repository, tokens);
    }

    @Test
    void registerStoresHashInsteadOfPassword() {
        User user = service.register("alice_01", "password1");
        assertNotEquals("password1", user.passwordHash());
        assertTrue(PasswordHasher.verify("password1", user.passwordHash()));
    }

    @Test
    void registerRejectsDuplicateUsername() {
        service.register("alice_01", "password1");
        assertThrows(DuplicateUsernameException.class,
                () -> service.register("alice_01", "password2"));
        assertEquals(1, repository.size());
    }

    @Test
    void registerRejectsInvalidDataAndStoresNothing() {
        assertThrows(IllegalArgumentException.class, () -> service.register("ab", "password1"));
        assertThrows(IllegalArgumentException.class, () -> service.register("alice_01", "short"));
        assertEquals(0, repository.size());
    }

    @Test
    void loginReturnsValidToken() {
        User user = service.register("alice_01", "password1");
        String token = service.login("alice_01", "password1");
        AuthenticatedUser authenticated = tokens.verify(token);
        assertEquals(user.id(), authenticated.userId());
        assertEquals("alice_01", authenticated.username());
    }

    @Test
    void loginRejectsWrongPassword() {
        service.register("alice_01", "password1");
        assertThrows(AuthenticationException.class, () -> service.login("alice_01", "password2"));
    }

    @Test
    void loginRejectsUnknownUser() {
        assertThrows(AuthenticationException.class, () -> service.login("nobody_1", "password1"));
    }

    @Test
    void errorMessageDoesNotRevealWhetherUserExists() {
        service.register("alice_01", "password1");
        AuthenticationException wrongPassword = assertThrows(AuthenticationException.class,
                () -> service.login("alice_01", "password2"));
        AuthenticationException unknownUser = assertThrows(AuthenticationException.class,
                () -> service.login("nobody_1", "password2"));
        assertEquals(wrongPassword.getMessage(), unknownUser.getMessage());
    }

    @Test
    void loginRejectsEmptyCredentials() {
        assertThrows(AuthenticationException.class, () -> service.login(null, "password1"));
        assertThrows(AuthenticationException.class, () -> service.login("alice_01", null));
        assertThrows(AuthenticationException.class, () -> service.login("  ", "password1"));
        assertThrows(AuthenticationException.class, () -> service.login("alice_01", ""));
    }
}