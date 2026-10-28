package com.healthtracker.bmi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BmiCalculatorTest {

    @Test
    void calculatesTypicalValue() {
        assertEquals(22.9, BmiCalculator.calculate(70, 175), 0.001);
    }

    @Test
    void roundsToOneDecimal() {
        assertEquals(24.7, BmiCalculator.calculate(80, 180), 0.001);
    }

    @ParameterizedTest
    @CsvSource({
            "15.4, UNDERWEIGHT",
            "18.4, UNDERWEIGHT",
            "18.5, NORMAL",
            "24.9, NORMAL",
            "25.0, OVERWEIGHT",
            "29.9, OVERWEIGHT",
            "30.0, OBESE",
            "40.0, OBESE"
    })
    void classifiesByWhoBoundaries(double bmi, BmiCategory expected) {
        assertEquals(expected, BmiCalculator.classify(bmi));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 175",
            "-70, 175",
            "10, 175",
            "600, 175",
            "70, 0",
            "70, -175",
            "70, 30",
            "70, 300"
    })
    void rejectsOutOfRangeInput(double weight, double height) {
        assertThrows(IllegalArgumentException.class,
                () -> BmiCalculator.calculate(weight, height));
    }

    @Test
    void rejectsNaN() {
        assertThrows(IllegalArgumentException.class,
                () -> BmiCalculator.calculate(Double.NaN, 175));
        assertThrows(IllegalArgumentException.class,
                () -> BmiCalculator.calculate(70, Double.NaN));
    }
}