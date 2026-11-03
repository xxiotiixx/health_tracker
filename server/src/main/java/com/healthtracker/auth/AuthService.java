package com.healthtracker.auth;

import java.util.Optional;

public class AuthService {
    private static final String BAD_CREDENTIALS = "Невірне ім'я або пароль";
    private final UserRepository users;
    private final TokenService tokens;
    private final String dummyHash;

    public AuthService(UserRepository users, TokenService tokens) {
        this.users = users;
        this.tokens = tokens;
        this.dummyHash = PasswordHasher.hash("dummy-password-1");
    }

    public User register(String username, String password) {
        CredentialsValidator.validate(username, password);
        String hash = PasswordHasher.hash(password);
        return users.create(username, hash);
    }
    public String login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            throw new AuthenticationException(BAD_CREDENTIALS);
        }
        Optional<User> found = users.findByUsername(username);
        if (found.isEmpty()) {
            PasswordHasher.verify(password, dummyHash);
            throw new AuthenticationException(BAD_CREDENTIALS);
        }
        User user = found.get();
        if (!PasswordHasher.verify(password, user.passwordHash())) {
            throw new AuthenticationException(BAD_CREDENTIALS);
        }
        return tokens.issue(user.id(), user.username());
    }
}