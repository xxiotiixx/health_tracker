package com.healthtracker.profile;

import com.healthtracker.calories.ActivityLevel;
import com.healthtracker.calories.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileServiceTest {

    private static final LocalDate BORN = LocalDate.of(2000, 6, 15);

    static class InMemoryProfileRepository implements ProfileRepository {
        final Map<Long, Profile> profiles = new HashMap<>();

        @Override
        public void save(Profile profile) {
            profiles.put(profile.userId(), profile);
        }

        @Override
        public Optional<Profile> findByUserId(long userId) {
            return Optional.ofNullable(profiles.get(userId));
        }
    }

    private InMemoryProfileRepository repository;
    private ProfileService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProfileRepository();
        Clock clock = Clock.fixed(Instant.parse("2026-06-15T10:00:00Z"), ZoneOffset.UTC);
        service = new ProfileService(repository, clock);
    }

    @Test
    void savesAndReadsProfile() {
        Profile saved = service.save(1, 175, 70, BORN, Gender.MALE, ActivityLevel.MODERATE);
        assertEquals(saved, service.get(1).orElseThrow());
    }

    @Test
    void secondSaveOverwritesProfile() {
        service.save(1, 175, 70, BORN, Gender.MALE, ActivityLevel.MODERATE);
        service.save(1, 175, 68, BORN, Gender.MALE, ActivityLevel.ACTIVE);
        assertEquals(68, service.get(1).orElseThrow().weightKg());
        assertEquals(1, repository.profiles.size());
    }

    @Test
    void invalidProfileIsNotSaved() {
        assertThrows(IllegalArgumentException.class,
                () -> service.save(1, 10, 70, BORN, Gender.MALE, ActivityLevel.MODERATE));
        assertThrows(IllegalArgumentException.class,
                () -> service.save(1, 175, 70, null, Gender.MALE, ActivityLevel.MODERATE));
        assertTrue(repository.profiles.isEmpty());
    }

    @Test
    void rejectsInvalidUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> service.save(0, 175, 70, BORN, Gender.MALE, ActivityLevel.MODERATE));
    }

    @Test
    void missingProfileIsEmpty() {
        assertTrue(service.get(99).isEmpty());
    }
}