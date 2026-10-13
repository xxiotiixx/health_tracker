package com.healthtracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BmiCalculatorTest {

    @Test
    void calculatesBmi() {
        assertEquals(22.86, BmiCalculator.calculate(70, 175), 0.01);
    }

    @Test
    void rejectsNonPositiveValues() {
        assertThrows(IllegalArgumentException.class, () -> BmiCalculator.calculate(0, 175));
    }
}