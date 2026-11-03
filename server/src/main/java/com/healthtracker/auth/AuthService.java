package com.healthtracker.auth;

public class AuthService {

    private static final String BAD_CREDENTIALS = "Невірне ім'я або пароль";
    private final JdbcUserRepository users;
    private final TokenService tokens;
    public AuthService(JdbcUserRepository users, TokenService tokens) {
        this.users = users;
        this.tokens = tokens;
    }
    public User register(String username, String password) {
        CredentialsValidator.validate(username, password);
        return users.create(username, PasswordHasher.hash(password));
    }
    public String login(String username, String password) {
        User user = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(BAD_CREDENTIALS));
        if (!PasswordHasher.verify(password, user.passwordHash())) {
            throw new IllegalArgumentException(BAD_CREDENTIALS);
        }
        return tokens.issue(user.id(), user.username());
    }
}