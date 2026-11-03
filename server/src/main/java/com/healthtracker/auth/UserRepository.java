package com.healthtracker.auth;
import java.util.Optional;

public interface UserRepository {
    User create(String username, String passwordHash);
    Optional<User> findByUsername(String username);
}