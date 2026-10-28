package com.healthtracker.calories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalorieCalculatorTest {

    // 10*70 + 6.25*175 - 5*30 + 5 = 1648.75
    @Test
    void bmrForMale() {
        assertEquals(1649, CalorieCalculator.calculateBmr(Gender.MALE, 70, 175, 30));
    }

    // 10*60 + 6.25*165 - 5*25 - 161 = 1345.25
    @Test
    void bmrForFemale() {
        assertEquals(1345, CalorieCalculator.calculateBmr(Gender.FEMALE, 60, 165, 25));
    }

    @Test
    void dailyNormMaleModerate() {
        // 1648.75 * 1.55 = 2555.56
        assertEquals(2556,
                CalorieCalculator.calculateDailyNorm(Gender.MALE, 70, 175, 30, ActivityLevel.MODERATE));
    }

    @Test
    void dailyNormFemaleSedentary() {
        // 1345.25 * 1.2 = 1614.3
        assertEquals(1614,
                CalorieCalculator.calculateDailyNorm(Gender.FEMALE, 60, 165, 25, ActivityLevel.SEDENTARY));
    }

    @Test
    void higherActivityGivesHigherNorm() {
        int low = CalorieCalculator.calculateDailyNorm(Gender.MALE, 70, 175, 30, ActivityLevel.SEDENTARY);
        int high = CalorieCalculator.calculateDailyNorm(Gender.MALE, 70, 175, 30, ActivityLevel.VERY_ACTIVE);
        assertEquals(true, high > low);
    }

    @Test
    void rejectsNullGenderAndActivity() {
        assertThrows(IllegalArgumentException.class,
                () -> CalorieCalculator.calculateBmr(null, 70, 175, 30));
        assertThrows(IllegalArgumentException.class,
                () -> CalorieCalculator.calculateDailyNorm(Gender.MALE, 70, 175, 30, null));
    }

    @ParameterizedTest
    @CsvSource({
            "10, 175, 30",
            "600, 175, 30",
            "70, 30, 30",
            "70, 300, 30",
            "70, 175, 9",
            "70, 175, 121"
    })
    void rejectsOutOfRangeParameters(double weight, double height, int age) {
        assertThrows(IllegalArgumentException.class,
                () -> CalorieCalculator.calculateBmr(Gender.MALE, weight, height, age));
    }

    @Test
    void sumsEntriesForDay() {
        assertEquals(1500, CalorieCalculator.totalForDay(Arrays.asList(400, 700, 400)));
    }

    @Test
    void emptyDayIsZero() {
        assertEquals(0, CalorieCalculator.totalForDay(Collections.emptyList()));
    }

    @Test
    void rejectsInvalidEntries() {
        assertThrows(IllegalArgumentException.class,
                () -> CalorieCalculator.totalForDay(null));
        assertThrows(IllegalArgumentException.class,
                () -> CalorieCalculator.totalForDay(Arrays.asList(500, -1)));
        assertThrows(IllegalArgumentException.class,
                () -> CalorieCalculator.totalForDay(Arrays.asList(500, 20001)));
        List<Integer> withNull = Arrays.asList(500, null);
        assertThrows(IllegalArgumentException.class,
                () -> CalorieCalculator.totalForDay(withNull));
    }

    @Test
    void remainingCaloriesCanBeNegative() {
        assertEquals(500, CalorieCalculator.remaining(2000, 1500));
        assertEquals(-300, CalorieCalculator.remaining(2000, 2300));
    }

    @Test
    void remainingRejectsNegativeValues() {
        assertThrows(IllegalArgumentException.class, () -> CalorieCalculator.remaining(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> CalorieCalculator.remaining(2000, -5));
    }
}