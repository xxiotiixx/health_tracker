package com.healthtracker.steps;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StepCounterTest {

    @Test
    void sumsEntriesForDay() {
        assertEquals(9000, StepCounter.totalForDay(Arrays.asList(3000, 4000, 2000)));
    }

    @Test
    void emptyDayIsZero() {
        assertEquals(0, StepCounter.totalForDay(Collections.emptyList()));
    }

    @Test
    void rejectsInvalidEntries() {
        assertThrows(IllegalArgumentException.class, () -> StepCounter.totalForDay(null));
        assertThrows(IllegalArgumentException.class,
                () -> StepCounter.totalForDay(Arrays.asList(1000, -1)));
        assertThrows(IllegalArgumentException.class,
                () -> StepCounter.totalForDay(Arrays.asList(1000, 100001)));
        List<Integer> withNull = Arrays.asList(1000, null);
        assertThrows(IllegalArgumentException.class, () -> StepCounter.totalForDay(withNull));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 10000, 0",
            "5000, 10000, 50",
            "7500, 10000, 75",
            "3333, 10000, 33",
            "10000, 10000, 100",
            "12000, 10000, 100"
    })
    void calculatesProgressPercent(int steps, int goal, int expected) {
        assertEquals(expected, StepCounter.progressPercent(steps, goal));
    }

    @Test
    void goalReachedOnlyWhenStepsAtLeastGoal() {
        assertFalse(StepCounter.isGoalReached(9999, 10000));
        assertTrue(StepCounter.isGoalReached(10000, 10000));
        assertTrue(StepCounter.isGoalReached(15000, 10000));
    }

    @Test
    void stepsToGoalIsNeverNegative() {
        assertEquals(4000, StepCounter.stepsToGoal(6000, 10000));
        assertEquals(0, StepCounter.stepsToGoal(10000, 10000));
        assertEquals(0, StepCounter.stepsToGoal(12000, 10000));
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 10000",
            "5000, 0",
            "5000, -100",
            "5000, 100001"
    })
    void rejectsInvalidStepsOrGoal(int steps, int goal) {
        assertThrows(IllegalArgumentException.class, () -> StepCounter.progressPercent(steps, goal));
        assertThrows(IllegalArgumentException.class, () -> StepCounter.isGoalReached(steps, goal));
        assertThrows(IllegalArgumentException.class, () -> StepCounter.stepsToGoal(steps, goal));
    }

    @Test
    void calculatesAveragePerDay() {
        assertEquals(10000, StepCounter.averagePerDay(Arrays.asList(8000, 10000, 12000)));
        assertEquals(2, StepCounter.averagePerDay(Arrays.asList(1, 2)));
    }

    @Test
    void averageOfEmptyListIsZero() {
        assertEquals(0, StepCounter.averagePerDay(Collections.emptyList()));
    }

    @Test
    void averageRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> StepCounter.averagePerDay(null));
        assertThrows(IllegalArgumentException.class,
                () -> StepCounter.averagePerDay(Arrays.asList(1000, -5)));
        List<Integer> withNull = Arrays.asList(1000, null);
        assertThrows(IllegalArgumentException.class, () -> StepCounter.averagePerDay(withNull));
    }

    @Test
    void defaultGoalIsWithinAllowedRange() {
        assertTrue(StepCounter.DEFAULT_GOAL >= StepCounter.MIN_GOAL);
        assertTrue(StepCounter.DEFAULT_GOAL <= StepCounter.MAX_GOAL);
    }
}