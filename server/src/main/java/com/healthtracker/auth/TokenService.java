package com.healthtracker.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public final class TokenService {
    public static final String ISSUER = "health-tracker";
    public static final int MIN_SECRET_LENGTH = 32;
    public static final Duration DEFAULT_TTL = Duration.ofHours(24);

    private static final String USERNAME_CLAIM = "username";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final Duration ttl;
    private final Clock clock;

    public TokenService(String secret, Duration ttl) {
        this(secret, ttl, Clock.systemUTC());
    }


    public TokenService(String secret, Duration ttl, Clock clock) {
        if (secret == null || secret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalArgumentException(
                    "Секрет має містити щонайменше " + MIN_SECRET_LENGTH + " символів");
        }
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("Термін дії токена має бути додатним");
        }
        if (clock == null) {
            throw new IllegalArgumentException("Годинник не вказано");
        }
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        this.ttl = ttl;
        this.clock = clock;
    }
    public static TokenService fromEnvironment() {
        return new TokenService(System.getenv("JWT_SECRET"), DEFAULT_TTL);
    }

    public String issue(long userId, String username) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Некоректний ідентифікатор користувача");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Ім'я користувача не вказано");
        }
        Instant now = clock.instant();
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(String.valueOf(userId))
                .withClaim(USERNAME_CLAIM, username)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(ttl)))
                .sign(algorithm);
    }

    public AuthenticatedUser verify(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Токен не вказано");
        }
        try {
            DecodedJWT jwt = verifier.verify(token);
            long userId = Long.parseLong(jwt.getSubject());
            String username = jwt.getClaim(USERNAME_CLAIM).asString();
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("У токені немає імені користувача");
            }
            return new AuthenticatedUser(userId, username);
        } catch (JWTVerificationException | NumberFormatException e) {
            throw new IllegalArgumentException("Недійсний токен", e);
        }
    }
}