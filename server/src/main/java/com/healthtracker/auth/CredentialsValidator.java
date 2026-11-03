package com.healthtracker.auth;

import java.nio.charset.StandardCharsets;

public final class CredentialsValidator {

    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_BYTES = 72;

    private CredentialsValidator() {
    }
    public static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Ім'я не вказано");
        }
        if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Ім'я має містити " + MIN_USERNAME_LENGTH + "-" + MAX_USERNAME_LENGTH + " символів");
        }
        if (!username.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException(
                    "Ім'я може містити лише латиницю, цифри та підкреслення");
        }
    }
    public static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Пароль не вказано");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Пароль має містити щонайменше " + MIN_PASSWORD_LENGTH + " символів");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new IllegalArgumentException("Пароль занадто довгий");
        }
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        if (!hasLetter || !hasDigit) {
            throw new IllegalArgumentException("Пароль має містити літеру та цифру");
        }
    }

    public static void validate(String username, String password) {
        validateUsername(username);
        validatePassword(password);
    }
}
