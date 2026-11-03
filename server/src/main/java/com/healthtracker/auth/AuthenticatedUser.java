package com.healthtracker.auth;

/** Дані користувача, отримані з перевіреного токена. */
public record AuthenticatedUser(long userId, String username) {
}