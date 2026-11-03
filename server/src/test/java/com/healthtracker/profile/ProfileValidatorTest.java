package com.healthtracker.profile;

import com.healthtracker.calories.ActivityLevel;
import com.healthtracker.calories.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProfileValidatorTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 6, 15);
    private static final LocalDate BORN_2000 = LocalDate.of(2000, 6, 15);

    @Test
    void returnsAgeInFullYears() {
        assertEquals(26, ProfileValidator.validate(175, 70, BORN_2000,
                Gender.MALE, ActivityLevel.MODERATE, TODAY));
    }

    @Test
    void ageIsNotIncrementedBeforeBirthday() {
        LocalDate birthdayTomorrow = LocalDate.of(2000, 6, 16);
        assertEquals(25, ProfileValidator.validate(175, 70, birthdayTomorrow,
                Gender.FEMALE, ActivityLevel.LIGHT, TODAY));
    }

    @ParameterizedTest
    @CsvSource({
            "10, 70",
            "300, 70",
            "175, 5",
            "175, 600"
    })
    void rejectsOutOfRangeBodyParameters(double height, double weight) {
        assertThrows(IllegalArgumentException.class, () -> ProfileValidator.validate(
                height, weight, BORN_2000, Gender.MALE, ActivityLevel.MODERATE, TODAY));
    }

    @Test
    void rejectsMissingFields() {
        assertThrows(IllegalArgumentException.class, () -> ProfileValidator.validate(
                175, 70, BORN_2000, null, ActivityLevel.MODERATE, TODAY));
        assertThrows(IllegalArgumentException.class, () -> ProfileValidator.validate(
                175, 70, BORN_2000, Gender.MALE, null, TODAY));
        assertThrows(IllegalArgumentException.class, () -> ProfileValidator.validate(
                175, 70, null, Gender.MALE, ActivityLevel.MODERATE, TODAY));
    }

    @Test
    void rejectsBirthDateInFuture() {
        assertThrows(IllegalArgumentException.class, () -> ProfileValidator.validate(
                175, 70, LocalDate.of(2030, 1, 1), Gender.MALE, ActivityLevel.MODERATE, TODAY));
    }

    @Test
    void rejectsTooYoungAndTooOld() {
        assertThrows(IllegalArgumentException.class, () -> ProfileValidator.validate(
                175, 70, LocalDate.of(2020, 1, 1), Gender.MALE, ActivityLevel.MODERATE, TODAY));
        assertThrows(IllegalArgumentException.class, () -> ProfileValidator.validate(
                175, 70, LocalDate.of(1890, 1, 1), Gender.MALE, ActivityLevel.MODERATE, TODAY));
    }
}