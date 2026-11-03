package com.healthtracker.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenServiceTest {
    private static final String SECRET = "test-secret-key-for-unit-tests-0123456789";
    private static final String OTHER_SECRET = "another-secret-key-for-unit-tests-9876543210";

    private final TokenService service = new TokenService(SECRET, Duration.ofHours(1));

    @Test
    void issuedTokenIsVerifiedAndContainsUserData() {
        String token = service.issue(42, "alice_01");
        AuthenticatedUser user = service.verify(token);
        assertEquals(42, user.userId());
        assertEquals("alice_01", user.username());
    }

    @Test
    void tokenHasThreeParts() {
        String token = service.issue(1, "alice_01");
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void tokenDoesNotContainSecret() {
        String token = service.issue(1, "alice_01");
        assertEquals(false, token.contains(SECRET));
    }

    @Test
    void rejectsExpiredToken() {
        Clock past = Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.UTC);
        TokenService oldService = new TokenService(SECRET, Duration.ofHours(1), past);
        String expired = oldService.issue(1, "alice_01");
        assertThrows(InvalidTokenException.class, () -> service.verify(expired));
    }

    @Test
    void rejectsTokenSignedWithOtherSecret() {
        TokenService other = new TokenService(OTHER_SECRET, Duration.ofHours(1));
        String foreign = other.issue(1, "alice_01");
        assertThrows(InvalidTokenException.class, () -> service.verify(foreign));
    }

    @Test
    void rejectsTokenWithSubstitutedPayload() {
        String alice = service.issue(1, "alice_01");
        String bob = service.issue(2, "bob_02");
        String[] a = alice.split("\\.");
        String[] b = bob.split("\\.");
        String forged = a[0] + "." + b[1] + "." + a[2];   // чужі дані з підписом alice
        assertNotEquals(alice, forged);
        assertThrows(InvalidTokenException.class, () -> service.verify(forged));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "garbage", "a.b.c", "not.a.token", "eyJhbGciOiJub25lIn0.e30."})
    void rejectsMalformedTokens(String token) {
        assertThrows(InvalidTokenException.class, () -> service.verify(token));
    }

    @Test
    void rejectsWeakOrMissingSecret() {
        assertThrows(IllegalArgumentException.class,
                () -> new TokenService("short", Duration.ofHours(1)));
        assertThrows(IllegalArgumentException.class,
                () -> new TokenService(null, Duration.ofHours(1)));
    }

    @Test
    void rejectsNonPositiveLifetime() {
        assertThrows(IllegalArgumentException.class,
                () -> new TokenService(SECRET, Duration.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new TokenService(SECRET, Duration.ofMinutes(-5)));
        assertThrows(IllegalArgumentException.class,
                () -> new TokenService(SECRET, null));
    }

    @Test
    void rejectsInvalidUserDataOnIssue() {
        assertThrows(IllegalArgumentException.class, () -> service.issue(0, "alice_01"));
        assertThrows(IllegalArgumentException.class, () -> service.issue(-3, "alice_01"));
        assertThrows(IllegalArgumentException.class, () -> service.issue(1, null));
        assertThrows(IllegalArgumentException.class, () -> service.issue(1, "  "));
    }
}