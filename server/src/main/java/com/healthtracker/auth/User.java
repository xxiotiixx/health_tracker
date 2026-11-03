package com.healthtracker.auth;

public record User(long id, String username, String passwordHash) {
}