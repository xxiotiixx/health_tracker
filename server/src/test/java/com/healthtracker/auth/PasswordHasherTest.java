package com.healthtracker.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    @Test
    void hashIsNotThePlainPassword() {
        String hash = PasswordHasher.hash("password1");
        assertNotEquals("password1", hash);
        assertTrue(hash.startsWith("$2"));
    }

    @Test
    void sameHashFitsDatabaseColumn() {
        // password_hash VARCHAR(255)
        assertTrue(PasswordHasher.hash("password1").length() <= 255);
    }

    @Test
    void sameDifferentPasswordsAndSaltsGiveDifferentHashes() {
        String first = PasswordHasher.hash("password1");
        String second = PasswordHasher.hash("password1");
        assertNotEquals(first, second);   // кожного разу інша сіль
    }

    @Test
    void verifiesCorrectPassword() {
        String hash = PasswordHasher.hash("password1");
        assertTrue(PasswordHasher.verify("password1", hash));
    }

    @Test
    void rejectsWrongPassword() {
        String hash = PasswordHasher.hash("password1");
        assertFalse(PasswordHasher.verify("password2", hash));
        assertFalse(PasswordHasher.verify("Password1", hash));
    }

    @Test
    void verifyHandlesNullAndEmpty() {
        assertFalse(PasswordHasher.verify(null, "hash"));
        assertFalse(PasswordHasher.verify("password1", null));
        assertFalse(PasswordHasher.verify("password1", ""));
    }

    @Test
    void hashRejectsWeakPassword() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hash("short"));
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hash(null));
    }
}