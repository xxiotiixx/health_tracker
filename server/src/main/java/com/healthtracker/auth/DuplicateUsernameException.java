package com.healthtracker.auth;

public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException(String username) {
        super("Користувач з таким ім'ям уже існує: " + username);
    }
}