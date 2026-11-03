package com.healthtracker.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CredentialsValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"abc", "user_01", "Anna", "a_b_c_d"})
    void acceptsValidUsernames(String username) {
        assertDoesNotThrow(() -> CredentialsValidator.validateUsername(username));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "ab", "user name", "user-1", "імя123", "user@mail"})
    void rejectsInvalidUsernames(String username) {
        assertThrows(IllegalArgumentException.class,
                () -> CredentialsValidator.validateUsername(username));
    }

    @Test
    void rejectsTooLongUsername() {
        String tooLong = "a".repeat(CredentialsValidator.MAX_USERNAME_LENGTH + 1);
        assertThrows(IllegalArgumentException.class,
                () -> CredentialsValidator.validateUsername(tooLong));
        String maxLength = "a".repeat(CredentialsValidator.MAX_USERNAME_LENGTH);
        assertDoesNotThrow(() -> CredentialsValidator.validateUsername(maxLength));
    }

    @ParameterizedTest
    @ValueSource(strings = {"password1", "Abcdefg1", "12345678a", "Пароль123"})
    void acceptsValidPasswords(String password) {
        assertDoesNotThrow(() -> CredentialsValidator.validatePassword(password));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"        ", "short1", "onlyletters", "12345678"})
    void rejectsInvalidPasswords(String password) {
        assertThrows(IllegalArgumentException.class,
                () -> CredentialsValidator.validatePassword(password));
    }

    @Test
    void rejectsPasswordLongerThanBcryptLimit() {
        String tooLong = "a1" + "b".repeat(CredentialsValidator.MAX_PASSWORD_BYTES);
        assertThrows(IllegalArgumentException.class,
                () -> CredentialsValidator.validatePassword(tooLong));
    }

    @Test
    void validateChecksBothFields() {
        assertDoesNotThrow(() -> CredentialsValidator.validate("user_01", "password1"));
        assertThrows(IllegalArgumentException.class,
                () -> CredentialsValidator.validate("ab", "password1"));
        assertThrows(IllegalArgumentException.class,
                () -> CredentialsValidator.validate("user_01", "short"));
    }
}