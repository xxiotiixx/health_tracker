package com.healthtracker.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;

public final class PasswordHasher {

    private static final int COST = 10;

    private PasswordHasher() {
    }
    public static String hash(String password) {
        CredentialsValidator.validatePassword(password);
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray());
    }
    public static boolean verify(String password, String hash) {
        if (password == null || hash == null || hash.isEmpty()) {
            return false;
        }
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }
}